package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.managers.WorldManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

public class WarpCommand extends Command {
    public WarpCommand() {
        super("warp");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Incorrect usage of /warp");
            if(sender instanceof Player player) {
                player.setInstance(WorldManager.worldList.get(0), new Pos(-912.5, 164, -1761.5));
            } else {
                sender.sendMessage("Only players can use this command.");
            }
        });

        /*var worldArg = ArgumentType.String("world");
        addSyntax((sender, context) -> {
            if(sender instanceof Player player) {
                player.setRespawnPoint(new Pos(-912.5, 164, -1761.5));
                player.setInstance(WorldManager.worldList.get(0));
            } else {
                sender.sendMessage("Only players can use this command.");
            }
        });*/
    }
}
