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
    private final int baseKnockback;
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
            baseKnockback = usedItem.getTag(Tag.Integer(BrawlTags.NBT_BASE_KNOCKBACK_KEY));
            knockbackScaling = usedItem.getTag(Tag.Double(BrawlTags.NBT_SCALING_KNOCKBACK_KEY));
        } else {
            baseKnockback = 25;
            knockbackScaling = 5;
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
            // float yaw = attacker.getYaw();
            //Vec knockback = new Vec(Math.sin(yaw * Math.PI/180), receiver.getPosition().y() - attacker.getPosition().y(), -Math.cos(yaw * Math.PI/180));

            // Knockback calculation, includes:
            // Damage of attack / 40
            // Base Knockback / 100
            // Knockback scaling * damagePercent / 200
            double power = damageAmount / 40d + baseKnockback / 100d + knockbackScaling * receiver.getCurrentDamagePercent()/200;
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
