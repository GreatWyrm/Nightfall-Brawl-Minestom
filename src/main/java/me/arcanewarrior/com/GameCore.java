package me.arcanewarrior.com;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import me.arcanewarrior.com.commands.CommandStarter;
import me.arcanewarrior.com.events.MainEventListener;
import me.arcanewarrior.com.managers.ActionPlayerManager;
import me.arcanewarrior.com.managers.ItemManager;
import me.arcanewarrior.com.managers.Manager;
import me.arcanewarrior.com.managers.WorldManager;
import me.arcanewarrior.com.serverbase.ServerConfig;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.server.ServerListPingEvent;

// The Main Managing Class for the entire game
public class GameCore {

    // Class - Instance Map for Managers
    // Used for Classes that need to be accessible from a wide variety of places
    private final ClassToInstanceMap<Manager> managers;


    private static GameCore gameCore = null;
    public static GameCore getGameCore() { return gameCore; }

    public GameCore() {

        ImmutableClassToInstanceMap.Builder<Manager> builder = ImmutableClassToInstanceMap.builder();
        builder.put(WorldManager.class, new WorldManager());
        builder.put(ItemManager.class, new ItemManager());
        builder.put(ActionPlayerManager.class, new ActionPlayerManager());

        this.managers = builder.build();

        managers.values().forEach(Manager::initialize);

        gameCore = this;

        CommandStarter.registerAllCommands(MinecraftServer.getCommandManager());

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerLoginEvent.class, playerLoginEvent -> {
            playerLoginEvent.setSpawningInstance(WorldManager.getManager().getDefaultWorld());
            playerLoginEvent.getPlayer().setRespawnPoint(new Pos(0, 42, 0));
        });


        globalEventHandler.addListener(ServerListPingEvent.class, serverListPingEvent -> serverListPingEvent.setResponseData(ServerConfig.getServerResponseData()));

        MainEventListener listener = new MainEventListener();
        listener.registerAllEvents();

    }

    public <S extends Manager> S getManager(Class<S> managerClass) {
        if(!managers.containsKey(managerClass)) {
            throw new IllegalStateException("Tried to access manager class " + managerClass.getCanonicalName() + ", but it was not registered!");
        }
        return managers.getInstance(managerClass);
    }

    public void stop() {
        managers.values().forEach(Manager::stop);
    }

}
