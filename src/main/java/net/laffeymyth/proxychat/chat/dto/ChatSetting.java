package net.laffeymyth.proxychat.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatSetting {
    private Set<String> enabledChats = new HashSet<>();

    public boolean isEnabledChat(String chat) {
        return enabledChats.contains(chat);
    }
}
