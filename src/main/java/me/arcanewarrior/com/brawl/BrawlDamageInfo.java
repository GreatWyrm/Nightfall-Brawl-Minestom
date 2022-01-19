package me.arcanewarrior.com.brawl;

import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;


/**
 * A class containing data relating to BrawlDamage, tracking the last person to hit this player and who to credit for a knockout
 */
public record BrawlDamageInfo(@NotNull BrawlPlayer lastAttacker, ItemStack itemUsed) {

    public Component getKnockoutMessage() {
        Component base = Component.text(" was knocked out by ")
                .append(lastAttacker.getDisplayName()
                        .hoverEvent(lastAttacker.getPlayer().asHoverEvent()));
        if(itemUsed != null && !itemUsed.isAir() && itemUsed.getDisplayName() != null) {
            return base.append(Component.text(" using "))
                    .append(itemUsed.getDisplayName()
                            .hoverEvent(itemUsed.asHoverEvent())
                    ).append(Component.text("."))
                    ;
        } else {
            return base.append(Component.text("."));
        }
    }
}
