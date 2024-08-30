package net.laffeymyth.proxychat.displayname;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class DefaultDisplayNameService implements DisplayNameService {
    private final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.legacyAmpersand();

    @Override
    public Component getDisplayName(String chatName, Player player) {
        return legacyComponentSerializer.deserialize("&7" + player.getUsername());
    }
}
