package me.arcanewarrior.com.brawl;

import me.arcanewarrior.com.action.items.ActionItemType;

import java.util.EnumSet;

public class BrawlPlayerData {

    private int gamesPlayed;
    private Loadout currentLoadout;

    public BrawlPlayerData() {
        this.gamesPlayed = 0;
        this.currentLoadout = new Loadout("default", "Default", null, EnumSet.of(ActionItemType.NYNEVE));
    }

    public void setCurrentLoadout(Loadout loadout) {
        this.currentLoadout = loadout;
    }
    public Loadout getCurrentLoadout() {
        return currentLoadout;
    }
}
