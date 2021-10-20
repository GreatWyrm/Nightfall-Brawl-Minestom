package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.GameCore;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public class StopCommand extends Command {
    public StopCommand() {
        super("stop");
        setDefaultExecutor((sender, context) -> {
            GameCore.getGameCore().stop();
            MinecraftServer.stopCleanly();
        });
    }
}
