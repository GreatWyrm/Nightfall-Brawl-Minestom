package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.managers.WorldManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;

public class WorldCommand extends Command {
    public WorldCommand() {
        super("world");

        var modeArg = ArgumentType.Word("mode").from("list", "load", "tp");
        modeArg.setCallback(((sender, exception) -> sender.sendMessage("Unknown mode: " + exception.getInput())));
        var thirdArg = ArgumentType.String("world");

        setDefaultExecutor(((sender, context) -> sender.sendMessage("Usage: /world <list|load|tp> [world]")));

        addSyntax(((sender, context) -> {
            String mode = context.get(modeArg);
            if(mode.equalsIgnoreCase("list")) {
                sender.sendMessage("Current World List: " + WorldManager.getManager().getWorldListNames());
            }
        }), modeArg);

        addSyntax(((sender, context) -> {
            String mode = context.get(modeArg);
            String worldName = context.get(thirdArg);
            switch(mode.toLowerCase()) {
                case "list" -> sender.sendMessage("Current World List: " + WorldManager.getManager().getWorldListNames());
                case "load" -> {
                    // TODO: Better error checking, make sure the file exists before loading random things
                    WorldManager.getManager().loadMinecraftWorld(worldName);
                    sender.sendMessage("Loaded world " + worldName);
                }
                case "tp" -> {
                    if(sender.isPlayer()) {
                        if(WorldManager.getManager().doesWorldExist(worldName)) {
                            // Find better way of getting the respawn point for a world, this only works on mt-velvetine
                            sender.asPlayer().setInstance(WorldManager.getManager().getWorld(worldName),  new Pos(-912.5, 164, -1761.5));
                        } else {
                            sender.sendMessage("No world is loaded with the name " + worldName);
                        }
                    } else {
                        sender.sendMessage("Only players can use this command!");
                    }
                }
            }
        }), modeArg, thirdArg);
    }
}
