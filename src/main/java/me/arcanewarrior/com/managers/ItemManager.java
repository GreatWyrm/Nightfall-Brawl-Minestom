package me.arcanewarrior.com.managers;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.items.ItemLoader;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.inventory.TransactionType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemManager implements Manager {

    public static ItemManager getManager() { return GameCore.getGameCore().getManager(ItemManager.class); }

    private final Map<String, ItemStack> itemList = new HashMap<>();

    @Override
    public void initialize() {
        loadItemsFromItemFolder();
    }

    public void reloadItems() {
        itemList.clear();
        loadItemsFromItemFolder();
    }

    private void loadItemsFromItemFolder() {
        ItemLoader itemLoader = new ItemLoader();
        Path itemFolder = Paths.get("items");
        if(Files.exists(itemFolder) && Files.isDirectory(itemFolder)) {
            try {
                Files.list(itemFolder).forEach(path -> {
                    System.out.println("Searching path: " + path.toString());
                    itemList.putAll(itemLoader.loadAllItems(path));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
