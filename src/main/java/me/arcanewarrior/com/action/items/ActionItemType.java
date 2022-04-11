package me.arcanewarrior.com.action.items;

import me.arcanewarrior.com.action.ActionPlayer;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum ActionItemType {


    NYNEVE(Nyneve::new),
    PYRRHIC(Pyrrhic::new),
    HORN(Horn::new),
    KINDLED_LIGHT(KindledLight::new),
    WILDFIRE(Wildfire::new),

    ;


    private final Function<ActionPlayer, BaseActionItem> actionItemCreator;

    ActionItemType(BiFunction<ActionPlayer, ActionItemType, BaseActionItem> actionItemCreator) {
        this.actionItemCreator = player -> actionItemCreator.apply(player, this);
    }

    public BaseActionItem createActionItem(ActionPlayer player) {
        return actionItemCreator.apply(player);
    }

    public String getPrettyName() {
        // All lowercase
        String base = name().toLowerCase(Locale.ROOT);
        // Split on _, will replace with space in the loop
        String[] words = base.split("_");
        StringBuilder builder = new StringBuilder();
        for(String s : words) {
            builder.append(Character.toUpperCase(s.charAt(0)));
            builder.append(s.substring(1));
            builder.append(" ");
        }
        // Remove last space
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
