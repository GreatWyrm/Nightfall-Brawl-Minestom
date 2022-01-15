package me.arcanewarrior.com.events;

import me.arcanewarrior.com.damage.DamageProcessor;
import me.arcanewarrior.com.damage.bow.BowMechanics;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.entity.EntityShootEvent;
import net.minestom.server.event.item.ItemUpdateStateEvent;
import net.minestom.server.event.player.PlayerItemAnimationEvent;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.item.Material;

public class MainEventListener {

    public void registerAllEvents() {
        DamageProcessor processer = new DamageProcessor();
        BowMechanics bowMechanics = new BowMechanics();
        EventNode<EntityEvent> node = EventNode.type("damage-events", EventFilter.ENTITY);
        node.addListener(EventListener.builder(EntityAttackEvent.class)
                .handler(processer::processEntityAttackEvent)
                .build());
        node.addListener(EventListener.builder(EntityShootEvent.class)
                .handler(processer::handleEntityShoot)
                .build());
        EventNode<EntityEvent> bowNode = EventNode.type("bow-events", EventFilter.ENTITY);
        node.addChild(bowNode);
        bowNode.addListener(EventListener.builder(ItemUpdateStateEvent.class)
                .handler(bowMechanics::handleBowUpdateState)
                .filter(event -> event.getItemStack().getMaterial() == Material.BOW || event.getItemStack().getMaterial() == Material.CROSSBOW)
                .build());
        bowNode.addListener(EventListener.builder(PlayerItemAnimationEvent.class)
                .handler(bowMechanics::bowStartDrawing)
                .filter(event -> event.getItemAnimationType() == PlayerItemAnimationEvent.ItemAnimationType.BOW || event.getItemAnimationType() == PlayerItemAnimationEvent.ItemAnimationType.CROSSBOW)
                .build());
        MinecraftServer.getGlobalEventHandler().addChild(node);
    }

}
