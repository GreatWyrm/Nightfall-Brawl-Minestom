package me.arcanewarrior.com.managers;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.action.items.ActionItemType;
import me.arcanewarrior.com.brawl.Loadout;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.sound.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class LoadoutManager implements Manager {

    public static LoadoutManager getManager() { return GameCore.getGameCore().getManager(LoadoutManager.class); }

    private final Logger logger = LoggerFactory.getLogger(LoadoutManager.class);


    // Map of Slot ID to loadout
    private final HashMap<Integer, Loadout> loadoutList = new HashMap<>();
    private final Path loadoutFilePath =  Path.of("loadouts.yml");

    @Override
    public void initialize() {
        // Load all loadouts
        if(!Files.exists(loadoutFilePath)) {
            MinecraftServer.LOGGER.info("No " + loadoutFilePath + " (loadouts file) found! Creating file with defaults...");
            try {
                Files.copy(Objects.requireNonNull(LoadoutManager.class.getClassLoader().getResourceAsStream(loadoutFilePath.toString())), loadoutFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(loadoutFilePath)
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .build();
        try {
            CommentedConfigurationNode root = loader.load();
            for(var node : root.childrenMap().entrySet()) {
                parseLoadout(node.getKey().toString(), node.getValue());
            }
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        loadoutList.clear();
    }

    private void parseLoadout(String loadoutName, CommentedConfigurationNode node) throws SerializationException {
        // Get list of action items
        EnumSet<ActionItemType> actionItems = EnumSet.noneOf(ActionItemType.class);
        int index = -1;
        if(node.hasChild("items")) {
            for(String item : node.node("items").getList(String.class)) {
                String enumName = item.replace("-", "_").toUpperCase(Locale.ROOT);
                try {
                    ActionItemType actionItem = ActionItemType.valueOf(enumName);
                    actionItems.add(actionItem);
                } catch (IllegalArgumentException e) {
                    logger.warn("Unknown ActionItemType: " + enumName);
                }
            }
        }
        // Find index: Slot position in the menu
        if(node.hasChild("index")) {
            index = node.node("index").getInt();
        } else {
            logger.warn("No index specified for loadout: " + loadoutName);
        }
        // Find the name that will go on the display itemstack
        String displayName;
        if(node.hasChild("name")) {
            displayName = node.node("name").getString();
        } else {
            logger.warn("No name specified for loadout: " + loadoutName);
            displayName = Character.toUpperCase(loadoutName.charAt(0)) + loadoutName.substring(1);
        }
        String skinName;
        if(node.hasChild("skin")) {
            skinName = node.node("skin").getString();
        } else {
            logger.warn("No skin specified for loadout: " + loadoutName);
            skinName = null;
        }
        loadoutList.put(index, new Loadout(loadoutName, displayName, skinName, actionItems));
    }

    public void displayLoadoutMenu(Player player) {
        Inventory inventory = new Inventory(InventoryType.CHEST_3_ROW, "Brawl Loadout");

        for(var entry : loadoutList.entrySet()) {
            int index = entry.getKey();
            Loadout loadout = entry.getValue();
            if(index < 0 || index >= inventory.getInventoryType().getSize()) {
                logger.warn("Loadout " + loadout.loadoutID() + " has an invalid index!");
            }
            inventory.setItemStack(index, loadout.getLoadoutItemStack());
        }

        inventory.addInventoryCondition((p, slot, clickType, inventoryConditionResult) -> {
            if(loadoutList.containsKey(slot)) {
                BrawlPlayerDataManager.getManager().modifyPlayerData(p, data -> data.setCurrentLoadout(loadoutList.get(slot)));
                p.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1f, 1.5f), Sound.Emitter.self());
                inventoryConditionResult.setCancel(true);
            }
        });

        player.openInventory(inventory);
    }
}
