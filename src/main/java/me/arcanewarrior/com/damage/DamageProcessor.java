package me.arcanewarrior.com.damage;

import me.arcanewarrior.com.damage.bow.Arrow;
import me.arcanewarrior.com.damage.invulnticks.InvulnerabilityTicks;
import me.arcanewarrior.com.damage.invulnticks.InvulnerabilityTicksConcurrentImpl;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.EntityDamage;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityShootEvent;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;


public class DamageProcessor {

    private final InvulnerabilityTicks invulnTickManager;
    private final AttackCooldown attackCooldowns;

    public DamageProcessor() {
        invulnTickManager = new InvulnerabilityTicksConcurrentImpl();
        attackCooldowns = new AttackCooldown();
    }

    public void processEntityAttackEvent(@NotNull EntityAttackEvent event) {
        DamageInstance instance = DamageUtils.createDamageInstance(event);

        if(instance != null) {
            // If the attacked entity isn't invulnerable, process the damage
            if(!invulnTickManager.isEntityInvulnerable(event.getTarget())) {
                invulnTickManager.setInvulnerabilityTicks(event.getTarget(), instance.getInvulnerableTicks());
                if(instance.getDamageType() instanceof EntityDamage entityDamage && entityDamage.getSource() instanceof Player player) {
                    instance.multiplyDamage(attackCooldowns.getCooldownDamageMultiplier(player));
                    System.out.println("Damage multiplier from cooldowns: " + attackCooldowns.getCooldownDamageMultiplier(player));
                    attackCooldowns.resetCooldown(player);
                }
                instance.applyDamage();
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

    public void handleSlotSwap(@NotNull PlayerChangeHeldSlotEvent event) {
        attackCooldowns.resetCooldown(event.getPlayer());
    }
}
