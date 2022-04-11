package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;

public class KindledLight extends BaseActionItem {
    public KindledLight(ActionPlayer player, ActionItemType type) {
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
