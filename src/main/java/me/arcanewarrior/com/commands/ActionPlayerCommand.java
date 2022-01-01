package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.managers.ActionPlayerManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;

import java.util.Set;

public class ActionPlayerCommand extends Command {
    public ActionPlayerCommand() {
        super("actionplayer", "actp");

        ArgumentWord mode = ArgumentType.Word("mode").from("add", "remove");
        ArgumentEntity player = ArgumentType.Entity("players").onlyPlayers(true);

        player.setSuggestionCallback((sender, context, suggestion) -> {
            if(context.get(mode).equalsIgnoreCase("remove")) {
                for(String name : ActionPlayerManager.getManager().getSetOfNames()) {
                    suggestion.addEntry(new SuggestionEntry(name));
                }
            } else if(context.get(mode).equalsIgnoreCase("add")) {
                // Get all names that aren't already an action player
                Set<String> actionPlayerNames =  ActionPlayerManager.getManager().getSetOfNames();
                var nonActionPlayers = MinecraftServer.getConnectionManager().getOnlinePlayers().stream().filter(p -> !actionPlayerNames.contains(p.getUsername())).toList();
                for(Player other : nonActionPlayers) {
                    suggestion.addEntry(new SuggestionEntry(other.getUsername()));
                }
            }
        });

        addSyntax((sender, context) -> {
            String modeString = context.get(mode).toLowerCase();
            switch (modeString) {
                case "add" -> {
                    for(var arg : context.get(player).find(sender)) {
                        if(arg instanceof Player p && !ActionPlayerManager.getManager().isActionPlayer(p)) {
                            ActionPlayerManager.getManager().addActionPlayer(p);
                        }
                    }
                }
                case "remove" -> {
                    for(var arg : context.get(player).find(sender)) {
                        if(arg instanceof Player p && ActionPlayerManager.getManager().isActionPlayer(p)) {
                            ActionPlayerManager.getManager().removeActionPlayer(p);
                        }
                    }
                }
                default -> sender.sendMessage("Invalid mode for player argument");
            }
        }, mode, player);
    }
}
