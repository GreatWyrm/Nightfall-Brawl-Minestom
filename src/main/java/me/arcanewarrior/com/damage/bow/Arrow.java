package me.arcanewarrior.com.damage.bow;

import me.arcanewarrior.com.damage.MultiPartDamage;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityProjectile;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.arrow.AbstractArrowMeta;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Arrow extends EntityProjectile {

    private final MultiPartDamage arrowDamage;
    // The bow that this arrow was fired from
    private ItemStack bowItem;

    public Arrow(@Nullable Entity shooter) {
        this(shooter, EntityType.ARROW);
    }

    public Arrow(@Nullable Entity shooter, @NotNull EntityType entityType) {
        super(shooter, entityType);
        arrowDamage = new MultiPartDamage(2);
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

    public void multiplyDamage(double multiplier) {
        arrowDamage.timesMult(multiplier);
    }

    public float getFinalDamage() {
        boolean critical = false;
        if(getEntityMeta() instanceof AbstractArrowMeta arrowMeta) {
            critical = arrowMeta.isCritical();
        }
        if(critical) {
            // Avoid modifiying the arrowDamage variable, clone and return
            MultiPartDamage damage = arrowDamage.cloneDamage();
            damage.timesMult(1.25);
            return (float) damage.getValue();
        } else {
            return (float) arrowDamage.getValue();
        }
    }
}
