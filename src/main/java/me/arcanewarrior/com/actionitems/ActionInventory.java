package me.arcanewarrior.com.actionitems;

import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.inventory.TransactionType;

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
}
