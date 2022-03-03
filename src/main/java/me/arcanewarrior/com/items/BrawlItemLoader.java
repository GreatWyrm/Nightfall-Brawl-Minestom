package me.arcanewarrior.com.items;

import me.arcanewarrior.com.brawl.BrawlTags;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class BrawlItemLoader extends ItemLoader {

    @Override
    protected ItemStack createItemFromNode(CommentedConfigurationNode node) {
        ItemStack base = super.createItemFromNode(node);
        // After loading in base, check and apply NBT tags
        int baseKB = node.node("base-kb").getInt(25);
        double scalingKB = node.node("scaling-kb").getDouble(5);

        return base.withMeta(meta -> {
            meta.setTag(Tag.Integer(BrawlTags.NBT_BASE_KNOCKBACK_KEY), baseKB);
            meta.setTag(Tag.Double(BrawlTags.NBT_SCALING_KNOCKBACK_KEY), scalingKB);
            return meta;
        });
    }
}
