package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;
import me.arcanewarrior.com.managers.ItemManager;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;

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

    public abstract void OnLeftClick();
    public abstract void OnRightClick();

    /**
     * Get the base string name of the item
     * This is used to look the item up in the item manager
     */
    protected abstract String getBaseItemName();

    public ItemStack getBaseItem() {
        ItemStack stack = ItemManager.getManager().getItem(getBaseItemName());
        return stack.withTag(Tag.Integer(NBT_TYPE_KEY), type.ordinal());
    }

    // TODO: Figure out how to easily write type as an NBT tag

    public boolean doesItemMatch(ItemStack other) {
        if(other == null) {
            return false;
        }
        Tag<Integer> intTag = Tag.Integer(NBT_TYPE_KEY);
        return other.hasTag(intTag) && other.getTag(intTag) == type.ordinal();
    }
}
