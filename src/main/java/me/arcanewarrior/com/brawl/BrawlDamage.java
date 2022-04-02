package me.arcanewarrior.com.brawl;


import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BrawlDamage {

    private final BrawlPlayer attacker;
    private final BrawlPlayer receiver;
    private final ItemStack usedItem;

    // Regards to knockback
    private double damageAmount;
    private final double baseKnockback;
    private final double knockbackScaling;

    public BrawlDamage(@NotNull BrawlPlayer attacker, BrawlPlayer receiver, float damageAmount) {
        this(attacker, receiver, attacker.getPlayer().getItemInMainHand(), damageAmount);
    }
    public BrawlDamage(@Nullable BrawlPlayer attacker, BrawlPlayer receiver, @Nullable ItemStack usedItem, float damageAmount) {
        this.attacker = attacker;
        this.receiver = receiver;
        this.usedItem = usedItem;
        this.damageAmount = damageAmount;
        if(usedItem != null && !usedItem.isAir()) {
            if(usedItem.hasTag(Tag.Double(BrawlTags.NBT_BASE_KNOCKBACK_KEY))) {
                this.baseKnockback = usedItem.getTag(Tag.Double(BrawlTags.NBT_BASE_KNOCKBACK_KEY));
            } else {
                this.baseKnockback = 10;
            }
            if(usedItem.hasTag(Tag.Double(BrawlTags.NBT_SCALING_KNOCKBACK_KEY))) {
                this.knockbackScaling = usedItem.getTag(Tag.Double(BrawlTags.NBT_SCALING_KNOCKBACK_KEY));
            } else {
                this.knockbackScaling = 2;
            }
        } else {
            this.baseKnockback = 10;
            this.knockbackScaling = 2;
        }
    }

    public double getDamageAmount() {
        return damageAmount;
    }

    public void setDamageAmount(double damageAmount) {
        this.damageAmount = damageAmount;
    }

    public BrawlPlayer getAttacker() {
        return attacker;
    }

    private void fireKnockback() {
        if(attacker != null) {
            Vec knockback = attacker.getPosition().sub(receiver.getPosition()).asVec();
            // Knockback calculation, includes:
            // Damage of attack / 30 + damage total
            // Base Knockback / 100
            // Knockback scaling * damagePercent / 200
            double power = damageAmount / 30d + baseKnockback / 100d + knockbackScaling * receiver.getCurrentDamagePercent()/200;
            receiver.applyKnockback(power, knockback);
        }
    }

    public ItemStack getUsedItem() {
        return usedItem;
    }

    public void fire() {
        // Notify players
        if(attacker != null) {
            attacker.onDamageAttack(this);
        }
        receiver.onDamageReceive(this);

        fireKnockback();
    }
}
