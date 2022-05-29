package me.arcanewarrior.com.brawl;

import me.arcanewarrior.com.Misc;
import me.arcanewarrior.com.action.ActionPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.ExplosionPacket;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;

import java.text.DecimalFormat;

public class BrawlPlayer extends ActionPlayer {

    private final BrawlGame parentGame;


    public BrawlPlayer(Player player, BrawlGame parent) {
        super(player);
        parentGame = parent;
    }

    // Random offset, to spread out various ticking things
    private final int tickOffset = Misc.randomInt(0, 2519);

    @Override
    public void update() {
        super.update();

        if(everyNthTick(20)) {
            updateActionBar();
        }
    }

    private final DecimalFormat df = new DecimalFormat("###.##");
    private void updateActionBar() {
        player.sendActionBar(Component.text("Damage: " + df.format(damagePercentage), NamedTextColor.RED));
    }

    public void modifyGravity(int negativeModifier, int duration) {
        // Annoying workaround, Java thinks that inlining the byte variable in the constructor is an int, not a byte
        byte b = Potion.AMBIENT_FLAG | Potion.ICON_FLAG | Potion.PARTICLES_FLAG;
        player.addEffect(new Potion(PotionEffect.LEVITATION, (byte) negativeModifier, duration, b));
    }

    private boolean everyNthTick(int n) {
        return (parentGame.getTickCount() + tickOffset) % n == 0;
    }

    // ---- Damage ----
    private BrawlDamageInfo lastBrawlDamage;

    private double damagePercentage = 0;

    public void onDamageAttack(BrawlDamage damage) {

    }

    public void onDamageReceive(BrawlDamage damage) {
        damagePercentage = Math.min(999, damagePercentage + damage.getDamageAmount());
        updateActionBar();
        if(damage.getAttacker() != null) {
            lastBrawlDamage = new BrawlDamageInfo(damage.getAttacker(), damage.getUsedItem());
        }
    }

    public double getCurrentDamagePercent() {
        return damagePercentage;
    }

    public void resetDamage() {
        damagePercentage = 0;
        lastBrawlDamage = null;
        updateActionBar();
    }

    public void applyKnockback(double strength, Vec knockback) {
        if(player.isSneaking() && player.isOnGround()) {
            strength *= 0.7;
        }

        // Prevent knockback from overflowing when using standard velocity packets
        if(damagePercentage > 100) {
            if(strength > 0) {
                // Get current Velocity
                Vec currentVel = player.getVelocity();
                Vec knockbackVel = knockback.normalize();
                // Calculation, take into account the current velocity and the new velocity
                strength *= 0.9;
                Vec newVelocity = new Vec(
                        currentVel.x() / 1.2d - knockbackVel.x() * strength, // x
                        player.isOnGround() ? Math.max(0.8f, currentVel.x() / 2d + knockbackVel.y() * strength) : currentVel.x() / 2d + knockbackVel.y() * strength, // y
                        currentVel.z() / 1.2d - knockbackVel.z() * strength // z
                );
                // Rough formula, this, velocity.x/2 * 10 blocks traveled
                ExplosionPacket packet = new ExplosionPacket((float) player.getPosition().x(), (float) player.getPosition().y(), (float) player.getPosition().z(),
                        -1f, new byte[0], (float) newVelocity.x(), (float) newVelocity.y(), (float) newVelocity.z());
                player.sendPacket(packet);
            }
        } else {
            player.takeKnockback((float) strength, knockback.x(), knockback.z());
        }
        modifyGravity(-5, 15);
    }

    public Component getKnockoutMessage() {
        Component knockoutMessage = getDisplayName().hoverEvent(player.asHoverEvent());
        if(lastBrawlDamage != null) {
            return knockoutMessage.append(lastBrawlDamage.getKnockoutMessage());
        } else {
            return knockoutMessage.append(Component.text(" was knocked out."));
        }
    }

    public void toggleReadyState() {
        parentGame.toggleReadyState(this);
    }

    public void reset() {
        player.getInventory().clear();
        clearActionInventory();
    }
}
