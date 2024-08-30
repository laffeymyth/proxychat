package net.laffeymyth.proxychat.chat;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.programmatic.LiteCommand;
import net.laffeymyth.localization.commons.service.ComponentLocalizationService;
import net.laffeymyth.proxychat.chat.dto.ChatSetting;
import net.laffeymyth.proxychat.displayname.DisplayNameService;

import java.util.Set;

import static net.laffeymyth.proxychat.util.PrefixUtil.PREFIX_RESOLVER;

public class ChatCommand extends LiteCommand<CommandSource> {
    private final ComponentLocalizationService lang = ComponentLocalizationService.lang();
    private final String topicName;

    public ChatCommand(String name,
                       Set<String> aliases,
                       String permission,
                       ChatService chatService,
                       ChatSettingService chatSettingService,
                       String topicName, DisplayNameService displayNameService, Set<String> changeSettingSubcommand) {
        super(name, aliases.stream().toList());
        this.topicName = topicName;

        permissions(permission);

        argumentJoin("text");

        execute(context -> {
            CommandSource commandSource = context.invocation().sender();

            if (!(commandSource instanceof Player player)) {
                return;
            }

            String text = context.argumentJoin("text");

            ChatSetting chatSetting = chatSettingService.getChatSettingCacheMap().get(player);

            if (changeSettingSubcommand.contains(text)) {
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

            chatService.sendMessage(
                    player.getUsername(),
                    displayNameService.getDisplayName(topicName, player),
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
