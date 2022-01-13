package me.arcanewarrior.com;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import me.arcanewarrior.com.action.items.ActionItemType;
import me.arcanewarrior.com.brawl.BrawlGame;
import me.arcanewarrior.com.commands.CommandStarter;
import me.arcanewarrior.com.events.MainEventListener;
import me.arcanewarrior.com.managers.ItemManager;
import me.arcanewarrior.com.managers.Manager;
import me.arcanewarrior.com.managers.WorldManager;
import me.arcanewarrior.com.serverbase.ServerConfig;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.server.ServerListPingEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// The Main Managing Class for the entire game
public class GameCore {

    // Class - Instance Map for Managers
    // Used for Classes that need to be accessible from a wide variety of places
    private final ClassToInstanceMap<Manager> managers;


    private static GameCore gameCore = null;
    public static GameCore getGameCore() { return gameCore; }

    private BrawlGame currentBrawlGame;

    public GameCore() {

        ImmutableClassToInstanceMap.Builder<Manager> builder = ImmutableClassToInstanceMap.builder();
        builder.put(WorldManager.class, new WorldManager());
        builder.put(ItemManager.class, new ItemManager());

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


    // ------ BRAWL GAME DELEGATION ------
    // Safely wraps around the brawl game, performing null checks
    public void createNewBrawlGame() {
        currentBrawlGame = new BrawlGame(WorldManager.getManager().getDefaultWorld());
    }

    public void endBrawlGame() {
        if(currentBrawlGame != null) {
            currentBrawlGame.stop();
            currentBrawlGame = null;
        }
    }

    public Set<String> getBrawlPlayerNames() {
        if(currentBrawlGame != null) {
            return currentBrawlGame.getSetOfNames();
        } else {
            return new HashSet<>();
        }
    }

    public boolean isBrawlPlayer(Player player) {
        return currentBrawlGame != null && currentBrawlGame.isBrawlPlayer(player);
    }

    public void addBrawlPlayer(Player player) {
        if(currentBrawlGame != null) {
            currentBrawlGame.addBrawlPlayer(player);
        }
    }

    public void removeBrawlPlayer(Player player) {
        if(currentBrawlGame != null) {
            currentBrawlGame.removeBrawlPlayer(player);
        }
    }

    public void stop() {
        managers.values().forEach(Manager::stop);
        if(currentBrawlGame != null) {
            currentBrawlGame.stop();
        }
    }

    public void giveActionItem(Player player, ActionItemType type) {
        giveActionItem(player.getUuid(), type);
    }

    public void giveActionItem(UUID uuid, ActionItemType type) {
        if(currentBrawlGame != null) {
            currentBrawlGame.giveActionItem(uuid, type);
        }
    }

    public void removeActionItem(Player player, ActionItemType type) {
        removeActionItem(player.getUuid(), type);
    }

    public void removeActionItem(UUID uuid, ActionItemType type) {
        if(currentBrawlGame != null) {
            currentBrawlGame.removeActionItem(uuid, type);
        }
    }

    public void warpPlayersToCenter() {
        if(currentBrawlGame != null) {
            currentBrawlGame.warpAllToCenter();
        }
    }
}
