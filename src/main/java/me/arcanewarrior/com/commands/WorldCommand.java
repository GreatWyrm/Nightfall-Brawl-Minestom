package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.managers.WorldManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;

import java.util.Locale;

public class WorldCommand extends Command {
    public WorldCommand() {
        super("world");

        var modeArg = ArgumentType.String("mode");
        var thirdArg = ArgumentType.String("world");

        setDefaultExecutor(((sender, context) -> sender.sendMessage("Usage: /world [list|load|tp] [other]")));

        addSyntax(((sender, context) -> {
            String mode = context.get(modeArg).toLowerCase(Locale.ROOT);
            switch(mode) {
                case "list" -> sender.sendMessage("Current World List: " + WorldManager.getManager().getWorldListNames());
                case "load" -> {
                    if(context.has(thirdArg)) {
                        WorldManager.getManager().loadMinecraftWorld(context.get(thirdArg));
                    } else {
                        sender.sendMessage("Missing Required filename parameter to load world!");
                    }
                }
                case "tp" -> {
                    if(sender.isPlayer()) {
                        if(context.has(thirdArg)) {
                            // Find world to load at with string - need world names
                            sender.sendMessage("Not supported yet");
                        }
                    } else {
                        sender.sendMessage("Only players can use this command!");
                    }
                }
            }
        }));
        /*
        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Incorrect usage of /warp");
            if(sender instanceof Player player) {
                player.setInstance(WorldManager.worldList.get(0), new Pos(-912.5, 164, -1761.5));
            } else {
                sender.sendMessage("Only players can use this command.");
            }
        }); */
    }
}
