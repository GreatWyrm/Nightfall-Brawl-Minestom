package me.arcanewarrior.com.commands;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.serverbase.ServerConfig;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public class StopCommand extends Command {
    public StopCommand() {
        super("stop");
        // TODO: Better feedback on command failure/success
        setDefaultExecutor((sender, context) -> {
            GameCore.getGameCore().stop();
            ServerConfig.writeServerConfig();
            MinecraftServer.stopCleanly();
        });
    }
}
