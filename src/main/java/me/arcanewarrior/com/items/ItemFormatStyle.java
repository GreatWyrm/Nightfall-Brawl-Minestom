package me.arcanewarrior.com.items;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum ItemFormatStyle {

    DEFAULT(NamedTextColor.AQUA, NamedTextColor.AQUA)
    ;


    private final TextColor nameColor;
    private final TextColor loreColor;

    ItemFormatStyle(TextColor color, TextColor loreColor) {
        nameColor = color;
        this.loreColor = loreColor;
    }

    public TextColor getNameColor() {
        return nameColor;
    }

    public TextColor getLoreColor() { return loreColor; }
}
