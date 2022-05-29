package me.arcanewarrior.com.action;

import me.arcanewarrior.com.action.items.ActionItemType;
import me.arcanewarrior.com.action.items.BaseActionItem;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.inventory.TransactionType;
import net.minestom.server.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ActionInventory {

    private final Map<ActionItemType, BaseActionItem> items = new HashMap<>();
    private final ActionPlayer player;

    public ActionInventory(ActionPlayer player) {
        this.player = player;
    }

    public boolean addToInventory(ActionItemType type) {
        if(items.containsKey(type)) {
            return false;
        } else {
            BaseActionItem newItem = type.createActionItem(player);
            items.put(type, newItem);
            player.getPlayer().getInventory().addItemStack(newItem.getBaseItem());
            return true;
        }
    }

    public boolean removeFromInventory(ActionItemType type) {
        if(!items.containsKey(type)) {
            return false;
        } else {
            BaseActionItem item = items.get(type);
            items.remove(type);
            player.getPlayer().getInventory().processItemStack(item.getBaseItem(), TransactionType.TAKE, TransactionOption.ALL_OR_NOTHING);
            return true;
        }
    }

    public BaseActionItem getFromHeld(ItemStack heldItem) {
        for(BaseActionItem item : items.values()) {
            if(item.doesItemMatch(heldItem)) {
                return item;
            }
        }
        return null;
    }

    public void update() {
        for(BaseActionItem item : items.values()) {
            item.update();
        }
    }

    public void clear() {
        items.clear();
    }
}
