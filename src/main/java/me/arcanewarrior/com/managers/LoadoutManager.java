package me.arcanewarrior.com.managers;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.action.items.ActionItemType;
import me.arcanewarrior.com.brawl.Loadout;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
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
    private final Loadout defaultLoadout = new Loadout("default", EnumSet.of(ActionItemType.NYNEVE));

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
        if(node.hasChild("index")) {
            index = node.node("index").getInt();
        } else {
            logger.warn("No index specified for loadout: " + loadoutName);
        }
        loadoutList.put(index, new Loadout(loadoutName, actionItems));
    }

    public void displayLoadoutMenu(Player brawlPlayer) {
        Inventory inventory = new Inventory(InventoryType.CHEST_3_ROW, "Brawl Loadout");

        for(var entry : loadoutList.entrySet()) {
            int index = entry.getKey();
            Loadout loadout = entry.getValue();
            if(index < 0 || index >= inventory.getInventoryType().getSize()) {
                logger.warn("Loadout " + loadout.loadoutID() + " has an invalid index!");
            }
            inventory.setItemStack(index, ItemStack.builder(Material.DIAMOND).displayName(Component.text(loadout.loadoutID(), NamedTextColor.AQUA)).build());
        }

        inventory.addInventoryCondition((player, slot, clickType, inventoryConditionResult) -> {
            if(loadoutList.containsKey(slot)) {
                //logger.info("Selected " + loadoutList.get(slot).loadoutID());
                BrawlPlayerDataManager.getManager().modifyPlayerData(player, data -> data.setCurrentLoadout(loadoutList.get(slot)));
                inventoryConditionResult.setCancel(true);
                // Play "ding" sound
            }
        });

        brawlPlayer.openInventory(inventory);
    }

    public Loadout getDefaultLoadout() {
        return defaultLoadout;
    }
}
