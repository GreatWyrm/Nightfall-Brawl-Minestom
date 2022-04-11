package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;

public class Wildfire extends BaseActionItem {

    public Wildfire(ActionPlayer player, ActionItemType type) {
        super(player, type);
    }

    @Override
    public void onPlayerInput(ActionInputType inputType) {

    }

    @Override
    protected String getBaseItemName() {
        return null;
    }
}
