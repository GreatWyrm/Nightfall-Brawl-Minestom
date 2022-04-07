package me.arcanewarrior.com.brawl;

import me.arcanewarrior.com.action.items.ActionItemType;

import java.util.Set;

public record Loadout(String loadoutID, Set<ActionItemType> actionItems) {


    public void applyToPlayer(BrawlPlayer player) {
        for(ActionItemType type : actionItems) {
            player.giveActionItemType(type);
        }
    }
}
