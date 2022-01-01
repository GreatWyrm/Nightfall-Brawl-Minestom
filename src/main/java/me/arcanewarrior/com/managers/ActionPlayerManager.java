package me.arcanewarrior.com.managers;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.events.ActionListener;
import me.arcanewarrior.com.action.ActionPlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.*;

public class ActionPlayerManager implements Manager {

    public static ActionPlayerManager getManager() { return GameCore.getGameCore().getManager(ActionPlayerManager.class); }

    private final Map<UUID, ActionPlayer> actionPlayerList = new HashMap<>();

    public Set<UUID> getListOfUUIDs() {
        return actionPlayerList.keySet();
    }

    public List<String> getListOfNames() {
        return actionPlayerList.values().stream().map(actionPlayer -> actionPlayer.getPlayer().getUsername()).toList();
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


    public ActionPlayer getActionPlayer(String actionPlayerName) {
        for(var entry : actionPlayerList.values()) {
            if(entry.getPlayer().getUsername().equals(actionPlayerName)) {
                return entry;
            }
        }
        return null;
    }

    public void addActionPlayer(Player player) {
        UUID id = player.getUuid();
        if(actionPlayerList.containsKey(id)) {
            throw new IllegalStateException("Cannot add player " + player.getName() + " to the Action Player List, as they are already in it!");
        }
        actionPlayerList.put(id, new ActionPlayer(player));
    }

    // TODO: Make the command use a player argument so we don't have to iterate
    public void removeActionPlayer(String username) {
        for(var entry : actionPlayerList.values()) {
            if(entry.getPlayer().getUsername().equals(username)) {
                actionPlayerList.remove(entry.getPlayer().getUuid());
            }
        }
    }

    @Override
    public void initialize() {
        ActionListener listener = new ActionListener(this, MinecraftServer.getGlobalEventHandler());
    }

    @Override
    public void stop() {

    }

}
