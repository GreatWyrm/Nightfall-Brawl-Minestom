package me.arcanewarrior.com.brawl;


import net.minestom.server.coordinate.Vec;

public class BrawlDamage {

    private final BrawlPlayer attacker;
    private final BrawlPlayer reciever;

    private double damageAmount = 0;

    public BrawlDamage(BrawlPlayer attacker, BrawlPlayer reciever) {
        this.attacker = attacker;
        this.reciever = reciever;
    }

    public double getDamageAmount() {
        return damageAmount;
    }

    public void setDamageAmount(double damageAmount) {
        this.damageAmount = damageAmount;
    }

    public void fire() {
        float yaw = attacker.getYaw();
        // Y value currently unused
        Vec knockback = new Vec(Math.sin(yaw * Math.PI/180), 0.1, -Math.cos(yaw * Math.PI/180));
        float power = (float) (0.4 + reciever.getCurrentDamagePercent()/50);
        reciever.applyKnockback(power, knockback);
    }
}
