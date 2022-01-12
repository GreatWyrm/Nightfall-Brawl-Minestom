package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;
import net.minestom.server.entity.Entity;

public abstract class BaseActionBow extends BaseActionItem {

    private long bowStartDrawing = -1;

    public BaseActionBow(ActionPlayer player, ActionItemType type) {
        super(player, type);
    }

    public void setBowStartDrawing(long startDrawing) {
        this.bowStartDrawing = startDrawing;
    }

    public void resetBowStartDrawing() { bowStartDrawing = -1; }

    protected long getBowStartDraw() { return bowStartDrawing; }

    /**
     * Called when the player fires this ActionBow
     * @param projectile The projectile they are shooting
     * @param power The power of the projectile, ranges from 0 to 3
     * @return True to allow the bow to fire, false otherwise
     */
    public boolean OnBowFire(Entity projectile, double power) {
        return false;
    }
}
