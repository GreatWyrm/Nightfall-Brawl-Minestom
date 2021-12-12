package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.managers.WorldManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;

public class WorldCommand extends Command {
    public WorldCommand() {
        super("world");
        // TODO: Better feedback on command failure/success

        ArgumentWord modeArg = ArgumentType.Word("mode").from("list", "load", "tp");
        modeArg.setCallback((sender, exception) -> sender.sendMessage("Unknown mode: " + exception.getInput()));
        ArgumentString worldNameArg = ArgumentType.String("world");

        worldNameArg.setSuggestionCallback((sender, context, suggestion) -> {
            if(context.get(modeArg).equalsIgnoreCase("tp")) {
                for(String s : WorldManager.getManager().getLoadedWorldNames()) {
                    suggestion.addEntry(new SuggestionEntry(s));
                }
            }
        });

        setDefaultExecutor(((sender, context) -> sender.sendMessage("Usage: /world <list|load|tp> [world]")));

        addSyntax(((sender, context) -> {
            String mode = context.get(modeArg);
            if(mode.equalsIgnoreCase("list")) {
                sender.sendMessage("Current World List: " + WorldManager.getManager().getFormattedWorldNameString());
            }
        }), modeArg);

        addSyntax(((sender, context) -> {
            String mode = context.get(modeArg);
            String worldName = context.get(worldNameArg);
            switch(mode.toLowerCase()) {
                case "list" -> sender.sendMessage("Current World List: " + WorldManager.getManager().getFormattedWorldNameString());
                case "load" -> {
                    // TODO: Better error checking, make sure the file exists before loading random things
                    WorldManager.getManager().loadMinecraftWorld(worldName);
                    sender.sendMessage("Loaded world " + worldName);
                }
                case "tp" -> {
                    if(sender instanceof Player player) {
                        if(WorldManager.getManager().doesWorldExist(worldName)) {
                            // Find better way of getting the respawn point for a world, this only works on mt-velvetine
                            player.setInstance(WorldManager.getManager().getWorld(worldName), WorldManager.getManager().getWorldSpawnPoint(worldName));
                        } else {
                            sender.sendMessage("No world is loaded with the name " + worldName);
                        }
                    } else {
                        sender.sendMessage("Only players can use this command!");
                    }
                }
            }
        }), modeArg, worldNameArg);
    }
}
