package net.laffeymyth.proxychat.chat;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.programmatic.LiteCommand;
import net.kyori.adventure.text.Component;
import net.laffeymyth.localization.commons.service.ComponentLocalizationService;
import net.laffeymyth.localization.commons.service.PlainLocalizationService;
import net.laffeymyth.localization.commons.util.ComponentResolver;
import net.laffeymyth.proxychat.chat.dto.ChatSetting;
import net.laffeymyth.proxychat.delay.DelayService;
import net.laffeymyth.proxychat.displayname.DisplayNameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static net.laffeymyth.proxychat.util.PrefixUtil.PREFIX_RESOLVER;

public class ChatCommand extends LiteCommand<CommandSource> {
    private static final Logger log = LoggerFactory.getLogger(ChatCommand.class);
    private final ComponentLocalizationService lang = ComponentLocalizationService.lang();
    private final PlainLocalizationService config = PlainLocalizationService.lang();
    private final String topicName;

    public ChatCommand(String name,
                       Set<String> aliases,
                       String permission,
                       ChatService chatService,
                       ChatSettingService chatSettingService,
                       String topicName, DisplayNameService displayNameService, Set<String> changeSettingSubcommand, DelayService delayService) {
        super(name, aliases.stream().toList());
        this.topicName = topicName;

        permissions(permission);

        argumentJoin("text");

        async();

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


            if (delayService.hasDelay(player.getUsername(), topicName)) {
                log.info(String.valueOf(delayService.getDelay(player.getUsername(), topicName, TimeUnit.SECONDS)));
                player.sendMessage(lang.getMessage(topicName + "_delay_error", "ru", ComponentResolver.tag("seconds", (argumentQueue, context1) -> Component.text(delayService.getDelay(player.getUsername(), topicName, TimeUnit.SECONDS)))));
                return;
            }

            long delay = Integer.parseInt(config.getMessage(topicName + "_delay", "ru"));


            delayService.putDelay(player.getUsername(), topicName,
                    delay,
                    TimeUnit.SECONDS);

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
