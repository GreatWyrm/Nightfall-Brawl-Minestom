package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;
import me.arcanewarrior.com.cooldown.Cooldown;
import me.arcanewarrior.com.cooldown.UseCooldown;
import me.arcanewarrior.com.damage.bow.Arrow;
import net.minestom.server.entity.Entity;

public class Pyrrhic extends BaseActionBow {

    private final int maxChargeupTime = 4*20; // 4 seconds
    private int heldCounter = 0;
    private boolean empowered = false;

    private final Cooldown backHopCD = new UseCooldown(3*20, () -> player.dashTowardsFacing(-15));

    public Pyrrhic(ActionPlayer player, ActionItemType type) {
        super(player, type);
    }

    @Override
    public void onPlayerInput(ActionInputType inputType) {
        if (inputType == ActionInputType.LEFT) {
            backHopCD.tryUse();
        }
    }

    @Override
    public void update() {
        if (isHoldingItem() && getBowStartDraw() != -1) {
            heldCounter++;
            if (heldCounter > maxChargeupTime && !empowered) {
                empowered = true;
                setItemShine(true);
            }
        } else {
            heldCounter = 0;
        }
        backHopCD.update();
    }

    @Override
    public boolean OnBowFire(Entity projectile, double power) {
        if(projectile instanceof Arrow arrow) {
            if(empowered) {
                empowered = false;
                setItemShine(false);
                heldCounter = 0;
            }
            double windupPower = Math.min((double) heldCounter/maxChargeupTime, 1d);
            double multiplier = 1 + 1.5d * windupPower;
            arrow.multiplyDamage(multiplier);
            resetBowStartDrawing();
            return true;
        }
        return false;
    }

    @Override
    protected String getBaseItemName() {
        return "pyrrhic";
    }
}