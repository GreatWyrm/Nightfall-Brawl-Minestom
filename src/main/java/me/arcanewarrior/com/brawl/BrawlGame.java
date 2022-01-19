package me.arcanewarrior.com.brawl;

import me.arcanewarrior.com.action.items.ActionItemType;
import me.arcanewarrior.com.particles.ParticleGenerator;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BrawlGame {


    private final Task updateBrawlPlayers;
    private final BrawlEvents events;
    private final Instance brawlWorld;
    private final Pos centerPos = new Pos(242, 39, 1348);

    private int tickCounter = 0;

    private final double BORDER_RADIUS = 40;
    // Calculated from = Half of the border radius, subtract 0.3, since border will stop you 0.3 blocks earlier if it's on an exact block boundry, and then 0.02 earlier for an epsilon value
    private final double BLAST_LINE_BOUNDRY = BORDER_RADIUS/2 - 0.3 - 0.02;

    // TODO: Add upper and lower blast lines, use https://wiki.vg/Plugin_channels#minecraft:debug.2Fgame_test_add_marker as markers

    public BrawlGame(Instance worldInstance) {
        updateBrawlPlayers = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            for(BrawlPlayer player : brawlPlayerList.values()) {
                player.update();
                // Check if colliding with world border (blast lines)
                if(isInBlastLines(player)) {
                    playerKnockout(player);
                }
            }
            tickCounter++;
        }, TaskSchedule.seconds(8), TaskSchedule.tick(1));
        // Above, start immediately, run once per tick
        events = new BrawlEvents(this, MinecraftServer.getGlobalEventHandler());
        events.registerEvents();
        brawlWorld = worldInstance;
        setupWorldBorder();
    }

    private final Map<UUID, BrawlPlayer> brawlPlayerList = new HashMap<>();

    public Set<String> getSetOfNames() {
        return brawlPlayerList.values().stream().map(player -> player.getPlayer().getUsername()).collect(Collectors.toSet());
    }

    public BrawlPlayer getBrawlPlayer(UUID uuid) {
        return brawlPlayerList.get(uuid);
    }

    public BrawlPlayer getBrawlPlayer(Entity entity) {
        return getBrawlPlayer(entity.getUuid());
    }

    public void addBrawlPlayer(Player player) {
        UUID id = player.getUuid();
        if(brawlPlayerList.containsKey(id)) {
            throw new IllegalStateException("Cannot add player " + player.getName() + " to the Action Player List, as they are already in it!");
        }
        brawlPlayerList.put(id, new BrawlPlayer(player, this));
    }

    public boolean isBrawlPlayer(Player player) {
        return brawlPlayerList.containsKey(player.getUuid());
    }

    public void giveActionItem(UUID id, ActionItemType type) {
        if(brawlPlayerList.containsKey(id)) {
            brawlPlayerList.get(id).giveActionItemType(type);
        }
    }

    public void removeActionItem(UUID id, ActionItemType type) {
        if(brawlPlayerList.containsKey(id)) {
            brawlPlayerList.get(id).removeActionItemType(type);
        }
    }


    public void removeBrawlPlayer(Player player) {
        brawlPlayerList.remove(player.getUuid());
    }

    public int getTickCount() {
        return tickCounter;
    }

    private void setupWorldBorder() {
        WorldBorder border = brawlWorld.getWorldBorder();
        border.setCenter((float) centerPos.x(), (float) centerPos.z());
        border.setDiameter(BORDER_RADIUS);
    }

    public void warpAllToCenter() {
        for(BrawlPlayer player : brawlPlayerList.values()) {
            player.getPlayer().setInstance(brawlWorld, centerPos);
        }
    }

    // ----- KNOCKOUTS -----

    public boolean isInBlastLines(BrawlPlayer player) {
        Pos position = player.getPlayer().getPosition();
        return position.x() <= -BLAST_LINE_BOUNDRY + centerPos.x() || position.x() >= BLAST_LINE_BOUNDRY + centerPos.x() ||
                position.z() <= -BLAST_LINE_BOUNDRY + centerPos.z() || position.z() >= BLAST_LINE_BOUNDRY + centerPos.z();
    }

    /**
     * Call when a Brawl Player should be knocked out and reset
     * @param player The player to count as "knocked out"
     */
    public void playerKnockout(BrawlPlayer player) {
        broadcastToPlayers(player.getKnockoutMessage());
        blastLineParticles(player);
        resetBrawlPlayer(player);
    }

    public void blastLineParticles(BrawlPlayer player) {
        Vec velocity = player.getPlayer().getVelocity();
        Pos currentPos = player.getPlayer().getPosition().add(player.getPlayer().getEyeHeight());
        // Create particles in opposite direction of velocity
        velocity = velocity.normalize().mul(-1);
        for(int i = 0; i < 6; i++) {
            ParticleGenerator.spawnParticlesForAll(player.getPlayer(), Particle.CLOUD, currentPos, false, 0.4f, 0.4f, 0.4f, 0, 10, null);
            ParticleGenerator.spawnParticlesForAll(player.getPlayer(), Particle.END_ROD, currentPos, false, 0.4f, 0.4f, 0.4f, 0, 10, null);
            ParticleGenerator.spawnParticlesForAll(player.getPlayer(), Particle.DUST_COLOR_TRANSITION, currentPos, false, 0.4f, 0.4f, 0.4f, 0, 10,
                ParticleGenerator.createDustTransitionData(1f, 1f, 1f, 3f, 0.3f, 0.8f, 0.4f));
            currentPos = currentPos.add(velocity);
        }
    }

    public void resetBrawlPlayer(BrawlPlayer player) {
        player.getPlayer().teleport(centerPos);
        player.resetDamage();
    }

    public void broadcastToPlayers(Component message) {
        for(BrawlPlayer player : brawlPlayerList.values()) {
            player.sendMessage(message);
        }
    }

    public void broadcastToPlayers(String message) {
        for(BrawlPlayer player : brawlPlayerList.values()) {
            player.sendMessage(message);
        }
    }


    /**
     * Stops the current brawl game, cleaning up any variables that may have been initialized
     */
    public void stop() {
        updateBrawlPlayers.cancel();
        events.unregisterEvents();
    }
}
