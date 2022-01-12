package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;
import me.arcanewarrior.com.damage.bow.Arrow;
import net.minestom.server.entity.Entity;

public class Pyrrhic extends BaseActionBow {

    private int heldCounter = 0;
    private boolean empowered = false;

    public Pyrrhic(ActionPlayer player, ActionItemType type) {
        super(player, type);
    }

    @Override
    public void OnLeftClick() {

    }

    @Override
    public void OnRightClick() {

    }

    @Override
    public void update() {
        if (isHoldingItem() && getBowStartDraw() != -1) {
            heldCounter++;
            if (heldCounter > 5 * 20 && !empowered) {
                empowered = true;
                setItemShine(true);
            }
        } else {
            heldCounter = 0;
        }
    }

    @Override
    public boolean OnBowFire(Entity projectile, double power) {
        if(projectile instanceof Arrow arrow) {
            if(empowered) {
                empowered = false;
                setItemShine(false);
                heldCounter = 0;
            }
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