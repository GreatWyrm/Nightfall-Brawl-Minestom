package me.arcanewarrior.com.action;

import me.arcanewarrior.com.action.items.ActionItemType;
import me.arcanewarrior.com.action.items.BaseActionItem;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

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

    // Inputs

    public enum InputType {
        LEFT,
        RIGHT
    }

    public void OnPlayerInput(InputType type) {
        ItemStack heldItem = player.getItemInMainHand();
        BaseActionItem actionItem = actionItemInventory.getFromHeld(heldItem);
        if(actionItem != null) {
            switch (type) {
                case LEFT -> actionItem.OnLeftClick();
                case RIGHT -> actionItem.OnRightClick();
            }
        }
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
