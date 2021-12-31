package me.arcanewarrior.com.actionitems;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum ActionItemType {


    NYNEVE(Nyneve::new);


    private final Function<ActionPlayer, BaseActionItem> actionItemCreator;

    ActionItemType(BiFunction<ActionPlayer, ActionItemType, BaseActionItem> actionItemCreator) {
        this.actionItemCreator = player -> actionItemCreator.apply(player, this);
    }

    public BaseActionItem createActionItem(ActionPlayer player) {
        return actionItemCreator.apply(player);
    }
}
