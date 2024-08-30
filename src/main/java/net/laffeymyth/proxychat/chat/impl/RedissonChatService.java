package net.laffeymyth.proxychat.chat.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.laffeymyth.proxychat.ProxyChat;
import net.laffeymyth.proxychat.chat.BaseChatService;
import net.laffeymyth.proxychat.chat.ChatService;
import net.laffeymyth.proxychat.chat.dto.ChatMessageDto;
import net.laffeymyth.proxychat.displayname.DisplayNameService;
import org.redisson.api.RedissonClient;

import java.util.Set;


public class RedissonChatService extends BaseChatService implements ChatService {
    private final RedissonClient redissonClient;
    private final String topicName;

    public RedissonChatService(ProxyChat proxyChat, String topicName, String permission, String command, Set<String> aliases, Set<String> subcommands, DisplayNameService displayNameService) {
        super(proxyChat, topicName, permission, command, aliases, subcommands, displayNameService);
        this.redissonClient = proxyChat.getRedissonClient();
        this.topicName = topicName;
    }

    @Override
    public void sendMessage(String playerName, Component playerDisplayName, String text) {
        redissonClient.getTopic(topicName).publish(ChatMessageDto.create(playerDisplayName, playerName, text));
    }

    @Override
    public void registerListener() {
        redissonClient.getTopic(topicName).addListener(ChatMessageDto.class, (channel, msg) -> {
            Component displayName = GsonComponentSerializer.gson().deserialize(msg.getDisplayNameJson());

            broadcast(displayName, msg.getText());
        });
    }
}
