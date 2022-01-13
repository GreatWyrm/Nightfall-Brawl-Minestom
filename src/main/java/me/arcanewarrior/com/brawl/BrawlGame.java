package me.arcanewarrior.com.brawl;

import me.arcanewarrior.com.action.items.ActionItemType;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BrawlGame {


    private final Task updateBrawlPlayers;
    private final BrawlEvents events;

    private int tickCounter = 0;

    public BrawlGame() {
        updateBrawlPlayers = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            for(BrawlPlayer player : brawlPlayerList.values()) {
                player.update();
            }
            tickCounter++;
        }, TaskSchedule.immediate(), TaskSchedule.tick(1));
        // Above, start immediately, run once per tick
        events = new BrawlEvents(this, MinecraftServer.getGlobalEventHandler());
        events.registerEvents();
    }

    private final Map<UUID, BrawlPlayer> brawlPlayerList = new HashMap<>();

    public Set<String> getSetOfNames() {
        return brawlPlayerList.values().stream().map(player -> player.getPlayer().getUsername()).collect(Collectors.toSet());
    }

    public BrawlPlayer getBrawlPlayer(UUID uuid) {
        return brawlPlayerList.get(uuid);
    }

    public BrawlPlayer getBrawlPlayer(Entity entity) {
        return getBrawlPlayer(entity.getUuid());
    }

    public void addBrawlPlayer(Player player) {
        UUID id = player.getUuid();
        if(brawlPlayerList.containsKey(id)) {
            throw new IllegalStateException("Cannot add player " + player.getName() + " to the Action Player List, as they are already in it!");
        }
        brawlPlayerList.put(id, new BrawlPlayer(player, this));
    }

    public boolean isBrawlPlayer(Player player) {
        return brawlPlayerList.containsKey(player.getUuid());
    }

    public void giveActionItem(UUID id, ActionItemType type) {
        if(brawlPlayerList.containsKey(id)) {
            brawlPlayerList.get(id).giveActionItemType(type);
        }
    }

    public void removeActionItem(UUID id, ActionItemType type) {
        if(brawlPlayerList.containsKey(id)) {
            brawlPlayerList.get(id).removeActionItemType(type);
        }
    }


    public void removeBrawlPlayer(Player player) {
        brawlPlayerList.remove(player.getUuid());
    }

    public int getTickCount() {
        return tickCounter;
    }

    /**
     * Stops the current brawl game, cleaning up any variables that may have been initialized
     */
    public void stop() {
        updateBrawlPlayers.cancel();
        events.unregisterEvents();
    }
}
