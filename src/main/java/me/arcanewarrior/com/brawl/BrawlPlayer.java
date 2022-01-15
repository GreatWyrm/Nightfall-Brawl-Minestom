package me.arcanewarrior.com.brawl;

import me.arcanewarrior.com.Misc;
import me.arcanewarrior.com.action.ActionPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

public class BrawlPlayer extends ActionPlayer {

    private final BrawlGame parentGame;


    public BrawlPlayer(Player player, BrawlGame parent) {
        super(player);
        parentGame = parent;
    }

    // Random offset, so we aren't updating a bunch of players on the same tick
    private final int tickOffset = Misc.randomInt(0, 2519);

    @Override
    public void update() {
        super.update();

        if(everyNthTick(20)) {
            updateActionBar();
        }
    }

    private void updateActionBar() {
        // TODO Format number
        player.sendActionBar(Component.text("Damage: " + damagePercentage, NamedTextColor.RED));
    }

    private boolean everyNthTick(int n) {
        return (parentGame.getTickCount() + tickOffset) % n == 0;
    }

    // ---- Damage ----
    private double damagePercentage = 0;

    public void onDamageAttack(BrawlDamage damage) {
        damage.setDamageAmount(9.2);
    }

    public void onDamageRecieve(BrawlDamage damage) {
        damagePercentage += damage.getDamageAmount();
        updateActionBar();
    }

    public double getCurrentDamagePercent() {
        return damagePercentage;
    }

    public void resetDamagePercent() { damagePercentage = 0; }

    public void applyKnockback(float strength, Vec knockback) {
        player.takeKnockback(strength, knockback.x(), knockback.z());
    }

    public float getYaw() {
        return player.getPosition().yaw();
    }
}
