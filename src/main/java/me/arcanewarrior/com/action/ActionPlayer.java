package me.arcanewarrior.com.action;

import me.arcanewarrior.com.action.items.ActionItemType;
import me.arcanewarrior.com.action.items.BaseActionBow;
import me.arcanewarrior.com.action.items.BaseActionItem;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ArcaneWarrior
 * A class meant to wrap around a Player
 * and allow them to use ActionItems
 */
public class ActionPlayer {

    private static final Logger logger = LoggerFactory.getLogger(ActionPlayer.class);

    protected final Player player;
    protected final ActionInventory actionItemInventory;

    public ActionPlayer(Player player) {
        this.player = player;
        actionItemInventory = new ActionInventory(this);
    }

    // Updating/Ticking

    public void update() {
        actionItemInventory.update();
        // Set cooldown
        BaseActionItem actionItem = actionItemInventory.getFromHeld(player.getItemInMainHand());
        if(actionItem != null) {
            player.setExp(actionItem.getCooldown());
        }
    }

    // Bow Stuff

    public void OnStartDrawing(long currentTime) {
        ItemStack heldItem = player.getItemInMainHand();
        BaseActionItem actionItem = actionItemInventory.getFromHeld(heldItem);
        if(actionItem instanceof BaseActionBow actionBow) {
            actionBow.setBowStartDrawing(currentTime);
        }
    }

    /**
     * Called when the player fires their bow
     * @param projectile The projectile they are shooting
     * @param power The power of the projectile, ranges from 0 to 3
     * @return The new entity to fire from this bow, and null if this shouldn't fire anything
     */
    public boolean OnBowFire(Entity projectile, double power) {
        ItemStack heldItem = player.getItemInMainHand();
        BaseActionItem actionItem = actionItemInventory.getFromHeld(heldItem);
        if(actionItem instanceof BaseActionBow actionBow) {
            return actionBow.OnBowFire(projectile, power);
        }
        return false;
    }

    // Damage



    // Movement

    public void dashTowardsFacing(double multiplier) {
        if(multiplier > 80) {
            logger.warn("Velocity Multiplier over 80! This may cause weird behavior!");
        }
        Vec direction = player.getPosition().direction();
        player.setVelocity(direction.mul(multiplier));
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

    public void sendMessage(Component component) {player.sendMessage(component); }

    public Player getPlayer() {
        return player;
    }
    public Component getDisplayName() {
        if(player.getDisplayName() != null) {
            return player.getDisplayName();
        } else {
            return Component.text(player.getUsername());
        }
    }
}
