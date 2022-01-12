package me.arcanewarrior.com.managers;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.action.ActionPlayer;
import me.arcanewarrior.com.events.ActionListener;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ActionPlayerManager implements Manager {

    public static ActionPlayerManager getManager() { return GameCore.getGameCore().getManager(ActionPlayerManager.class); }

    private final Map<UUID, ActionPlayer> actionPlayerList = new HashMap<>();

    public Set<String> getSetOfNames() {
        return actionPlayerList.values().stream().map(actionPlayer -> actionPlayer.getPlayer().getUsername()).collect(Collectors.toSet());
    }

    public boolean isActionPlayer(Player player) {
        return isActionPlayer(player.getUuid());
    }

    public boolean isActionPlayer(UUID uuid) {
        return actionPlayerList.containsKey(uuid);
    }

    public ActionPlayer getActionPlayer(Player player) {
        return getActionPlayer(player.getUuid());
    }

    public ActionPlayer getActionPlayer(UUID uuid) {
        return actionPlayerList.get(uuid);
    }

    public void addActionPlayer(Player player) {
        UUID id = player.getUuid();
        if(actionPlayerList.containsKey(id)) {
            throw new IllegalStateException("Cannot add player " + player.getName() + " to the Action Player List, as they are already in it!");
        }
        actionPlayerList.put(id, new ActionPlayer(player));
    }

    public void removeActionPlayer(Player player) {
        actionPlayerList.remove(player.getUuid());
    }

    private ActionListener listener;

    @Override
    public void initialize() {
        listener = new ActionListener(this, MinecraftServer.getGlobalEventHandler());
        listener.registerEvents();
        updateActionPlayersTask = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            for(ActionPlayer player : actionPlayerList.values()) {
                player.update();
            }
        }, TaskSchedule.immediate(), TaskSchedule.tick(1));
        // Above, start immediately, run once per tick

    }

    @Override
    public void stop() {
        listener.unregisterEvents();
        updateActionPlayersTask.cancel();
    }


    private Task updateActionPlayersTask;
}
