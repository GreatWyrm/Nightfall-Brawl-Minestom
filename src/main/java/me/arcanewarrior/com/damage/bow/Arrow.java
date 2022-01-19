package me.arcanewarrior.com.damage.bow;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityProjectile;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.arrow.AbstractArrowMeta;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Arrow extends EntityProjectile {

    private double baseDamage;
    private double multiplier;
    // The bow that this arrow was fired from
    private ItemStack bowItem;

    public Arrow(@Nullable Entity shooter) {
        this(shooter, EntityType.ARROW);
    }

    public Arrow(@Nullable Entity shooter, @NotNull EntityType entityType) {

        super(shooter, entityType);
    }

    public void setCritical(boolean critical) {
        if(getEntityMeta() instanceof AbstractArrowMeta arrowMeta) {
            arrowMeta.setCritical(critical);
        }
    }

    public void setBowItemStack(ItemStack bowItem) {
        this.bowItem = bowItem;
    }

    public ItemStack getBowItem() {
        return bowItem;
    }


    public void setBaseDamage(double baseDamage) {
        this.baseDamage = baseDamage;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public float getFinalDamage() {
        boolean critical = false;
        if(getEntityMeta() instanceof AbstractArrowMeta arrowMeta) {
            critical = arrowMeta.isCritical();
        }
        return (float) (baseDamage * multiplier * (critical ? 1.25 : 1));
    }
}
