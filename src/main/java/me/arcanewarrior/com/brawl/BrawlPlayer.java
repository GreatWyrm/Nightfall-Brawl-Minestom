package me.arcanewarrior.com.brawl;

import me.arcanewarrior.com.Misc;
import me.arcanewarrior.com.action.ActionPlayer;
import me.arcanewarrior.com.action.items.ActionItemType;
import me.arcanewarrior.com.managers.ItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

public class BrawlPlayer extends ActionPlayer {

    private final BrawlGame parentGame;


    public BrawlPlayer(Player player, BrawlGame parent) {
        super(player);
        parentGame = parent;

        // Hardcoded kit
        giveActionItemType(ActionItemType.NYNEVE);
        giveActionItemType(ActionItemType.PYRRHIC);
        ItemManager.getManager().giveItemToPlayer("galvan", 64, player);
    }

    // Random offset, so we aren't updating a bunch of players on the same tick
    private final int tickOffset = Misc.randomInt(0, 2519);

    @Override
    public void update() {
        super.update();
    }

    private void updateActionBar() {
        // TODO Format number
        player.sendActionBar(Component.text("Damage: " + damagePercentage, NamedTextColor.RED));
    }

    private boolean everyNthTick(int n) {
        return (parentGame.getTickCount() + tickOffset) % n == 0;
    }

    // ---- Damage ----
    private BrawlDamageInfo lastBrawlDamage;

    private double damagePercentage = 0;

    public void onDamageAttack(BrawlDamage damage) {

    }

    public void onDamageRecieve(BrawlDamage damage) {
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

    public void applyKnockback(float strength, Vec knockback) {
        player.takeKnockback(strength, knockback.x(), knockback.z());
    }

    public Component getKnockoutMessage() {
        Component knockoutMessage = getDisplayName().hoverEvent(player.asHoverEvent());
        if(lastBrawlDamage != null) {
            return knockoutMessage.append(lastBrawlDamage.getKnockoutMessage());
        } else {
            return knockoutMessage.append(Component.text(" was knocked out."));
        }
    }

    public float getYaw() {
        return player.getPosition().yaw();
    }
}
