package net.laffeymyth.proxychat;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.programmatic.LiteCommand;
import lombok.Getter;
import net.laffeymyth.localization.commons.service.PlainLocalizationService;
import net.laffeymyth.proxychat.chat.ChatService;
import net.laffeymyth.proxychat.chat.ChatSettingService;
import net.laffeymyth.proxychat.chat.impl.RedissonChatService;
import net.laffeymyth.proxychat.chat.impl.SingleChatService;
import net.laffeymyth.proxychat.delay.DelayService;
import net.laffeymyth.proxychat.delay.impl.DelayServiceImpl;
import net.laffeymyth.proxychat.displayname.DefaultDisplayNameService;
import net.laffeymyth.proxychat.displayname.DisplayNameService;
import net.laffeymyth.proxychat.displayname.LuckPermsDisplayNameService;
import net.laffeymyth.proxychat.factory.CommandsFactory;
import net.laffeymyth.proxychat.factory.LocalizationFactory;
import net.laffeymyth.proxychat.factory.RedissonFactory;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Plugin(
        id = "proxychat",
        name = "proxychat",
        version = "1.0-SNAPSHOT",
        dependencies = {@Dependency(id = "luckperms", optional = true)}
)
public class ProxyChat {
    private final PlainLocalizationService lang = PlainLocalizationService.lang();
    @Inject
    private ProxyServer proxy;
    @Inject
    private Logger logger;
    @Inject
    private @DataDirectory Path dataDirectory;
    private RedissonClient redissonClient;
    private LiteCommands<CommandSource> liteCommands;
    private ChatService chatService;
    private ChatSettingService chatSettingService;
    private LuckPerms luckPerms;
    private DisplayNameService displayNameService;
    private DelayService delayService;
    private boolean singleMod = false;

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        initServices();

        List<LiteCommand<CommandSource>> commandList = new ArrayList<>();

        createChat("donate_chat", "proxychat.donatechat", "donatechat", Set.of("dc"), commandList);
        createChat("player_chat", "proxychat.playerchat", "playerchat", Set.of("pc"), commandList);
        createChat("staff_chat", "proxychat.staffchat", "staffchat", Set.of("sc"), commandList);

        liteCommands = new CommandsFactory(proxy, commandList).create();
    }

    private void initServices() {
        LocalizationFactory.initLocalization();

        boolean luckPermsSupport = Boolean.parseBoolean(lang.getMessage("config_luck_perms", "ru"));
        this.singleMod = Boolean.parseBoolean(lang.getMessage("single_mod", "ru"));

        if (luckPermsSupport) {
            luckPerms = LuckPermsProvider.get();
            displayNameService = new LuckPermsDisplayNameService(luckPerms);
        } else {
            displayNameService = new DefaultDisplayNameService();
        }

        redissonClient = RedissonFactory.create();
        delayService = new DelayServiceImpl(redissonClient.getMapCache("delay_map"));
        chatSettingService = new ChatSettingService(redissonClient.getMap("chat_setting_map"));
        proxy.getEventManager().register(this, chatSettingService);
    }

    private void createChat(String topicName, String permission, String command, Set<String> aliases, List<LiteCommand<CommandSource>> commandList) {
        if (singleMod) {
            chatService = new RedissonChatService(this, topicName, permission, command, aliases, new HashSet<>(lang.getMessageList("config_change_setting_sub_commands", "ru")), displayNameService, delayService);
        } else {
            chatService = new SingleChatService(this, topicName, permission, command, aliases, new HashSet<>(lang.getMessageList("config_change_setting_sub_commands", "ru")), displayNameService, delayService);
        }

        chatService.registerListener();

        commandList.add(chatService.createCommand());
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        redissonClient.shutdown();
    }
}
