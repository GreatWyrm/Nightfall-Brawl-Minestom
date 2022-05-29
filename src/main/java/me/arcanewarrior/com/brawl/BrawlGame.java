package me.arcanewarrior.com.brawl;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.action.items.ActionItemType;
import me.arcanewarrior.com.managers.BrawlPlayerDataManager;
import me.arcanewarrior.com.managers.ItemManager;
import me.arcanewarrior.com.managers.WorldManager;
import me.arcanewarrior.com.particles.ParticleGenerator;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.particle.Particle;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BrawlGame {

    // ---- World/Position Variables
    private final Instance brawlWorld;
    private final Pos centerPos = new Pos(242, 39, 1348);
    private final double BORDER_RADIUS = 40;
    // Calculated from = Half of the border radius, subtract 0.3, since border will stop you 0.3 blocks earlier if it's on an exact block boundry, and then 0.02 earlier for an epsilon value
    private final double BLAST_LINE_BOUNDRY = BORDER_RADIUS/2 - 0.3 - 0.02;

    // Events/Ticking
    private Task updateBrawlPlayers;
    private Task countdown;
    private final BrawlEvents events;
    private int tickCounter = 0;


    // TODO: Add upper and lower blast lines, use https://wiki.vg/Plugin_channels#minecraft:debug.2Fgame_test_add_marker as markers
    private final Sidebar brawlSidebar = new Sidebar(Component.text("Nightfall Brawl", NamedTextColor.AQUA));
    private BrawlGameSettings gameSettings = new BrawlGameSettings(3, 180);
    private GameState gameState = GameState.LOBBY;
    // Player list
    private final Map<UUID, BrawlPlayer> brawlPlayerList = new HashMap<>();
    private final Set<UUID> readyPlayerList = new HashSet<>();

    private final Logger logger = LoggerFactory.getLogger(BrawlGame.class);

    // Initialization
    public BrawlGame(Instance worldInstance) {
        events = new BrawlEvents(this, MinecraftServer.getGlobalEventHandler());
        events.registerEvents();
        brawlWorld = worldInstance;
        setupWorldBorder();
        brawlSidebar.createLine(new Sidebar.ScoreboardLine("state", Component.text(gameState.name()), 1));
    }

    public Set<String> getListOfNames() {
        return brawlPlayerList.values().stream().map(player -> player.getPlayer().getUsername()).collect(Collectors.toSet());
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

    // ---- GAME STATE CHANGES ----

    private void startGame() {
        countdown.cancel();
        warpAllToCenter();
        // Transition any players in countdown to their loadout
        for(BrawlPlayer player : brawlPlayerList.values()) {
            player.reset();
            // Apply loadout
            Loadout loadout = BrawlPlayerDataManager.getManager().getPlayerData(player.getPlayer()).getCurrentLoadout();
            loadout.applyToPlayer(player);
            // Hardcoded arrows
            ItemManager.getManager().giveItemToPlayer("galvan", 64, player.getPlayer());
        }
        updateBrawlPlayers = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            for(BrawlPlayer player : brawlPlayerList.values()) {
                player.update();
                // Check if colliding with world border (blast lines)
                if(isInBlastLines(player)) {
                    playerKnockout(player);
                }
            }
            tickCounter++;
        }, TaskSchedule.immediate(), TaskSchedule.tick(1));
    }

    private void startCountdown() {
        for(BrawlPlayer player : brawlPlayerList.values()) {
           player.getPlayer().playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_CHIME, Sound.Source.MASTER, 1f, 1f));
        }
        AtomicInteger countdownTime = new AtomicInteger(15);
        countdown = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            int time = countdownTime.getAndDecrement();
            if(time <= 0) {
                transitionToState(GameState.GAME);
                return;
            }
            for(BrawlPlayer player : brawlPlayerList.values()) {
                player.getPlayer().showTitle(
                        Title.title(Component.empty(),
                                Component.text(time, NamedTextColor.AQUA),
                                Title.Times.times(Ticks.duration(5), Ticks.duration(12), Ticks.duration(3))
                        )
                );
            }
        }, TaskSchedule.nextTick(), TaskSchedule.seconds(1));
    }

    // ----- BRAWL PLAYER ADD/REMOVE/ACCESS -----

    public boolean isBrawlPlayer(Player player) {
        return brawlPlayerList.containsKey(player.getUuid());
    }
    public boolean isBrawlPlayer(UUID id) {
        return brawlPlayerList.containsKey(id);
    }
    public @Nullable BrawlPlayer getBrawlPlayer(UUID uuid) {
        return brawlPlayerList.get(uuid);
    }

    public @Nullable BrawlPlayer getBrawlPlayer(Entity entity) {
        return getBrawlPlayer(entity.getUuid());
    }
    public void addBrawlPlayer(Player player) {
        UUID id = player.getUuid();
        if(brawlPlayerList.containsKey(id)) {
            logger.warn("Attempted to add player " + player.getName() + " to the Brawl Player List List, but they are already in it!");
            return;
        }
        brawlPlayerList.put(id, new BrawlPlayer(player, this));
        brawlSidebar.addViewer(player);
        switch (gameState) {
            case LOBBY, COUNTDOWN -> {
                // Default lobby loadout
                brawlPlayerList.get(id).giveActionItemType(ActionItemType.OPEN_KIT_MENU);
                brawlPlayerList.get(id).giveActionItemType(ActionItemType.READY_ITEM);
            }
            case GAME -> {
                // Apply loadout
                Loadout loadout = BrawlPlayerDataManager.getManager().getPlayerData(player).getCurrentLoadout();
                loadout.applyToPlayer(brawlPlayerList.get(id));
                // Hardcoded arrows
                ItemManager.getManager().giveItemToPlayer("galvan", 64, player);
            }
        }
    }
    public void removeBrawlPlayer(Player player) {
        brawlPlayerList.remove(player.getUuid());
        brawlSidebar.removeViewer(player);
    }

    // ----- KNOCKOUTS -----

    public boolean isInBlastLines(BrawlPlayer player) {
        if(player.getInstance() != brawlWorld) {
            return false;
        }
        Pos position = player.getPosition();
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
        broadcastToPlayers(Component.text("Testing"));
        player.getPlayer().teleport(centerPos);
        player.resetDamage();
    }

    public void broadcastToPlayers(Component message) {
        brawlSidebar.sendMessage(message);
    }

    // GAME STATE

    public void transitionToState(GameState state) {
        var requiredState = state.previousState();

        if(gameState == requiredState) {
            gameState = state;
            brawlSidebar.updateLineContent("state", Component.text(gameState.name()));
            switch (gameState) {
                case COUNTDOWN -> startCountdown();
                case GAME -> startGame();
                case END -> MinecraftServer.getSchedulerManager().scheduleTask(() -> {
                    broadcastToPlayers(Component.text("The Game has ended!", NamedTextColor.RED));
                    stop();
                    GameCore.getGameCore().createNewBrawlGame();
                }, TaskSchedule.seconds(30), TaskSchedule.stop());
            }
        } else {
            logger.warn("Attempted to change to state " + state.name() + ", but current state is " + gameState.name() + "!");
        }
    }

    public void toggleReadyState(BrawlPlayer player) {
        if(gameState != GameState.LOBBY) return;

        if(brawlPlayerList.containsValue(player)) {
            if(readyPlayerList.contains(player.getPlayer().getUuid())) {
                readyPlayerList.remove(player.getPlayer().getUuid());
            } else {
                readyPlayerList.add(player.getPlayer().getUuid());
                // Check if we should start the countdown
                if(readyPlayerList.size() == brawlPlayerList.size()) {
                    transitionToState(GameState.COUNTDOWN);
                }
            }
        }
    }

    /**
     * Stops the current brawl game, cleaning up any variables that may have been initialized
     */
    public void stop() {
        for(BrawlPlayer player : brawlPlayerList.values()) {
            player.reset();
            brawlSidebar.removeViewer(player.getPlayer());
            player.getPlayer().setInstance(WorldManager.getManager().getDefaultWorld(), WorldManager.getManager().getDefaultWorldSpawnPos());
        }
        brawlPlayerList.clear();
        if(updateBrawlPlayers != null && updateBrawlPlayers.isAlive()) {
            updateBrawlPlayers.cancel();
        }
        if(countdown != null && countdown.isAlive()) {
            countdown.cancel();
        }
        events.unregisterEvents();
    }
}
