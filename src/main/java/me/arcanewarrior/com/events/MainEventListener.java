package me.arcanewarrior.com.events;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.damage.DamageEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.trait.EntityEvent;

public class MainEventListener {

    private final GameCore gameCore;

    public MainEventListener(GameCore core) {
        gameCore = core;
    }

    public void registerAllEvents() {
        DamageEvents damageEvents = new DamageEvents();
        damageEvents.registerDamageEvents();

        EventNode<EntityEvent> basicEventsNode = EventNode.type("basic-events", EventFilter.ENTITY);
        basicEventsNode.addListener(PlayerLoginEvent.class, event -> gameCore.broadcastMessage(Component.text(event.getPlayer().getUsername() + " has joined the server", NamedTextColor.YELLOW)));
        basicEventsNode.addListener(PlayerDisconnectEvent.class, event -> gameCore.broadcastMessage(Component.text(event.getPlayer().getUsername() + " has left the server", NamedTextColor.YELLOW)));
        MinecraftServer.getGlobalEventHandler().addChild(basicEventsNode);
    }

}
