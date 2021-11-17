package me.arcanewarrior.com.events;

import me.arcanewarrior.com.damage.DamageProcessor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.trait.EntityEvent;

public class MainEventListener {

    public void registerAllEvents() {
        DamageProcessor processer = new DamageProcessor();
        EventNode<EntityEvent> node = EventNode.type("damage-events", EventFilter.ENTITY);
        node.addListener(EventListener.builder(EntityDamageEvent.class)
                .handler(processer::handleEntityDamage)
                .build());
        node.addListener(EventListener.builder(EntityAttackEvent.class)
                .handler(processer::processEntityAttackEvent)
                .build());
        MinecraftServer.getGlobalEventHandler().addChild(node);
    }

}
