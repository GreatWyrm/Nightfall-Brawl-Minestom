package me.arcanewarrior.com.items;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ItemLoader {

    private final Logger logger = LoggerFactory.getLogger(ItemLoader.class);

    public ItemLoader() {

    }

    public Map<String, ItemStack> loadAllItems(Path path) {
        if(Files.exists(path) && !Files.isDirectory(path)) {
            try {
                FileInputStream stream = new FileInputStream(path.toFile());
                return loadAllItems(stream);
            } catch (FileNotFoundException e) {
                logger.warn("Failed to find file " + path);
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Could not find file " + path + ", or it is a directory!");
        }
        logger.warn("Unknown error occurred while trying to load items.");
        return new HashMap<>();
    }


    public Map<String, ItemStack> loadAllItems(InputStream itemStream) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, ItemStack> loadedItems = new HashMap<>();
        try {
            JsonNode rootNode = mapper.readTree(itemStream);
            Iterator<Map.Entry<String, JsonNode>> iterator = rootNode.fields();
            while(iterator.hasNext()) {
                Map.Entry<String, JsonNode> nextNode = iterator.next();
                // Double try catch, I know, pretty bad, but this is to ensure it won't skip the rest of the file if it fails to load an item
                try {
                    ItemStack newItem = loadItem(nextNode.getValue());
                    loadedItems.put(nextNode.getKey(), newItem);
                } catch (IllegalArgumentException e) {
                    logger.warn("Failed to load item: " + nextNode.getKey());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedItems;
    }

    public ItemStack loadItem(JsonNode itemNode) throws IllegalArgumentException {
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

        JsonNode formatStyleJSON = itemNode.get("style");
        ItemFormatStyle formatStyle = ItemFormatStyle.valueOf(formatStyleJSON == null ? "DEFAULT" : formatStyleJSON.asText("DEFAULT").toUpperCase());

        JsonNode itemNameJSON = itemNode.get("name");
        String ERROR_NAME = "Error: Name not Specified";
        String itemName = itemNameJSON == null ? ERROR_NAME : itemNameJSON.asText(ERROR_NAME);

        JsonNode modelDataJson = itemNode.get("custommodeldata");
        int modelData = modelDataJson == null ? 0 : modelDataJson.asInt(0);

        ArrayList<Component> lore = new ArrayList<>();
        JsonNode loreNode = itemNode.get("lore");
        if(loreNode != null && loreNode.isTextual()) {
            // TODO: Apply variables to string
            // Unfortunately, reading in a \n directly to a component just produces a line feed icon
            String[] lines = loreNode.asText().split("\n");
            for(String line : lines) {
                lore.add(Component.text(line, Style.style(formatStyle.getLoreColor(), TextDecoration.ITALIC.as(false))));
            }
        }

        ArrayList<ItemAttribute> attributes = new ArrayList<>();
        JsonNode attributeNode = itemNode.get("attributes");
        if(attributeNode != null && attributeNode.isArray()) {
            for (JsonNode attribute : attributeNode) {
                if (attribute.isObject()) {
                    // Get particular fields
                    JsonNode attributeName = attribute.get("attribute");
                    if (attributeName == null || !attributeName.isTextual()) {
                        logger.warn("Error loading item, attribute field is missing or wrong value type!");
                        continue;
                    }
                    JsonNode value = attribute.get("amount");
                    if (value == null || !value.isNumber()) {
                        logger.warn("Error loading item, amount field is missing or wrong value type!");
                        continue;
                    }
                    // Optional
                    JsonNode operationName = attribute.get("operation");
                    JsonNode slotName = attribute.get("slot");

                    Attribute actual = Attribute.fromKey("generic." + attributeName.asText().toLowerCase());
                    if (actual == null) {
                        logger.warn("Unknown attribute name: " + attributeName.asText());
                        continue;
                    }
                    AttributeOperation operation = AttributeOperation.ADDITION;
                    if (operationName != null) {
                        if (operationName.isNumber()) {
                            operation = AttributeOperation.fromId(operationName.asInt());
                            if (operation == null) {
                                logger.warn("Unknown attribute operation id: " + operationName.asInt());
                                continue;
                            }
                        }
                    }
                    AttributeSlot slot = AttributeSlot.MAINHAND;
                    if (slotName != null && slotName.isTextual()) {
                        try {
                            slot = AttributeSlot.valueOf(slotName.asText().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            logger.warn("Unknown attribute slot name: " + slotName.asText());
                            continue;
                        }
                    }
                    var newAttribute = new ItemAttribute(UUID.randomUUID(), "name", actual, operation, value.asDouble(), slot);
                    attributes.add(newAttribute);
                }
            }
        }

        HashMap<Enchantment, Short> enchantments = new HashMap<>();
        JsonNode enchantmentsNode = itemNode.get("enchantments");
        if(enchantmentsNode != null && enchantmentsNode.isObject()) {
            for (Iterator<Map.Entry<String, JsonNode>> it = enchantmentsNode.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> enchantment = it.next();
                // String holds enchant name, JsonNode holds enchant value
                var enchant = Enchantment.fromNamespaceId("minecraft:"+enchantment.getKey());
                if(enchant != null &&
                        enchantment.getValue() != null && enchantment.getValue().isNumber()) {
                    enchantments.put(enchant, enchantment.getValue().shortValue());
                }
            }
        }

        return ItemStack.builder(baseMaterial)
                .displayName(Component.text(itemName,
                        Style.style(formatStyle.getNameColor(), TextDecoration.ITALIC.as(false), TextDecoration.OBFUSCATED.as(false),
                                TextDecoration.BOLD.as(false), TextDecoration.STRIKETHROUGH.as(false), TextDecoration.UNDERLINED.as(false))))
                .meta(itemMetaBuilder -> itemMetaBuilder
                        .hideFlag(
                                //ItemHideFlag.HIDE_ATTRIBUTES,
                                //ItemHideFlag.HIDE_ENCHANTS,
                                ItemHideFlag.HIDE_UNBREAKABLE
                        )
                        .attributes(attributes)
                        .enchantments(enchantments)
                        .customModelData(modelData)
                        .unbreakable(true)
                        .lore(lore)
                )
                .amount(1)
                .build();
    }
}
