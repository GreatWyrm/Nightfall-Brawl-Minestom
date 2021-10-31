package me.arcanewarrior.com.managers;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.items.ItemLoader;
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
        if(!itemList.containsKey(name)) {
            throw new IllegalArgumentException("Tried to get item '" + name + "', but it doesn't exist!");
        } else {
            return itemList.get(name);
        }
    }

    @Override
    public void stop() {
        itemList.clear();
    }
}
