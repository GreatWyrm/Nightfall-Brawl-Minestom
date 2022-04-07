package me.arcanewarrior.com.managers;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.brawl.BrawlPlayerData;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Class to store
 */
public class BrawlPlayerDataManager implements Manager {

    private final Logger logger = LoggerFactory.getLogger(BrawlPlayerDataManager.class);

    public static BrawlPlayerDataManager getManager() { return GameCore.getGameCore().getManager(BrawlPlayerDataManager.class); }

    private final Map<UUID, BrawlPlayerData> playerDataMap = new ConcurrentHashMap<>();
    // Loading/Unloading sets, used to check when accessing data
    private final HashSet<UUID> loadingUUIDs = new HashSet<>();
    private final HashSet<UUID> unloadingUUIDs = new HashSet<>();
    private final BrawlPlayerData DEFAULT_DATA = new BrawlPlayerData();

    @Override
    public void initialize() {
        EventNode<EntityEvent> dataLoader = EventNode.type("brawl-data-events", EventFilter.ENTITY);
        dataLoader.addListener(EventListener.builder(PlayerLoginEvent.class)
                .handler(this::handlePlayerLogin)
                .build());
        dataLoader.addListener(EventListener.builder(PlayerDisconnectEvent.class)
                .handler(this::handlePlayerDisconnect)
                .build());
        MinecraftServer.getGlobalEventHandler().addChild(dataLoader);
    }

    @Override
    public void stop() {

    }

    private void handlePlayerLogin(PlayerLoginEvent event) {
        UUID id = event.getPlayer().getUuid();
        if(unloadingUUIDs.contains(id)) {
            // Player rejoined before their data unloaded, cancel unload process
            unloadingUUIDs.remove(id);
        } else if(playerDataMap.containsKey(id)) {
            // If their data is already loaded, do nothing
            logger.warn("Player " + event.getPlayer().getUsername() + " logged in while their data was already loaded?!");
        } else if(loadingUUIDs.contains(id)) {
            // Their data is already loading, do nothing
        } else {
            loadDataFromStorage(id);
        }
    }

    private void handlePlayerDisconnect(PlayerDisconnectEvent event) {
        UUID id = event.getPlayer().getUuid();
        unloadingUUIDs.add(id);
        // Unload data after a short delay
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            if(unloadingUUIDs.contains(id)) {
                saveDataToStorage(id);
                unloadingUUIDs.remove(id);
            }
        }).delay(TaskSchedule.tick(20)).schedule();
    }

    private void loadDataFromStorage(UUID id) {
        loadingUUIDs.add(id);
        // Temporary: Set as default data
        playerDataMap.put(id, new BrawlPlayerData());
        // Async load player from storage, since we're doing file reading
        loadingUUIDs.remove(id);
    }

    private void saveDataToStorage(UUID id) {
        // Async Save player to storage, since we're doing file writing
    }

    /**
     * Gets the BrawlPlayerData of a player
     * <p>This should be only used to read from it, as it as various safety checks are built in to this function and returns default data if it cannot retrieve it</p>
     * Use {@link #modifyPlayerData(Player, Consumer)} to edit data
     * @param player The player to get the data for
     * @return The player data, or default data if their data does not current exist
     */
    public BrawlPlayerData getPlayerData(Player player) {
        UUID id = player.getUuid();
        if(unloadingUUIDs.contains(id)) {
            logger.warn("Tried to access the data of a player that was unloading!");
            return DEFAULT_DATA;
        } else if(loadingUUIDs.contains(id)) {
            logger.warn("Tried to access the data of a player before their data loaded!");
            return DEFAULT_DATA;
        } else if(playerDataMap.containsKey(id)) {
            return playerDataMap.get(id);
        } else {
            logger.error("Player " + player.getUsername() + " was missing their PlayerData?!?");
            loadDataFromStorage(id);
            return DEFAULT_DATA;
        }
    }

    public void modifyPlayerData(Player player, Consumer<BrawlPlayerData> modifierFunction) {
        if(playerDataMap.containsKey(player.getUuid())) {
            modifierFunction.accept(playerDataMap.get(player.getUuid()));
        } else {
            logger.error("Tried to modify BrawlPlayerData, but they didn't have their data loaded!");
        }
    }
}
