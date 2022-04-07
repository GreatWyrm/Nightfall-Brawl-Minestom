package me.arcanewarrior.com.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.attribute.AttributeOperation;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.attribute.AttributeSlot;
import net.minestom.server.item.attribute.ItemAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemLoader {

    private final Logger logger = LoggerFactory.getLogger(ItemLoader.class);

    public ItemLoader() {

    }

    public Map<String, ItemStack> loadAllItems(Path path) {
        if(Files.exists(path) && !Files.isDirectory(path)) {
            return loadItemsFromPath(path);
        } else {
            throw new IllegalArgumentException("Could not find file " + path + ", or it is a directory!");
        }
    }

    private Map<String, ItemStack> loadItemsFromPath(Path path) {
        Map<String, ItemStack> loadedItems = new HashMap<>();
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(path)
                .indent(2)
                .build();
        try {
            CommentedConfigurationNode input = loader.load();
            for(var child : input.childrenMap().entrySet()) {
                ItemStack stack = createItemFromNode(child.getValue());
                loadedItems.put(child.getKey().toString(), stack);
            }
        } catch (ConfigurateException e) {
            logger.warn("Failed to load items in path: " + path.toString());
            e.printStackTrace();
        }
        return loadedItems;
    }

    protected ItemStack createItemFromNode(CommentedConfigurationNode node) {

        CommentedConfigurationNode materialNode = node.node("material");
        if(materialNode.isNull()) {
            logger.warn("Material is not specified for item!");
        }
        String materialName = materialNode.getString( "diamond");
        Material baseMaterial = Material.fromNamespaceId("minecraft:"+materialName);
        if(baseMaterial == null) {
            throw new IllegalArgumentException("Could not find material: " + materialName + "!");
        }

        CommentedConfigurationNode nameNode = node.node("name");
        if(nameNode.isNull()) {
            logger.warn("Name is not specified for item!");
        }
        String itemName = nameNode.getString("Error: No Name Specified");

        CommentedConfigurationNode styleNode = node.node("style");
        String styleName = styleNode.getString("default").toUpperCase();
        ItemFormatStyle formatStyle = ItemFormatStyle.valueOf(styleName);

        CommentedConfigurationNode modelDataNode = node.node("custommodeldata");
        int customModelData = modelDataNode.getInt(0);

        ArrayList<Component> lore = new ArrayList<>();
        CommentedConfigurationNode loreNode = node.node("lore");
        String loreLines = loreNode.getString("default");
        if (loreLines != null) {
            for(String line : loreLines.split("\n")) {
                lore.add(Component.text(line, Style.style(formatStyle.getLoreColor(), TextDecoration.ITALIC.withState(false))));
            }
        }

        ArrayList<ItemAttribute> attributes = new ArrayList<>();
        CommentedConfigurationNode attributeNode = node.node("attributes");
        for(var child : attributeNode.childrenList()) {
            String attributeName = child.node("attribute").getString();
            if(attributeName == null) {
                logger.warn("Unable to read attribute name!");
                continue;
            }
            Attribute actual = Attribute.fromKey("generic." + attributeName.toLowerCase());
            if (actual == null) {
                logger.warn("Unknown attribute name: " + attributeName);
                continue;
            }
            int operation = child.node("operation").getInt(0);
            AttributeOperation attributeOperation = AttributeOperation.fromId(operation);
            if (attributeOperation == null) {
                logger.warn("Unknown attribute operation id: " + operation);
                continue;
            }
            double amount = child.node("amount").getDouble(1);
            AttributeSlot slot = AttributeSlot.valueOf(child.node("slot").getString("mainhand").toUpperCase());

            var newAttribute = new ItemAttribute(UUID.randomUUID(), "name", actual, attributeOperation, amount, slot);
            attributes.add(newAttribute);
        }

        HashMap<Enchantment, Short> enchantments = new HashMap<>();
        CommentedConfigurationNode enchantmentsNode = node.node("enchantments");
        for(var child : enchantmentsNode.childrenMap().entrySet()) {
            Enchantment enchantment = Enchantment.fromNamespaceId("minecraft:"+child.getKey());
            if(enchantment == null) {
                logger.warn("Unknown enchantment " + child.getKey());
            }
            short value = (short) child.getValue().getInt(0);
            enchantments.put(enchantment, value);
        }

        return ItemStack.builder(baseMaterial)
                .displayName(Component.text(itemName,
                        Style.style(formatStyle.getNameColor(), TextDecoration.ITALIC.withState(false)))
                )
                .meta(itemMetaBuilder -> itemMetaBuilder
                        .hideFlag(
                                //ItemHideFlag.HIDE_ATTRIBUTES,
                                //ItemHideFlag.HIDE_ENCHANTS,
                                ItemHideFlag.HIDE_UNBREAKABLE
                        )
                        .attributes(attributes)
                        .enchantments(enchantments)
                        .customModelData(customModelData)
                        .unbreakable(true)
                        .lore(lore)
                )
                .amount(1)
                .build();
    }
}
