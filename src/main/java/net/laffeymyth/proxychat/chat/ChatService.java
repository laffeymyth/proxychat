package net.laffeymyth.proxychat.chat;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.programmatic.LiteCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.laffeymyth.localization.commons.service.ComponentLocalizationService;
import net.laffeymyth.localization.commons.util.ComponentResolver;
import net.laffeymyth.proxychat.ProxyChat;
import net.laffeymyth.proxychat.chat.dto.ChatMessageDto;
import net.laffeymyth.proxychat.chat.dto.ChatSetting;
import net.laffeymyth.proxychat.util.PrefixUtil;
import net.luckperms.api.LuckPerms;
import org.redisson.api.RedissonClient;

import java.util.List;


public class ChatService {
    private final ComponentLocalizationService lang = ComponentLocalizationService.lang();
    private final RedissonClient redissonClient;
    private final ProxyServer proxyServer;
    private final ChatSettingService chatSettingService;
    private final LuckPerms luckPerms;
    private final String topicName;
    private final String permission;
    private final String command;
    private final List<String> aliases;

    public ChatService(ProxyChat proxyChat, String topicName, String permission, String command, List<String> aliases) {
        this.redissonClient = proxyChat.getRedissonClient();
        this.proxyServer = proxyChat.getProxy();
        this.chatSettingService = proxyChat.getChatSettingService();
        this.luckPerms = proxyChat.getLuckPerms();
        this.topicName = topicName;
        this.permission = permission;
        this.command = command;
        this.aliases = aliases;
    }

    public void sendMessage(String playerName, Component playerDisplayName, String text) {
        redissonClient.getTopic(topicName).publish(ChatMessageDto.create(playerDisplayName, playerName, text));
    }

    public void registerListener() {
        redissonClient.getTopic(topicName).addListener(ChatMessageDto.class, (channel, msg) -> {
            Component displayName = GsonComponentSerializer.gson().deserialize(msg.getDisplayNameJson());

            broadcast(displayName, msg.getText());
        });
    }

    public LiteCommand<CommandSource> createCommand() {
        return new ChatCommand(command, aliases, permission, this, luckPerms, chatSettingService, topicName);
    }

    private void broadcast(Component playerDisplayName, String text) {
        proxyServer.getAllPlayers().forEach(player -> {
            if (!player.hasPermission(permission)) {
                return;
            }

            ChatSetting chatSetting = chatSettingService.getChatSettingCacheMap().get(player);

            if (chatSetting == null || !chatSetting.isEnabledChat(topicName)) {
                return;
            }

            Component component = lang.getMessage(topicName + "_message", "ru",
                    ComponentResolver.tag("player", (argumentQueue, context) -> playerDisplayName),
                    ComponentResolver.tag("message", (argumentQueue, context) -> Component.text(text)),
                    PrefixUtil.PREFIX_RESOLVER.apply(topicName)
            );

            player.sendMessage(component);
        });
    }
}
