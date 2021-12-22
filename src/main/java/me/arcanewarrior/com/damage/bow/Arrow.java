package me.arcanewarrior.com.damage.bow;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityProjectile;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Arrow extends EntityProjectile {

    public Arrow(@Nullable Entity shooter) {
        this(shooter, EntityType.ARROW);
    }

    public Arrow(@Nullable Entity shooter, @NotNull EntityType entityType) {
        super(shooter, entityType);
    }
}
