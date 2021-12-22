package me.arcanewarrior.com.damage;

import net.minestom.server.attribute.Attribute;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.entity.EntityShootEvent;
import net.minestom.server.event.item.ItemUpdateStateEvent;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
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
            }
        }
    }

    public void handleBowFire(@NotNull EntityShootEvent event) {
        System.out.println("Entity shoot event was called");
        System.out.println("Spread: " + event.getSpread());
        System.out.println("Power: " + event.getPower());
        System.out.println("Projectile: " + event.getProjectile().getEntityType().name());
    }

}
