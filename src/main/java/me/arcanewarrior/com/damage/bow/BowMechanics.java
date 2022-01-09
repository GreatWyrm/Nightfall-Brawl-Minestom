package me.arcanewarrior.com.damage.bow;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.item.ItemUpdateStateEvent;
import net.minestom.server.event.player.PlayerItemAnimationEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class BowMechanics {

    private final HashMap<UUID, Long> bowFirstDrawn = new HashMap<>();

    private final Logger bowLogger = LoggerFactory.getLogger(BowMechanics.class);

    /**
     * Should only be called with an ItemUpdateStateEvent with either a bow or a crossbow
     * @param event Event with a Bow or a Crossbow
     */
    public void handleBowUpdateState(@NotNull ItemUpdateStateEvent event) {
        Player player = event.getPlayer();
        double power;
        double defaultSpread = 1d;
        if(bowFirstDrawn.containsKey(player.getUuid())) {
            long timeDrawn = System.currentTimeMillis() - bowFirstDrawn.get(player.getUuid());
            power = getBowPower(timeDrawn);
        } else {
            bowLogger.warn("Player does not have a bowFirstDrawn entry!");
            power = 0.5;
        }

        Arrow arrow = new Arrow(player);
        if(power >= 0.98) {
            arrow.setCritical(true);
        }
        if(player.getInstance() != null) {
            // Magic Math, with thanks to https://github.com/Bloepiloepi/MinestomPvP/blob/f470919d55a165d256ae0b92deff4f7d62ae2809/src/main/java/io/github/bloepiloepi/pvp/projectile/ProjectileListener.java#L246
            Pos position = player.getPosition().add(0D, player.getEyeHeight(), 0D);
            arrow.setInstance(player.getInstance(), position.sub(0, 0.10000000149011612D, 0));
            Vec direction = position.direction();
            position = position.add(direction).sub(0, 0.2, 0);
            arrow.shoot(position, power * 3, defaultSpread);
        } else {
            bowLogger.warn("Player tried to fire an arrow in a null instance!");
        }
    }

    public void bowStartDrawing(@NotNull PlayerItemAnimationEvent event) {
        bowFirstDrawn.put(event.getPlayer().getUuid(), System.currentTimeMillis());
    }

    // Magic Math courtesy of https://github.com/Bloepiloepi/MinestomPvP/blob/f470919d55a165d256ae0b92deff4f7d62ae2809/src/main/java/io/github/bloepiloepi/pvp/projectile/ProjectileListener.java#L346
    private double getBowPower(long useDurationMillis) {
        double seconds = useDurationMillis / 1000.0;
        double power = (seconds * seconds + seconds * 2.0) / 3.0;
        if (power > 1) {
            power = 1;
        }
        return power;
    }
}
