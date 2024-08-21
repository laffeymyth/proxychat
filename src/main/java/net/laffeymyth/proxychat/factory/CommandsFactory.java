package net.laffeymyth.proxychat.factory;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.message.LiteMessages;
import dev.rollczi.litecommands.programmatic.LiteCommand;
import dev.rollczi.litecommands.velocity.LiteVelocityFactory;
import dev.rollczi.litecommands.velocity.tools.VelocityOnlyPlayerContextual;
import net.laffeymyth.localization.commons.service.ComponentLocalizationService;

import java.util.List;

public class CommandsFactory {
    private final ComponentLocalizationService componentLocalizationService = ComponentLocalizationService.lang();
    private final ProxyServer proxyServer;
    private final List<LiteCommand<CommandSource>> commandList;

    public CommandsFactory(ProxyServer proxyServer, List<LiteCommand<CommandSource>> commandList) {
        this.proxyServer = proxyServer;
        this.commandList = commandList;
    }

    public LiteCommands<CommandSource> create() {
        return LiteVelocityFactory.builder(proxyServer)
                .settings(settings -> settings.nativePermissions(true))
                .context(Player.class, new VelocityOnlyPlayerContextual<>(componentLocalizationService.getMessage("error_players_only_command", "ru")))
                .message(LiteMessages.INVALID_USAGE, (invocation, missingPermissions) -> componentLocalizationService.getMessage("invalid_usage", "ru"))
                .message(LiteMessages.MISSING_PERMISSIONS, (invocation, missingPermissions) -> componentLocalizationService.getMessage("not_enough_permissions", "ru"))
                .commands(commandList.toArray(new Object[0]))
                .build();
    }
}
