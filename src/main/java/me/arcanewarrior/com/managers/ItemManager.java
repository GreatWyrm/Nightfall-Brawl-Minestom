package me.arcanewarrior.com.managers;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.items.ItemLoader;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.inventory.TransactionType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemManager implements Manager {

    public static ItemManager getManager() { return GameCore.getGameCore().getManager(ItemManager.class); }

    private final Map<String, ItemStack> itemList = new HashMap<>();

    @Override
    public void initialize() {
        ItemLoader itemLoader = new ItemLoader();
        itemList.putAll(itemLoader.loadAllItems(Paths.get("test.json")));
    }

    public Set<String> getAllItemNames() {
        return itemList.keySet();
    }

    public @NotNull ItemStack getItem(String name) {
        return getItem(name, 1);
    }

    public @NotNull ItemStack getItem(String name, int amount) {
        if(!itemList.containsKey(name)) {
            throw new IllegalArgumentException("Tried to get item '" + name + "', but it doesn't exist!");
        } else {
            return itemList.get(name).withAmount(amount);
        }
    }

    /**
     * Adds the item with the specified name to the player, only if the player's inventory can fit it
     * @param name The internal name of the item
     * @param amount The size of the ItemStack
     * @param player the player to add the item to
     * @return True if the item was added to the inventory, false if it could not be added (usually when the inventory is full)
     */
    public boolean giveItemToPlayer(String name, int amount, Player player) {
        ItemStack stack = getItem(name, amount);
        return player.getInventory().processItemStack(stack, TransactionType.ADD, TransactionOption.ALL_OR_NOTHING);
    }

    /**
     * Adds the item with the specified name to the player, giving as much as possible
     * @param name The internal name of the item
     * @param amount The size of the ItemStack
     * @param player the player to add the item to
     */
    public void forceGiveItemToPlayer(String name, int amount, Player player) {
        ItemStack stack = getItem(name, amount);
        player.getInventory().processItemStack(stack, TransactionType.ADD, TransactionOption.ALL);
    }

    @Override
    public void stop() {
        itemList.clear();
    }
}
