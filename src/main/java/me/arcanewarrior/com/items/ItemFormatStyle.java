package me.arcanewarrior.com.items;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum ItemFormatStyle {

    DEFAULT(NamedTextColor.AQUA)
    ;


    private final TextColor nameColor;

    ItemFormatStyle(TextColor color) {
        nameColor = color;
    }

    public TextColor getNameColor() {
        return nameColor;
    }
}
