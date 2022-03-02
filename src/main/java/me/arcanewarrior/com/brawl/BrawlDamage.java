package me.arcanewarrior.com.brawl;


import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BrawlDamage {

    private final BrawlPlayer attacker;
    private final BrawlPlayer receiver;
    private final ItemStack usedItem;

    private double damageAmount;

    public BrawlDamage(@Nullable BrawlPlayer attacker, BrawlPlayer receiver, @Nullable ItemStack usedItem, float damageAmount) {
        this.attacker = attacker;
        this.receiver = receiver;
        this.usedItem = usedItem;
        this.damageAmount = damageAmount;
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

    public void fireKnockback() {
        if(attacker != null) {
            float yaw = attacker.getYaw();
            Vec knockback = attacker.getPosition().sub(receiver.getPosition()).asVec();
            //Vec knockback = new Vec(Math.sin(yaw * Math.PI/180), receiver.getPosition().y() - attacker.getPosition().y(), -Math.cos(yaw * Math.PI/180));
            float power = (float) (0.4 + receiver.getCurrentDamagePercent()/50);
            receiver.applyKnockback(power, knockback);
        }
    }

    public ItemStack getUsedItem() {
        return usedItem;
    }
}
