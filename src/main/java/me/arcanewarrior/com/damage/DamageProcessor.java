package me.arcanewarrior.com.damage;

import me.arcanewarrior.com.damage.bow.Arrow;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.entity.EntityShootEvent;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.attribute.ItemAttribute;
import org.jetbrains.annotations.NotNull;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class DamageProcessor {


    public void handleEntityDamage(@NotNull EntityDamageEvent event) {
        System.out.println("Entity damage event was called");
        System.out.println("DamageType: " + event.getDamageType());
        System.out.println("Damage Amount: " + event.getDamage());
    }

    public void processEntityAttackEvent(@NotNull EntityAttackEvent event) {
        Entity attacker = event.getEntity();
        Entity attackee = event.getTarget();
        if(attackee instanceof LivingEntity targetLiving) {

            // Calculate damage amount
            if(attacker instanceof LivingEntity attackerLiving) {
                // Get items in hands and armor slots
                Map<EquipmentSlot, ItemStack> checkedItems = new HashMap<>();
                for(EquipmentSlot slot : EquipmentSlot.values()) {
                    checkedItems.put(slot, attackerLiving.getEquipment(slot));
                }
                int totalDamage = 0;
                for(Map.Entry<EquipmentSlot, ItemStack> slotStackPair : checkedItems.entrySet()) {
                    EquipmentSlot slot = slotStackPair.getKey();
                    ItemStack stack = slotStackPair.getValue();
                    if(!stack.isAir()) {
                        // Sum damage from attribute
                        for(ItemAttribute attribute : stack.getMeta().getAttributes()) {
                            // If slots match and the attribute is attack damage
                            if (slot.equals(EquipmentSlot.fromAttributeSlot(attribute.getSlot()))
                                    && attribute.getAttribute().equals(Attribute.ATTACK_DAMAGE)) {

                                // Attribute operations aren't taken into consideration
                                totalDamage += attribute.getValue();
                            }
                            if(slot == EquipmentSlot.MAIN_HAND) {
                                short fireAspect = stack.getMeta().getEnchantmentMap().getOrDefault(Enchantment.FIRE_ASPECT, (short) 0);
                                targetLiving.setFireForDuration(4*fireAspect, ChronoUnit.SECONDS);
                            }
                        }
                    }
                }

                // If there isn't any damage, set damage to 1
                if(totalDamage == 0) {
                    totalDamage = 1;
                }

                targetLiving.damage(DamageType.fromEntity(event.getEntity()), totalDamage);
            } else if(attacker instanceof Arrow arrow) {
                targetLiving.damage(DamageType.fromProjectile(arrow.getShooter(), arrow), arrow.getFinalDamage());
            }
        }
    }

    public void handleEntityShoot(@NotNull EntityShootEvent event) {
        Entity shooter = event.getEntity();
        Entity projectile = event.getProjectile();
        // If this is an arrow, set damage and multiplier properly
        if(shooter instanceof LivingEntity livingEntity && projectile instanceof Arrow arrow) {
            ItemStack bowItem = livingEntity.getItemInMainHand();
            arrow.setBaseDamage(2);
            short powerLevel = bowItem.getMeta().getEnchantmentMap().getOrDefault(Enchantment.POWER, (short) 0);
            double power = event.getPower();
            if(powerLevel > 0) {
                // Formula from minecraft wiki: Power Damage multiplier = 1 + powerLevel * .25
                arrow.setMultiplier(((powerLevel + 1) * .25 + 1) * power);
            } else {
                arrow.setMultiplier(power);
            }
        }
    }
}
