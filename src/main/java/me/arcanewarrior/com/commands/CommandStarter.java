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
        manager.register(new GamemodeCommand());
        manager.register(new ItemCommand());
        manager.register(new SummonCommand());
        manager.register(new ActionItemCommand());
        manager.register(new BrawlGameCommand());
        manager.register(new KillCommand());
        manager.register(new EffectCommand());

        LOGGER.debug("Finished Initializing Commands.");
    }
}
