package me.arcanewarrior.com.damage;

import me.arcanewarrior.com.damage.bow.Arrow;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityShootEvent;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class DamageProcessor {

    private final Task updateInvulTicks;

    public DamageProcessor() {
        updateInvulTicks = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            if(invulnerabilityTickTime.isEmpty()) return;

            for (UUID key : invulnerabilityTickTime.keySet()) {
                int ticks = invulnerabilityTickTime.compute(key, (uuid, integer) -> integer - 1);
                if (ticks == 0) {
                    invulnerabilityTickTime.remove(key);
                }
            }
        }, TaskSchedule.immediate(), TaskSchedule.tick(1));
    }

    private final HashMap<UUID, Integer> invulnerabilityTickTime = new HashMap<>();

    public void processEntityAttackEvent(@NotNull EntityAttackEvent event) {
        DamageInstance instance = DamageUtils.createDamageInstance(event);

        if(instance != null) {
            // If the attacked entity isn't invulnerable, process the damage
            if(!invulnerabilityTickTime.containsKey(event.getTarget().getUuid())) {
                instance.applyDamage();
                invulnerabilityTickTime.put(event.getTarget().getUuid(), instance.getInvulnTicks());
            }
        }
    }

    public void handleEntityShoot(@NotNull EntityShootEvent event) {
        Entity shooter = event.getEntity();
        Entity projectile = event.getProjectile();
        // If this is an arrow, set damage and multiplier properly
        if(shooter instanceof LivingEntity livingEntity && projectile instanceof Arrow arrow) {
            ItemStack bowItem = livingEntity.getItemInMainHand();
            short powerLevel = bowItem.getMeta().getEnchantmentMap().getOrDefault(Enchantment.POWER, (short) 0);
            double power = event.getPower();
            if(powerLevel > 0) {
                // Formula from minecraft wiki: Power Damage multiplier = 1 + powerLevel * .25
                arrow.multiplyDamage(((powerLevel + 1) * .25 + 1) * power);
            } else {
                arrow.multiplyDamage(power);
            }
            arrow.setBowItemStack(bowItem);
        }
    }
}
