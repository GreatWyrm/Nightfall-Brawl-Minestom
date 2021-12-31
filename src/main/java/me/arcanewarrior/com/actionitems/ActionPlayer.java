package me.arcanewarrior.com.actionitems;

import net.minestom.server.entity.Player;

/**
 * @author ArcaneWarrior
 * A class meant to wrap around a Player
 * and allow them to use ActionItems
 */
public class ActionPlayer {

    private final Player player;
    private final ActionInventory actionItemInventory;

    public ActionPlayer(Player player) {
        this.player = player;
        actionItemInventory = new ActionInventory(this);
    }

    // ---- Action Inventory Manipulation ----

    public void giveActionItemType(ActionItemType type) {
        actionItemInventory.addToInventory(type);
    }

    public void removeActionItemType(ActionItemType type) {
        actionItemInventory.removeFromInventory(type);
    }

    // ---- Misc ----

    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    public Player getPlayer() {
        return player;
    }
}
