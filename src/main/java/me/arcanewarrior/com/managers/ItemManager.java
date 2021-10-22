package me.arcanewarrior.com.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.attribute.AttributeOperation;
import net.minestom.server.item.*;
import net.minestom.server.item.attribute.AttributeSlot;
import net.minestom.server.item.attribute.ItemAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemManager implements Manager {

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

    @Override
    public void stop() {
        itemList.clear();
    }
}
