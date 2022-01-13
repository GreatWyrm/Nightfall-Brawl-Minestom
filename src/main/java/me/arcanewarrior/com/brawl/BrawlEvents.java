package me.arcanewarrior.com.brawl;

import me.arcanewarrior.com.action.ActionPlayer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.entity.EntityShootEvent;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.event.player.PlayerItemAnimationEvent;
import net.minestom.server.event.player.PlayerStopFlyingEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.trait.EntityEvent;

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
                player.OnPlayerInput(ActionPlayer.InputType.RIGHT);
            }
        });
        node.addListener(PlayerHandAnimationEvent.class, event -> {
            BrawlPlayer player = parentGame.getBrawlPlayer(event.getPlayer());
            if(player != null) {
                player.OnPlayerInput(ActionPlayer.InputType.LEFT);
            }
        });
        // Bow
        node.addListener(PlayerItemAnimationEvent.class, event -> {
            BrawlPlayer player = parentGame.getBrawlPlayer(event.getPlayer());
            if(player != null) {
                if(event.getItemAnimationType() == PlayerItemAnimationEvent.ItemAnimationType.BOW || event.getItemAnimationType() == PlayerItemAnimationEvent.ItemAnimationType.CROSSBOW) {
                    player.OnStartDrawing(System.currentTimeMillis());
                }
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
        node.addListener(EntityAttackEvent.class, event -> {
            BrawlPlayer attacker = parentGame.getBrawlPlayer(event.getEntity());
            BrawlPlayer target = parentGame.getBrawlPlayer(event.getTarget());
            if(attacker != null && target != null) {
                BrawlDamage damage = new BrawlDamage(attacker, target);
                attacker.onDamageAttack(damage);
                target.onDamageRecieve(damage);
                damage.fire();
            }
        });
        //node.addListener(PlayerStopFlyingEvent.class, event -> event.getPlayer().setFlying(true));
        eventHandler.addChild(node);
    }

    public void unregisterEvents() {
        eventHandler.removeChildren(eventNodeName);
    }
}
