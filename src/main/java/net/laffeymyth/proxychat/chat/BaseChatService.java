package net.laffeymyth.proxychat.chat;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.programmatic.LiteCommand;
import net.kyori.adventure.text.Component;
import net.laffeymyth.localization.commons.service.ComponentLocalizationService;
import net.laffeymyth.localization.commons.util.ComponentResolver;
import net.laffeymyth.proxychat.ProxyChat;
import net.laffeymyth.proxychat.chat.dto.ChatSetting;
import net.laffeymyth.proxychat.delay.DelayService;
import net.laffeymyth.proxychat.displayname.DisplayNameService;
import net.laffeymyth.proxychat.util.PrefixUtil;

import java.util.Set;

public abstract class BaseChatService implements ChatService{
    private final ComponentLocalizationService lang = ComponentLocalizationService.lang();
    private final ProxyServer proxyServer;
    private final ChatSettingService chatSettingService;
    private final String topicName;
    private final String permission;
    private final String command;
    private final Set<String> aliases;
    private final Set<String> subcommands;
    private final DisplayNameService displayNameService;
    private final DelayService delayService;

    public BaseChatService(ProxyChat proxyChat, String topicName, String permission, String command, Set<String> aliases, Set<String> subcommands, DisplayNameService displayNameService, DelayService delayService) {
        this.proxyServer = proxyChat.getProxy();
        this.chatSettingService = proxyChat.getChatSettingService();
        this.topicName = topicName;
        this.permission = permission;
        this.command = command;
        this.aliases = aliases;
        this.subcommands = subcommands;
        this.displayNameService = displayNameService;
        this.delayService = delayService;
    }

    @Override
    public LiteCommand<CommandSource> createCommand() {
        return new ChatCommand(command, aliases, permission, this, chatSettingService, topicName, displayNameService, subcommands, delayService);
    }

    @Override
    public void registerListener() {

    }

    protected void broadcast(Component displayName, String text) {
        proxyServer.getAllPlayers().forEach(player -> {
            if (!player.hasPermission(permission)) {
                return;
            }

            ChatSetting chatSetting = chatSettingService.getChatSettingCacheMap().get(player);

            if (chatSetting == null || !chatSetting.isEnabledChat(topicName)) {
                return;
            }

            Component component = lang.getMessage(topicName + "_message", "ru",
                    ComponentResolver.tag("player", (argumentQueue, context) -> displayName),
                    ComponentResolver.tag("message", (argumentQueue, context) -> Component.text(text)),
                    PrefixUtil.PREFIX_RESOLVER.apply(topicName)
            );

            player.sendMessage(component);
        });
    }
}
