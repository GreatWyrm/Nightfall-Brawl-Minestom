package me.arcanewarrior.com.items;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum ItemFormatStyle {

    DEFAULT(NamedTextColor.AQUA, Style.style(NamedTextColor.AQUA, TextDecoration.ITALIC.withState(false)), NamedTextColor.DARK_AQUA, NamedTextColor.BLUE)
    ;


    private final TextColor nameColor;
    private final Style loreStyle;
    private final TextColor leftClickColor;
    private final TextColor rightClickColor;

    ItemFormatStyle(TextColor color, Style loreStyle, TextColor leftClickColor, TextColor rightClickColor) {
        this.nameColor = color;
        this.loreStyle = loreStyle;
        this.leftClickColor = leftClickColor;
        this.rightClickColor = rightClickColor;
    }

    public TextColor getNameColor() {
        return nameColor;
    }

    public Style getLoreStyle() { return loreStyle; }

    public TextColor getLeftClickColor() {
        return leftClickColor;
    }

    public TextColor getRightClickColor() {
        return rightClickColor;
    }
}
