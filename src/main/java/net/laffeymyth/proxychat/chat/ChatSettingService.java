package net.laffeymyth.proxychat.chat;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.laffeymyth.proxychat.chat.dto.ChatSetting;
import org.redisson.api.RMap;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ChatSettingService {
    @Getter
    private final Map<Player, ChatSetting> chatSettingCacheMap = new HashMap<>();
    private final RMap<String, ChatSetting> chatSettingMap;

    @Subscribe
    public void onLogin(LoginEvent event) {
        Player player = event.getPlayer();
        chatSettingCacheMap.remove(player);

        ChatSetting chatSetting = chatSettingMap.get(player.getUsername().toLowerCase());

        if (chatSetting == null) {
            chatSetting = new ChatSetting();
        }

        chatSettingCacheMap.put(event.getPlayer(), chatSetting);
    }

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        ChatSetting chatSetting = chatSettingCacheMap.remove(event.getPlayer());

        if (chatSetting == null) {
            return;
        }

        if (isDefault(chatSetting)) { //оптимизация
            return;
        }

        chatSettingMap.put(event.getPlayer().getUsername().toLowerCase(), chatSetting);
    }

    private boolean isDefault(ChatSetting chatSetting) {
        return chatSetting.getEnabledChats().isEmpty();
    }
}
