package net.laffeymyth.proxychat.chat;

import com.velocitypowered.api.command.CommandSource;
import dev.rollczi.litecommands.programmatic.LiteCommand;
import net.kyori.adventure.text.Component;

public interface ChatService {
    void sendMessage(String playerName, Component playerDisplayName, String text);

    void registerListener();

    LiteCommand<CommandSource> createCommand();
}
