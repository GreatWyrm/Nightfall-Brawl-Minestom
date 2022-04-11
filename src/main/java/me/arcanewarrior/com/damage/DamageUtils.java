package me.arcanewarrior.com.damage;

import me.arcanewarrior.com.damage.bow.Arrow;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.attribute.ItemAttribute;
import org.jetbrains.annotations.NotNull;

public class DamageUtils {

    public static DamageInstance createDamageInstance(@NotNull EntityAttackEvent event) {
        Entity attacker = event.getEntity();
        if(event.getTarget() instanceof LivingEntity targetLiving) {
            // Calculate damage amount
            if(attacker instanceof LivingEntity attackerLiving) {
                int totalDamage = 0;
                int fireTicks = 0;
                for(EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack stack = attackerLiving.getEquipment(slot);
                    if(!stack.isAir()) {
                        // Sum damage from attribute
                        for(ItemAttribute attribute : stack.getMeta().getAttributes()) {
                            // If slots match and the attribute is attack damage
                            if (slot.equals(EquipmentSlot.fromAttributeSlot(attribute.slot()))
                                    && attribute.attribute().equals(Attribute.ATTACK_DAMAGE)) {

                                //TODO: Attribute operations aren't taken into consideration
                                totalDamage += attribute.amount();
                            }
                            if(slot == EquipmentSlot.MAIN_HAND) {
                                short fireAspect = stack.getMeta().getEnchantmentMap().getOrDefault(Enchantment.FIRE_ASPECT, (short) 0);
                                // Each level of fire aspect sets you on fire for 4 seconds
                                fireTicks = 4 * 20 * fireAspect;
                            }
                        }
                    }
                }
                // If there isn't any damage, set damage to 1
                if(totalDamage == 0) {
                    totalDamage = 1;
                }
                DamageInstance instance = new DamageInstance(targetLiving, DamageType.fromEntity(attackerLiving), totalDamage);
                instance.setFireTicks(fireTicks);
                return instance;
            } else if(attacker instanceof Arrow arrow) {
                return new DamageInstance(targetLiving, DamageType.fromProjectile(arrow.getShooter(), arrow), arrow.getFinalDamage());
            } else {
                return new DamageInstance(targetLiving, DamageType.fromEntity(attacker), 1f);
            }
        }
        return null;
    }
}
