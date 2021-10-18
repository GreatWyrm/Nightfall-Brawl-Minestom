package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.managers.WorldManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;

import java.util.Locale;

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
                case "load" -> WorldManager.getManager().loadMinecraftWorld(worldName);
                case "tp" -> {
                    if(sender.isPlayer()) {
                        if(context.has(thirdArg)) {
                            // Find world to load at with string - need world names
                            sender.sendMessage("Not supported yet");
                            sender.asPlayer().setInstance(WorldManager.getManager().getWorld(1),  new Pos(-912.5, 164, -1761.5));
                        }
                    } else {
                        sender.sendMessage("Only players can use this command!");
                    }
                }
            }
        }), modeArg, thirdArg);
    }
}
