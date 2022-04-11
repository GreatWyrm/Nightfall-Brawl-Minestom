package me.arcanewarrior.com.damage;

import net.minestom.server.MinecraftServer;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.TaskSchedule;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AttackCooldown {

    private final ConcurrentHashMap<UUID, Integer> cooldownTime = new ConcurrentHashMap<>();

    public AttackCooldown() {
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            for(UUID id : cooldownTime.keySet()) {
                cooldownTime.compute(id, (uuid, integer) -> integer - 1);
                if(cooldownTime.get(id) <= 0) {
                    cooldownTime.remove(id);
                }
            }
        }, TaskSchedule.immediate(), TaskSchedule.tick(1));
    }

    public void resetCooldown(Player player) {
        // From https://minecraft.fandom.com/wiki/Damage#Attack_cooldown
        float attackSpeed = player.getAttributeValue(Attribute.ATTACK_SPEED);
        int cooldownTicks = (int) (20f / attackSpeed);
        cooldownTime.put(player.getUuid(), cooldownTicks);
    }

    public float getCooldownDamageMultiplier(Player player) {
        // Formula from https://minecraft.fandom.com/wiki/Damage#Attack_cooldown
        if(!cooldownTime.containsKey(player.getUuid())) {
            return 1f;
        }
        float attackSpeed = player.getAttributeValue(Attribute.ATTACK_SPEED);
        int cooldownTicks = (int) (20f / attackSpeed);
        int ticksSinceLastAttacked = cooldownTicks - cooldownTime.get(player.getUuid());
        return (float) (0.2f + Math.pow(((ticksSinceLastAttacked + 0.5) / cooldownTicks), 2) * 0.8f);
    }
}
