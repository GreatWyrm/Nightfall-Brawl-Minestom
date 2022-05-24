package me.arcanewarrior.com.brawl;

public enum GameState {
    LOBBY,
    COUNTDOWN,
    GAME,
    END
    ;

    public GameState previousState() {
        int ord = ordinal();
        if (ord == 0) {
            return null;
        } else {
            return values()[ord-1];
        }
    }

    public void onTransitionTo(BrawlGame game) {
        switch (this) {
            case GAME -> game.startGame();
        }
    }
}
