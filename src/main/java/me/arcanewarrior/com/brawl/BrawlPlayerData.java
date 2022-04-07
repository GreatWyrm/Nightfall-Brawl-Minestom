package me.arcanewarrior.com.brawl;

import me.arcanewarrior.com.managers.LoadoutManager;

public class BrawlPlayerData {

    private int gamesPlayed;
    private Loadout currentLoadout;

    public BrawlPlayerData() {
        this.gamesPlayed = 0;
        this.currentLoadout = LoadoutManager.getManager().getDefaultLoadout();
    }

    public void setCurrentLoadout(Loadout loadout) {
        this.currentLoadout = loadout;
    }
    public Loadout getCurrentLoadout() {
        return currentLoadout;
    }
}
