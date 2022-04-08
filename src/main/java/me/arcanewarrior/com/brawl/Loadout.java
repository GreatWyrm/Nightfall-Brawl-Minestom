package me.arcanewarrior.com.brawl;

import me.arcanewarrior.com.action.items.ActionItemType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.Set;

public record Loadout(String loadoutID, String displayName, Set<ActionItemType> actionItems) {

    public void applyToPlayer(BrawlPlayer player) {
        for(ActionItemType type : actionItems) {
            player.giveActionItemType(type);
        }
    }

    public ItemStack getLoadoutItemStack() {
        ArrayList<Component> loreComponents = new ArrayList<>();
        // Add description
        loreComponents.add(Component.text("Description").decoration(TextDecoration.ITALIC, false));
        loreComponents.add(Component.empty());

        loreComponents.add(Component.text("This Brawler has the following items:", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        // Add items in kit
        for(ActionItemType type : actionItems) {
            loreComponents.add(Component.text("-" + type.getPrettyName(), NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        }
        return ItemStack.builder(Material.DIAMOND)
                .displayName(Component.text(displayName, NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                .lore(loreComponents)
                .build();
    }
}
