package me.arcanewarrior.com.managers;

import me.arcanewarrior.com.GameCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.attribute.AttributeOperation;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.attribute.AttributeSlot;
import net.minestom.server.item.attribute.ItemAttribute;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemManager implements Manager {

    public static ItemManager getManager() { return GameCore.getGameCore().getManager(ItemManager.class); }

    private final Map<String, ItemStack> itemList = new HashMap<>();

    @Override
    public void initialize() {
        itemList.put("test", ItemStack.builder(Material.DIAMOND_SWORD)
                .displayName(Component.text("Entropy, the Blackened Blade", NamedTextColor.DARK_GRAY)) // Fully edgelord name
                .lore(Component.text("A sword that bleeds away the lifeforce of anything it touches.", NamedTextColor.DARK_RED, TextDecoration.ITALIC))
                .meta(itemMetaBuilder -> itemMetaBuilder.hideFlag(
                        ItemHideFlag.HIDE_ATTRIBUTES,
                        ItemHideFlag.HIDE_ENCHANTS,
                        ItemHideFlag.HIDE_UNBREAKABLE
                ).attributes(List.of(
                        new ItemAttribute(UUID.randomUUID(), "Attack!?", Attribute.ATTACK_DAMAGE, AttributeOperation.ADDITION, 20, AttributeSlot.MAINHAND))))
                .build()
        );
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
