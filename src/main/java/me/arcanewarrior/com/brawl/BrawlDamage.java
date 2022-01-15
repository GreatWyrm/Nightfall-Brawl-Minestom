package me.arcanewarrior.com.brawl;


import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.Nullable;

public class BrawlDamage {

    private final BrawlPlayer attacker;
    private final BrawlPlayer receiver;

    private double damageAmount;

    public BrawlDamage(@Nullable BrawlPlayer attacker, BrawlPlayer receiver, float damageAmount) {
        this.attacker = attacker;
        this.receiver = receiver;
        this.damageAmount = damageAmount;
    }

    public double getDamageAmount() {
        return damageAmount;
    }

    public void setDamageAmount(double damageAmount) {
        this.damageAmount = damageAmount;
    }

    public void fireKnockback() {
        if(attacker != null) {
            float yaw = attacker.getYaw();
            // Y value currently unused
            Vec knockback = new Vec(Math.sin(yaw * Math.PI/180), 0.1, -Math.cos(yaw * Math.PI/180));
            float power = (float) (0.4 + receiver.getCurrentDamagePercent()/50);
            receiver.applyKnockback(power, knockback);
        }

    }
}
