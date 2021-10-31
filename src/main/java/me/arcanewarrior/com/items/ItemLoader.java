package me.arcanewarrior.com.items;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ItemLoader {

    private final Logger logger = LoggerFactory.getLogger(ItemLoader.class);
    private final String ERROR_NAME = "Error: Name not Specified";

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
        ItemFormatStyle formatStyle = ItemFormatStyle.valueOf(formatStyleJSON == null ? "DEFAULT" : formatStyleJSON.asText("DEFAULT"));

        JsonNode itemNameJSON = itemNode.get("name");
        String itemName = itemNameJSON == null ? ERROR_NAME : itemNameJSON.asText(ERROR_NAME);

        // TODO: Lore
        // TODO: Attributes
        // TODO: Enchants

        return ItemStack.builder(baseMaterial)
                .displayName(Component.text(itemName,
                        Style.style(formatStyle.getNameColor(), TextDecoration.ITALIC.as(false), TextDecoration.OBFUSCATED.as(false),
                                TextDecoration.BOLD.as(false), TextDecoration.STRIKETHROUGH.as(false), TextDecoration.UNDERLINED.as(false))))
                .meta(itemMetaBuilder -> itemMetaBuilder
                        .hideFlag(
                                ItemHideFlag.HIDE_ATTRIBUTES,
                                ItemHideFlag.HIDE_ENCHANTS,
                                ItemHideFlag.HIDE_UNBREAKABLE))
                .amount(1)
                .build();
    }
}
