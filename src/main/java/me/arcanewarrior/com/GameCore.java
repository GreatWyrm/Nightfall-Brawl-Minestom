package me.arcanewarrior.com;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import me.arcanewarrior.com.action.items.ActionItemType;
import me.arcanewarrior.com.brawl.BrawlGame;
import me.arcanewarrior.com.commands.CommandStarter;
import me.arcanewarrior.com.events.MainEventListener;
import me.arcanewarrior.com.managers.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.message.ChatPosition;
import net.minestom.server.network.packet.server.play.ChatMessagePacket;

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
        builder.put(LoadoutManager.class, new LoadoutManager());
        builder.put(BrawlPlayerDataManager.class, new BrawlPlayerDataManager());

        this.managers = builder.build();

        managers.values().forEach(Manager::initialize);

        gameCore = this;

        CommandStarter.registerAllCommands(MinecraftServer.getCommandManager());

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerLoginEvent.class, playerLoginEvent -> {
            playerLoginEvent.setSpawningInstance(WorldManager.getManager().getDefaultWorld());
            playerLoginEvent.getPlayer().setRespawnPoint(new Pos(0, 42, 0));
        });


        MainEventListener listener = new MainEventListener(this);
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
        if(!WorldManager.getManager().doesWorldExist("final-destination")) {
            WorldManager.getManager().loadMinecraftWorld("final-destination");
        }
        currentBrawlGame = new BrawlGame(WorldManager.getManager().getWorld("final-destination"));
    }

    public void endBrawlGame() {
        if(currentBrawlGame != null) {
            currentBrawlGame.stop();
            currentBrawlGame = null;
        }
    }

    public Set<String> getBrawlPlayerNames() {
        if(currentBrawlGame != null) {
            return currentBrawlGame.getListOfNames();
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
        if(currentBrawlGame != null && currentBrawlGame.isBrawlPlayer(uuid)) {
            currentBrawlGame.getBrawlPlayer(uuid).giveActionItemType(type);
        }
    }

    public void removeActionItem(Player player, ActionItemType type) {
        removeActionItem(player.getUuid(), type);
    }

    public void removeActionItem(UUID uuid, ActionItemType type) {
        if(currentBrawlGame != null && currentBrawlGame.isBrawlPlayer(uuid)) {
            currentBrawlGame.getBrawlPlayer(uuid).removeActionItemType(type);
        }
    }

    public void warpPlayersToCenter() {
        if(currentBrawlGame != null) {
            currentBrawlGame.warpAllToCenter();
        }
    }

    public void broadcastMessage(Component text) {
        Audiences.players().sendMessage(text);
    }
}
