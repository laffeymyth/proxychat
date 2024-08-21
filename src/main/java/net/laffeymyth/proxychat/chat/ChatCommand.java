package net.laffeymyth.proxychat.chat;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.programmatic.LiteCommand;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.laffeymyth.localization.commons.service.ComponentLocalizationService;
import net.laffeymyth.proxychat.chat.dto.ChatSetting;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;

import java.util.List;

import static net.laffeymyth.proxychat.util.PrefixUtil.PREFIX_RESOLVER;

public class ChatCommand extends LiteCommand<CommandSource> {
    private final ComponentLocalizationService lang = ComponentLocalizationService.lang();
    private final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.legacyAmpersand();
    private final String topicName;

    public ChatCommand(String name,
                       List<String> aliases, String permission,
                       ChatService chatService,
                       LuckPerms luckPerms,
                       ChatSettingService chatSettingService,
                       String topicName) {
        super(name, aliases);
        this.topicName = topicName;

        permissions(permission);

        execute(context -> {
            CommandSource commandSource = context.invocation().sender();

            if (!(commandSource instanceof Player player)) {
                return;
            }

            String text = context.argumentJoin("text");

            ChatSetting chatSetting = chatSettingService.getChatSettingCacheMap().get(player);

            if (text.equals("on") || text.equals("off")) {
                if (chatSetting == null) {
                    throw new RuntimeException("chatSetting is null");
                }

                changeChatState(chatSetting, player);
                return;
            }

            if (!chatSetting.isEnabledChat(topicName)) {
                player.sendMessage(lang.getMessage(topicName + "_error_disabled", "ru", PREFIX_RESOLVER.apply(topicName)));
                return;
            }

            User user = luckPerms.getUserManager().getUser(player.getUsername());

            if (user == null) {
                throw new RuntimeException("user is null");
            }

            String prefix = user.getCachedData().getMetaData().getPrefix();

            if (prefix == null) {
                prefix = "&7";
            }

            chatService.sendMessage(
                    player.getUsername(),
                    legacyComponentSerializer.deserialize(prefix + player.getUsername()),
                    text);
        });
    }

    private void changeChatState(ChatSetting chatSetting, Player player) {
        if (chatSetting.isEnabledChat(topicName)) {
            chatSetting.getEnabledChats().remove(topicName);
            player.sendMessage(lang.getMessage(topicName + "_off", "ru", PREFIX_RESOLVER.apply(topicName)));
        } else {
            chatSetting.getEnabledChats().add(topicName);
            player.sendMessage(lang.getMessage(topicName + "_on", "ru", PREFIX_RESOLVER.apply(topicName)));
        }
    }
}
