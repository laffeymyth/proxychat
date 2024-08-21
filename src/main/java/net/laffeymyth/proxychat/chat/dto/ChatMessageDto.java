package net.laffeymyth.proxychat.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private String displayNameJson;
    private String playerName;
    private String text;

    public static ChatMessageDto create(Component displayName, String playerName, String text) {
        return new ChatMessageDto(GsonComponentSerializer.gson().serialize(displayName), playerName, text);
    }
}
