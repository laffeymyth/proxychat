package net.laffeymyth.proxychat.displayname;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.laffeymyth.localization.commons.service.PlainLocalizationService;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuckPermsDisplayNameService implements DisplayNameService {
    private static final Logger log = LoggerFactory.getLogger(LuckPermsDisplayNameService.class);
    private final LuckPerms luckPerms;
    private final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.legacyAmpersand();
    private final PlainLocalizationService plainLocalizationService = PlainLocalizationService.lang();

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public LuckPermsDisplayNameService(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    @Override
    public Component getDisplayName(String chatName, Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUsername());

        if (user == null) {
            throw new RuntimeException("user is null");
        }

        String prefix = user.getCachedData().getMetaData().getPrefix();
        String suffix = user.getCachedData().getMetaData().getSuffix();

        if (prefix == null) {
            prefix = "&7";
        }

        if (suffix == null) {
            suffix = "&7";
        }

        String format = plainLocalizationService.getMessage("donate_chat_display_name_format", "ru")
                .replace("<prefix>", miniMessage.serialize(legacyComponentSerializer.deserialize(prefix)))
                .replace("<player_name>", player.getUsername())
                .replace("<suffix>", miniMessage.serialize(legacyComponentSerializer.deserialize(suffix)));

        log.info(format);

        return miniMessage.deserialize(format);
    }
}
