package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;
import me.arcanewarrior.com.managers.ItemManager;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;

import java.util.HashMap;

/**
 * @author ArcaneWarrior
 * An abstract class of an Action Item
 * An Action Item is defined as an item that will perform a function
 * upon being right-clicked, or left-clicked.
 */
public abstract class BaseActionItem {

    // Key for storing the type integer
    public static final String NBT_TYPE_KEY = "ActionItemType";

    protected final ActionPlayer player;
    private final ActionItemType type;

    public BaseActionItem(ActionPlayer player, ActionItemType type) {
        this.player = player;
        this.type = type;
    }

    /**
     * Called when the player left-clicks this item
     */
    public abstract void OnLeftClick();

    /**
     * Called when the player right-clicks this item
     */
    public abstract void OnRightClick();

    /**
     * Called once every tick (20 times a second)
     */
    public void update() {}

    /**
     * Get the base string name of the item
     * This is used to look the item up in the item manager
     */
    protected abstract String getBaseItemName();

    public ItemStack getBaseItem() {
        ItemStack stack = ItemManager.getManager().getItem(getBaseItemName());
        return stack.withTag(Tag.Integer(NBT_TYPE_KEY), type.ordinal());
    }

    /**
     * Sets this current action item to either have an enchantment glint or not
     * @param shine True for the enchantment glint, false otherwise
     */
    protected void setItemShine(boolean shine) {
        // If on the cursor
        var inventory = player.getPlayer().getInventory();
        if(doesItemMatch(inventory.getCursorItem())) {
            inventory.setCursorItem(setShineOnStack(inventory.getCursorItem(), shine));
        } else {
            for(int i = 0; i < inventory.getSize(); i++) {
                if(doesItemMatch(inventory.getItemStack(i))) {
                    inventory.replaceItemStack(i, itemStack -> setShineOnStack(itemStack, shine));
                }
            }
        }
    }

    private ItemStack setShineOnStack(ItemStack stack, boolean shine) {
        if(shine && !stack.getMeta().getEnchantmentMap().containsKey(Enchantment.VANISHING_CURSE)) {
            var currentEnchantments = new HashMap<>(stack.getMeta().getEnchantmentMap());
            currentEnchantments.put(Enchantment.VANISHING_CURSE, (short) 1);
            return stack.withMeta(meta -> meta.enchantments(currentEnchantments));
        } else if(!shine && stack.getMeta().getEnchantmentMap().containsKey(Enchantment.VANISHING_CURSE)) {
            var newEnchantments = new HashMap<>(stack.getMeta().getEnchantmentMap());
            newEnchantments.remove(Enchantment.VANISHING_CURSE);
            return stack.withMeta(meta -> meta.enchantments(newEnchantments));
        }
        // Wasn't valid, nothing was modified, return the original
        return stack;
    }

    public boolean doesItemMatch(ItemStack other) {
        if(other == null) {
            return false;
        }
        Tag<Integer> intTag = Tag.Integer(NBT_TYPE_KEY);
        return other.hasTag(intTag) && other.getTag(intTag) == type.ordinal();
    }

    protected boolean isHoldingItem() {
        return doesItemMatch(player.getPlayer().getItemInMainHand());
    }

    public float getCooldown() {
        return 0;
    }
}
