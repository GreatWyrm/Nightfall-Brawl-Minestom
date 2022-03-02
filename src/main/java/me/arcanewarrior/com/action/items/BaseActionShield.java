package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;

public abstract class BaseActionShield extends BaseActionItem {

    private boolean isHeldUp;

    public BaseActionShield(ActionPlayer player, ActionItemType type) {
        super(player, type);
    }

    /**
     * Sets the status of the shield, true if the shield is being held, false otherwise
     * @param status The new status of the shield
     */
    public void setShieldStatus(boolean status) {
        isHeldUp = status;
    }
}
