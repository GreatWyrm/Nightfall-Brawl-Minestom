package me.arcanewarrior.com.brawl;

import me.arcanewarrior.com.action.items.ActionInputType;
import me.arcanewarrior.com.damage.bow.Arrow;
import net.minestom.server.entity.damage.EntityDamage;
import net.minestom.server.entity.damage.EntityProjectileDamage;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.entity.EntityShootEvent;
import net.minestom.server.event.item.ItemUpdateStateEvent;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.event.player.PlayerItemAnimationEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.item.ItemStack;

public class BrawlEvents {

    private final BrawlGame parentGame;
    private final GlobalEventHandler eventHandler;

    public BrawlEvents(BrawlGame parentGame, GlobalEventHandler handler) {
        this.parentGame = parentGame;
        this.eventHandler = handler;
    }

    private final String eventNodeName = "brawl-events";

    public void registerEvents() {
        EventNode<EntityEvent> node = EventNode.type(eventNodeName, EventFilter.ENTITY);
        node.addListener(PlayerUseItemEvent.class, event -> {
            BrawlPlayer player = parentGame.getBrawlPlayer(event.getPlayer());
            if(player != null) {
                player.OnPlayerInput(ActionInputType.RIGHT);
            }
        });
        node.addListener(PlayerHandAnimationEvent.class, event -> {
            BrawlPlayer player = parentGame.getBrawlPlayer(event.getPlayer());
            if(player != null) {
                player.OnPlayerInput(ActionInputType.LEFT);
            }
        });
        // Bow
        node.addListener(PlayerItemAnimationEvent.class, event -> {
            BrawlPlayer player = parentGame.getBrawlPlayer(event.getPlayer());
            if(player != null) {
                if(event.getItemAnimationType() == PlayerItemAnimationEvent.ItemAnimationType.BOW || event.getItemAnimationType() == PlayerItemAnimationEvent.ItemAnimationType.CROSSBOW) {
                    player.OnStartDrawing(System.currentTimeMillis());
                } else if(event.getItemAnimationType() == PlayerItemAnimationEvent.ItemAnimationType.SHIELD) {
                    player.OnStartShielding(System.currentTimeMillis());
                }
            }
        });
        node.addListener(ItemUpdateStateEvent.class, event -> {
            BrawlPlayer player = parentGame.getBrawlPlayer(event.getPlayer());
            if(player != null) {
                player.updateItemState(event.getItemStack());
            }
        });
        node.addListener(EntityShootEvent.class, event -> {
            BrawlPlayer player = parentGame.getBrawlPlayer(event.getEntity());
            if(player != null) {
                boolean shouldFire = player.OnBowFire(event.getProjectile(), event.getPower());
                if(!shouldFire) {
                    event.setCancelled(true);
                }
            }
        });
        node.addListener(EntityDamageEvent.class, event -> {
            BrawlPlayer target = parentGame.getBrawlPlayer(event.getEntity().getUuid());
            // Prevent brawl players from taking damage
            if(target != null) {
                float damage = event.getDamage();
                // ENTITY DAMAGE
                if(event.getDamageType() instanceof EntityDamage entityDamage) {
                    BrawlPlayer attacker = parentGame.getBrawlPlayer(entityDamage.getSource().getUuid());
                    // It's fine if we have a null attacker, just be wary of it when calculating damage
                    BrawlDamage brawlDamage;
                    if(attacker != null) {
                        brawlDamage = new BrawlDamage(attacker, target, damage);
                    } else {
                        brawlDamage = new BrawlDamage(null, target, null, damage);
                    }
                    brawlDamage.fire();
                // BOW DAMAGE
                } else if(event.getDamageType() instanceof EntityProjectileDamage projectileDamage) {
                    ItemStack usedItem;
                    if(projectileDamage.getProjectile() instanceof Arrow arrow) {
                        damage = arrow.getFinalDamage();
                        usedItem = arrow.getBowItem();
                    } else {
                        // Half damage compared to fully charged arrow
                        damage = 3;
                        usedItem = null;
                    }
                    if(projectileDamage.getShooter() != null) {
                        BrawlPlayer attacker = parentGame.getBrawlPlayer(projectileDamage.getShooter());
                        // It's fine if we have a null attacker, just be wary of it when calculating damage
                        BrawlDamage brawlDamage = new BrawlDamage(attacker, target, usedItem, damage);
                        brawlDamage.fire();
                    } else {
                        BrawlDamage brawlDamage = new BrawlDamage(null, target, usedItem, damage);
                        brawlDamage.fire();
                    }
                }
                event.setDamage(0);
            }
        });
        node.addListener(PlayerMoveEvent.class, event -> {
           if(parentGame.canPlayerMove(event.getPlayer())) {
               event.setCancelled(true);
           }
        });
        eventHandler.addChild(node);
    }

    public void unregisterEvents() {
        eventHandler.removeChildren(eventNodeName);
    }
}
