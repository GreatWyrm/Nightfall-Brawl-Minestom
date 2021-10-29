package me.arcanewarrior.com.items;

import com.fasterxml.jackson.databind.JsonNode;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class ItemLoader {

    public ItemLoader() {

    }

    public ItemStack loadItem(JsonNode itemNode) {
        // Get material
        JsonNode material = itemNode.get("material");
        if(material == null) {
            throw new IllegalArgumentException("Material is not defined!");
        } else if(!material.isTextual()) {
            throw new IllegalArgumentException("Material must be of type string!");
        }
        Material baseMaterial = Material.fromNamespaceId("minecraft:" + material.asText());
        if(baseMaterial == null) {
            throw new IllegalArgumentException("Could not find material: " + material.asText() + "!");
        }

        JsonNode formatStyle = itemNode.get("style");
        ItemFormatStyle style = ItemFormatStyle.valueOf(formatStyle.asText("default"));

        String itemName = itemNode.asText("ERROR: No name was specified");

        // TODO: Lore
        // TODO: Attributes
        // TODO: Enchants


        return ItemStack.builder(baseMaterial)
                .displayName(Component.text(itemName, style.getNameColor()))
                .meta(itemMetaBuilder -> itemMetaBuilder
                        .hideFlag(
                                ItemHideFlag.HIDE_ATTRIBUTES,
                                ItemHideFlag.HIDE_ENCHANTS,
                                ItemHideFlag.HIDE_UNBREAKABLE))
                .amount(1)
                .build();
    }
}
