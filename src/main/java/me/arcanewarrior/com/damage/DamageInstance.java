package me.arcanewarrior.com.damage;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.DamageType;
import org.jetbrains.annotations.NotNull;

import java.time.temporal.ChronoUnit;

/**
 * Represents an instance of Damage to an entity, containing a DamageType and other information about it
 */
public class DamageInstance {

    private static final int DEFAULT_INVULN_TICKS = 10;

    /** The LivingEntity taking the damage */
    private final LivingEntity receiver;
    /** The DamageType associated with this damage */
    private final DamageType damageType;
    /** The amount of damage to deal */
    private float damageAmount;
    /** The amount of fire ticks to apply */
    private int fireTicks = 0;
    /** The amount of invulnerability ticks this damage will grant */
    private int invulnTicks = DEFAULT_INVULN_TICKS;

    public DamageInstance(@NotNull LivingEntity receiver, DamageType damageType, float damageAmount) {
        this.receiver = receiver;
        this.damageType = damageType;
        this.damageAmount = damageAmount;
    }

    /**
     * Applies the damage to the target
     */
    public void applyDamage() {
        receiver.damage(damageType, damageAmount);
        if(fireTicks > 0) {
            receiver.setFireForDuration(fireTicks * MinecraftServer.TICK_MS, ChronoUnit.MILLIS);
        }
    }

    public int getInvulnTicks() {
        return invulnTicks;
    }

    public void setInvulnTicks(int invulnTicks) {
        this.invulnTicks = invulnTicks;
    }

    public void setFireTicks(int fireTicks) {
        this.fireTicks = fireTicks;
    }
}
