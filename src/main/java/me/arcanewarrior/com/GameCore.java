package me.arcanewarrior.com;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import me.arcanewarrior.com.commands.CommandStarter;
import me.arcanewarrior.com.managers.Manager;
import me.arcanewarrior.com.managers.WorldManager;
import net.minestom.server.MinecraftServer;

// The Main Managing Class for the entire game
public class GameCore {

    // Class - Instance Map for Managers
    // Used for Classes that need to be accessible from a wide variety of places
    private final ClassToInstanceMap<Manager> managers;


    private static GameCore gameCore = null;
    public static GameCore getGameCore() { return gameCore; }

    public GameCore() {

        CommandStarter.registerAllCommands(MinecraftServer.getCommandManager());

        ImmutableClassToInstanceMap.Builder<Manager> builder = ImmutableClassToInstanceMap.builder();
        builder.put(WorldManager.class, new WorldManager());

        this.managers = builder.build();

        managers.values().forEach(Manager::initialize);

        gameCore = this;
    }

    public <S extends Manager> S getManager(Class<S> managerClass) {
        if(!managers.containsKey(managerClass)) {
            throw new IllegalStateException("Tried to access manager class " + managerClass.getCanonicalName() + ", but it was not registered!");
        }
        return managers.getInstance(managerClass);
    }

}
