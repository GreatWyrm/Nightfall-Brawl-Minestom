package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;
import me.arcanewarrior.com.brawl.BrawlPlayer;

public class ReadyItem extends BaseActionItem {
    public ReadyItem(ActionPlayer player, ActionItemType type) {
        super(player, type);
    }

    @Override
    public void onPlayerInput(ActionInputType inputType) {
        if(player instanceof BrawlPlayer brawlPlayer) {
            brawlPlayer.toggleReadyState();
        }
    }

    @Override
    protected String getBaseItemName() {
        return "ready-item";
    }
}
