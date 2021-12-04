package me.arcanewarrior.com.commands;

import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

public class GamemodeCommand extends Command {
    public GamemodeCommand() {
        super("gamemode", "gm");

        ArgumentEnum<GameMode> gamemode = ArgumentType.Enum("gamemode", GameMode.class).setFormat(ArgumentEnum.Format.LOWER_CASED);

        gamemode.setCallback((sender, exception) -> sender.sendMessage(
                Component.text("Invalid gamemode ", NamedTextColor.RED)
                        .append(Component.text(exception.getInput(), NamedTextColor.WHITE))
                        .append(Component.text("!")), MessageType.SYSTEM));

        ArgumentEntity players = ArgumentType.Entity("targets").onlyPlayers(true);

        setDefaultExecutor((sender, context) -> sender.sendMessage(Component.text("Usage: /gamemode <gamemode> [targets]", NamedTextColor.RED), MessageType.SYSTEM));

        addSyntax((sender, context) -> {
            if(sender instanceof Player player) {
                GameMode mode = context.get(gamemode);
                player.setGameMode(mode);
                player.sendMessage(Component.text("Set gamemode to ", NamedTextColor.GREEN).append(Component.text(mode.name(), NamedTextColor.YELLOW)), MessageType.SYSTEM);
            } else {
                sender.sendMessage(Component.text("Console cannot execute gamemode command without targets!", NamedTextColor.RED));
            }
        }, gamemode);

        addSyntax((sender, context) -> {
            EntityFinder finder = context.get(players);
            GameMode mode = context.get(gamemode);
            List<Entity> entityList = finder.find(sender);
            for(Entity entity : entityList) {
                if(entity instanceof Player other) {
                    other.setGameMode(mode);
                    sender.sendMessage(Component.text("Set ", NamedTextColor.GREEN)
                            // Return display name if not null
                            .append(other.getDisplayName() == null ? Component.text(other.getUsername(), NamedTextColor.AQUA) : other.getDisplayName())
                            .append(Component.text("'s gamemode to ", NamedTextColor.GREEN))
                            .append(Component.text(mode.name(), NamedTextColor.YELLOW)), MessageType.SYSTEM);
                }
            }
        }, gamemode, players);
    }
}
