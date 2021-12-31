package me.arcanewarrior.com.actionitems;

public class Nyneve extends BaseActionItem {


    public Nyneve(ActionPlayer player, ActionItemType type) {
        super(player, type);
    }
    @Override
    public void OnLeftClick() {
        player.sendMessage("From Nyneve: Hello! L");
    }

    @Override
    public void OnRightClick() {
        player.sendMessage("From Nyneve: Hello! R");
    }

    @Override
    public String getBaseItemName() {
        return "nyneve";
    }
}
