package me.arcanewarrior.com.damage;

import me.arcanewarrior.com.damage.bow.BowMechanics;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityShootEvent;
import net.minestom.server.event.item.ItemUpdateStateEvent;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerItemAnimationEvent;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.item.Material;

public class DamageEvents {

    public void registerDamageEvents() {
        DamageProcessor processor = new DamageProcessor();
        BowMechanics bowMechanics = new BowMechanics();
        EventNode<EntityEvent> node = EventNode.type("damage-events", EventFilter.ENTITY);
        node.addListener(EventListener.builder(EntityAttackEvent.class)
                .handler(processor::processEntityAttackEvent)
                .build());
        node.addListener(EventListener.builder(EntityShootEvent.class)
                .handler(processor::handleEntityShoot)
                .build());
        node.addListener(EventListener.builder(PlayerChangeHeldSlotEvent.class)
                .handler(processor::handleSlotSwap)
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
