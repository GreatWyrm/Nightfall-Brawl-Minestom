package me.arcanewarrior.com.damage;

import me.arcanewarrior.com.damage.bow.Arrow;
import me.arcanewarrior.com.damage.invulnticks.InvulnerabilityTicks;
import me.arcanewarrior.com.damage.invulnticks.InvulnerabilityTicksImpl;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityShootEvent;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;


public class DamageProcessor {

    private final InvulnerabilityTicks invulnTickManager;

    public DamageProcessor() {
        invulnTickManager = new InvulnerabilityTicksImpl();
    }

    public void processEntityAttackEvent(@NotNull EntityAttackEvent event) {
        DamageInstance instance = DamageUtils.createDamageInstance(event);

        if(instance != null) {
            // If the attacked entity isn't invulnerable, process the damage
            if(!invulnTickManager.isEntityInvulnerable(event.getTarget())) {
                instance.applyDamage();
                invulnTickManager.setInvulnerabilityTicks(event.getTarget(), instance.getInvulnerableTicks());
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
