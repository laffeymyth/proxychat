package net.laffeymyth.proxychat.displayname;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

public interface DisplayNameService {
    Component getDisplayName(String chatName, Player player);
}
