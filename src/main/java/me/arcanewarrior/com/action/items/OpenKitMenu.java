package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;
import me.arcanewarrior.com.managers.LoadoutManager;

public class OpenKitMenu extends BaseActionItem {
    public OpenKitMenu(ActionPlayer player, ActionItemType type) {
        super(player, type);
    }

    @Override
    public void onPlayerInput(ActionInputType inputType) {
        LoadoutManager.getManager().displayLoadoutMenu(player.getPlayer());
    }

    @Override
    protected String getBaseItemName() {
        return "open-loadout-menu";
    }
}
