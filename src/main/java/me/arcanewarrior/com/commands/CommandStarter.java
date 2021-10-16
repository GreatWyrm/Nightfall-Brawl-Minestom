package me.arcanewarrior.com.commands;

import net.minestom.server.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommandStarter {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommandStarter.class);

    public static void registerAllCommands(CommandManager manager) {
        LOGGER.debug("Initializing Commands...");
        manager.register(new StopCommand());
        manager.register(new WorldCommand());

        LOGGER.debug("Finished Initializing Commands.");
    }
}
