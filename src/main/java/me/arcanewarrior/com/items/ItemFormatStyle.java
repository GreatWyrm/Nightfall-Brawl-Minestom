package me.arcanewarrior.com.items;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum ItemFormatStyle {

    DEFAULT(NamedTextColor.AQUA, NamedTextColor.AQUA, NamedTextColor.DARK_AQUA, NamedTextColor.BLUE)
    ;


    private final TextColor nameColor;
    private final TextColor loreColor;
    private final TextColor leftClickColor;
    private final TextColor rightClickColor;

    ItemFormatStyle(TextColor color, TextColor loreColor, TextColor leftClickColor, TextColor rightClickColor) {
        this.nameColor = color;
        this.loreColor = loreColor;
        this.leftClickColor = leftClickColor;
        this.rightClickColor = rightClickColor;
    }

    public TextColor getNameColor() {
        return nameColor;
    }

    public TextColor getLoreColor() { return loreColor; }

    public TextColor getLeftClickColor() {
        return leftClickColor;
    }

    public TextColor getRightClickColor() {
        return rightClickColor;
    }
}
