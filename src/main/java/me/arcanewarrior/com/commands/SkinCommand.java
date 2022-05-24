package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.managers.SkinManager;
import me.arcanewarrior.com.managers.WorldManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;


public class SkinCommand extends Command {

    private final SkinManager manager;
    public SkinCommand() {
        super("skin");

        manager = SkinManager.getManager();

        ArgumentWord mode = ArgumentType.Word("mode").from("set", "remove");
        ArgumentEntity players = ArgumentType.Entity("players").onlyPlayers(true);
        ArgumentString skinName = ArgumentType.String("skin");

        skinName.setSuggestionCallback((sender, context, suggestion) -> {
            for(String s : manager.getAllSkinNames()) {
                suggestion.addEntry(new SuggestionEntry(s));
            }
        });

        addSyntax((sender, context) -> {
            String modeString = context.get(mode);
            if(modeString.equalsIgnoreCase("remove")) {
                var playerList = context.get(players).find(sender);
                for(Entity entity : playerList) {
                    if(entity instanceof Player player) {
                        manager.removeSkin(player);
                    }
                }
            }
        }, mode, players);

        addSyntax((sender, context) -> {
            String modeString = context.get(mode);
            String skinString = context.get(skinName);
            if(modeString.equalsIgnoreCase("set")) {
                var playerList = context.get(players).find(sender);
                for(Entity entity : playerList) {
                    if(entity instanceof Player player) {
                        manager.setSkin(player, skinString);
                    }
                }
            }
        }, mode, players, skinName);
    }
}
