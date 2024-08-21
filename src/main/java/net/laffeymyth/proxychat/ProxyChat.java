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
import net.laffeymyth.proxychat.chat.ChatService;
import net.laffeymyth.proxychat.chat.ChatSettingService;
import net.laffeymyth.proxychat.factory.CommandsFactory;
import net.laffeymyth.proxychat.factory.LocalizationFactory;
import net.laffeymyth.proxychat.factory.RedissonFactory;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
@Plugin(
        id = "proxychat",
        name = "proxychat",
        version = "1.0-SNAPSHOT",
        dependencies = {@Dependency(id = "luckperms")}
)
public class ProxyChat {

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

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        initServices();

        List<LiteCommand<CommandSource>> commandList = new ArrayList<>();

        createChat("donate_chat", "proxychat.donatechat", "donatechat", List.of("dc"), commandList);
        createChat("player_chat", "proxychat.playerchat", "playerchat", List.of("pc"), commandList);
        createChat("staff_chat", "proxychat.staffchat", "staffchat", List.of("sc"), commandList);

        liteCommands = new CommandsFactory(proxy, commandList).create();
    }

    private void initServices() {
        luckPerms = LuckPermsProvider.get();
        redissonClient = RedissonFactory.create();
        LocalizationFactory.initLocalization();
        chatSettingService = new ChatSettingService(redissonClient.getMap("chat_setting_map"));
        proxy.getEventManager().register(this, chatSettingService);
    }

    private void createChat(String topicName, String permission, String command, List<String> aliases, List<LiteCommand<CommandSource>> commandList) {
        chatService = new ChatService(this, topicName, permission, command, aliases);
        chatService.registerListener();

        commandList.add(chatService.createCommand());
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        redissonClient.shutdown();
    }
}
