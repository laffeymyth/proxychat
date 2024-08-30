package net.laffeymyth.proxychat.chat.impl;

import net.kyori.adventure.text.Component;
import net.laffeymyth.proxychat.ProxyChat;
import net.laffeymyth.proxychat.chat.BaseChatService;
import net.laffeymyth.proxychat.chat.ChatService;
import net.laffeymyth.proxychat.delay.DelayService;
import net.laffeymyth.proxychat.displayname.DisplayNameService;

import java.util.Set;

public class SingleChatService extends BaseChatService implements ChatService {
    public SingleChatService(ProxyChat proxyChat, String topicName, String permission, String command, Set<String> aliases, Set<String> subcommands, DisplayNameService displayNameService, DelayService delayService) {
        super(proxyChat, topicName, permission, command, aliases, subcommands, displayNameService, delayService);
    }

    @Override
    public void sendMessage(String playerName, Component playerDisplayName, String text) {
        broadcast(playerDisplayName, text);
    }
}
