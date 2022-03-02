package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;
import me.arcanewarrior.com.cooldown.Cooldown;
import me.arcanewarrior.com.cooldown.UseCooldown;

public class Horn extends BaseActionShield {
    private final Cooldown chargeCD = new UseCooldown(5*20, this::horn);

    public Horn(ActionPlayer player, ActionItemType type) {
        super(player, type);
    }

    @Override
    public void onPlayerInput(ActionInputType inputType) {
        switch (inputType) {
            case LEFT -> chargeCD.tryUse();
            case RIGHT -> chargeCD.update();
        }
    }

    @Override
    protected String getBaseItemName() {
        return "horn";
    }

    private void horn() {
        player.sendMessage("hron");
    }

    @Override
    public float getCooldown() {
        return 1 - chargeCD.getCooldownPercentage();
    }
}
