package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.GameCore;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;

import java.util.Set;

public class BrawlGameCommand extends Command {

    GameCore gameCore = GameCore.getGameCore();

    public BrawlGameCommand() {
        super("brawl");

        ArgumentWord modeArg = ArgumentType.Word("mode").from("start", "stop", "add", "remove", "quickstart");
        ArgumentEntity playerArg = ArgumentType.Entity("players").onlyPlayers(true);

        playerArg.setSuggestionCallback((sender, context, suggestion) -> {
            if(context.get(modeArg).equalsIgnoreCase("remove")) {
                for(String name : gameCore.getBrawlPlayerNames()) {
                    suggestion.addEntry(new SuggestionEntry(name));
                }
            } else if(context.get(modeArg).equalsIgnoreCase("add")) {
                // Get all names that aren't already an action player
                Set<String> brawlPlayerNames = gameCore.getBrawlPlayerNames();
                var nonBrawlPlayers = MinecraftServer.getConnectionManager().getOnlinePlayers().stream().filter(p -> !brawlPlayerNames.contains(p.getUsername())).toList();
                for(Player other : nonBrawlPlayers) {
                    suggestion.addEntry(new SuggestionEntry(other.getUsername()));
                }
            }
        });

        addSyntax((sender, context) -> {
            if(context.get(modeArg).equalsIgnoreCase("start")) {
                gameCore.createNewBrawlGame();
            } else if(context.get(modeArg).equalsIgnoreCase("stop")) {
                gameCore.endBrawlGame();
            } else if(context.get(modeArg).equalsIgnoreCase("quickstart")) {
                gameCore.createNewBrawlGame();
                for(Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                    gameCore.addBrawlPlayer(player);
                }
            }
        }, modeArg);

        addSyntax((sender, context) -> {
            String modeString = context.get(modeArg).toLowerCase();
            switch (modeString) {
                case "add" -> {
                    for(var arg : context.get(playerArg).find(sender)) {
                        if(arg instanceof Player p && !gameCore.isBrawlPlayer(p)) {
                            gameCore.addBrawlPlayer(p);
                            sender.sendMessage("Added " + p.getUsername() + " as an Brawl Player");
                        }
                    }
                }
                case "remove" -> {
                    for(var arg : context.get(playerArg).find(sender)) {
                        if(arg instanceof Player p && gameCore.isBrawlPlayer(p)) {
                            gameCore.removeBrawlPlayer(p);
                            sender.sendMessage("Removed " + p.getUsername() + " as an Brawl Player");
                        }
                    }
                }
                default -> sender.sendMessage("Invalid mode for player argument");
            }
        }, modeArg, playerArg);
    }
}
