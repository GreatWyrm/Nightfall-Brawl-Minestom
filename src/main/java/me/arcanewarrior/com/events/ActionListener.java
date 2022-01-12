package me.arcanewarrior.com.events;

import me.arcanewarrior.com.action.ActionPlayer;
import me.arcanewarrior.com.managers.ActionPlayerManager;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityShootEvent;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.event.player.PlayerItemAnimationEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.trait.EntityEvent;

public class ActionListener {

    private final ActionPlayerManager parentManager;
    private final GlobalEventHandler handler;

    public ActionListener(ActionPlayerManager parentManager, GlobalEventHandler handler) {
       this.parentManager = parentManager;
       this.handler = handler;
    }

    private final String eventNodeName = "action-input-events";

    public void registerEvents() {
        EventNode<EntityEvent> node = EventNode.type(eventNodeName, EventFilter.ENTITY);
        node.addListener(PlayerUseItemEvent.class, event -> {
            if(parentManager.isActionPlayer(event.getPlayer())) {
                parentManager.getActionPlayer(event.getPlayer()).OnPlayerInput(ActionPlayer.InputType.RIGHT);
            }
        });
        node.addListener(PlayerHandAnimationEvent.class, event -> {
            if(parentManager.isActionPlayer(event.getPlayer())) {
                parentManager.getActionPlayer(event.getPlayer()).OnPlayerInput(ActionPlayer.InputType.LEFT);
            }
        });
        // Bow
        node.addListener(PlayerItemAnimationEvent.class, event -> {
            if(parentManager.isActionPlayer(event.getPlayer())) {
                if(event.getItemAnimationType() == PlayerItemAnimationEvent.ItemAnimationType.BOW || event.getItemAnimationType() == PlayerItemAnimationEvent.ItemAnimationType.CROSSBOW) {
                    parentManager.getActionPlayer(event.getPlayer()).OnStartDrawing(System.currentTimeMillis());
                }
            }
        });
        node.addListener(EntityShootEvent.class, event -> {
            if(parentManager.isActionPlayer(event.getEntity().getUuid())) {
                boolean shouldFire = parentManager.getActionPlayer(event.getEntity().getUuid()).OnBowFire(event.getProjectile(), event.getPower());
                if(!shouldFire) {
                    event.setCancelled(true);
                }
            }
        });
        handler.addChild(node);
    }

    public void unregisterEvents() {
        handler.removeChildren(eventNodeName);
    }
}
