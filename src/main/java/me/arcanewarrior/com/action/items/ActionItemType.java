package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum ActionItemType {


    NYNEVE(Nyneve::new),
    PYRRHIC(Pyrrhic::new),
    HORN(Horn::new),

    ;


    private final Function<ActionPlayer, BaseActionItem> actionItemCreator;

    ActionItemType(BiFunction<ActionPlayer, ActionItemType, BaseActionItem> actionItemCreator) {
        this.actionItemCreator = player -> actionItemCreator.apply(player, this);
    }

    public BaseActionItem createActionItem(ActionPlayer player) {
        return actionItemCreator.apply(player);
    }
}
