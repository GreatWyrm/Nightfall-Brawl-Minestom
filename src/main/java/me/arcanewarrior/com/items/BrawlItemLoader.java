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
        double baseKB = node.node("base-kb").getDouble(10);
        double scalingKB = node.node("scaling-kb").getDouble(2);

        return base.withMeta(meta -> {
            meta.setTag(Tag.Double(BrawlTags.NBT_BASE_KNOCKBACK_KEY), baseKB);
            meta.setTag(Tag.Double(BrawlTags.NBT_SCALING_KNOCKBACK_KEY), scalingKB);
            return meta;
        });
    }
}
