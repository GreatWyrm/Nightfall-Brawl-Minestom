package me.arcanewarrior.com.managers;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.VoidChunkGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.*;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldManager implements Manager {

    public static WorldManager getManager() { return GameCore.getGameCore().getManager(WorldManager.class); }

    private static final Map<String, InstanceContainer> worldList = new HashMap<>();

    private static final Pos DEFAULT_WORLD_SPAWN_POS = new Pos(0, 42, 0);
    private static final String DEFAULT_WORLD_NAME = "default";

    public InstanceContainer getWorld(String worldName) {
        return worldList.get(worldName);
    }

    public boolean doesWorldExist(String worldName) {
        return worldList.containsKey(worldName);
    }

    public void loadMinecraftWorld(String path) {
        if(worldList.containsKey(path)) {
            throw new IllegalArgumentException("World already exists with name: " + path + "!");
        }
        AnvilLoader loader = new AnvilLoader(path);
        InstanceContainer newWorld = MinecraftServer.getInstanceManager().createInstanceContainer(loader);
        newWorld.setChunkGenerator(new VoidChunkGenerator());
        worldList.put(path, newWorld);
    }

    public void unloadMinecraftWorld(String worldName, boolean save) {
        if(worldName.equals(DEFAULT_WORLD_NAME)) {
            throw new IllegalArgumentException("Cannot unload default world!");
        }
        InstanceContainer world = worldList.remove(worldName);
        // Teleport any players to default world
        for(Player player : world.getPlayers()) {
            player.setInstance(getDefaultWorld(), DEFAULT_WORLD_SPAWN_POS);
        }
        if(save) {
            // Runs async in the background
            world.saveInstance();
        }
        MinecraftServer.getInstanceManager().unregisterInstance(world);
    }

    private void createDefaultWorld() {
        if(!worldList.isEmpty()) {
            MinecraftServer.LOGGER.warn("WorldList not empty when creating the default world?!?!");
        }
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setChunkGenerator(new BasicChunkGenerator());
        worldList.put(DEFAULT_WORLD_NAME, instanceContainer);
    }

    public InstanceContainer getDefaultWorld() {
        return worldList.get(DEFAULT_WORLD_NAME);
    }

    @Override
    public void initialize() {
        createDefaultWorld();
    }

    @Override
    public void stop() {
        for(String worldName : worldList.keySet()) {
            // Skip unloading default world, we do that later
            if(!worldName.equals(DEFAULT_WORLD_NAME)) {
                unloadMinecraftWorld(worldName, false);
            }
        }
        getDefaultWorld().saveChunksToStorage();
    }

    private static class BasicChunkGenerator implements ChunkGenerator {

        @Override
        public void generateChunkData(@NotNull ChunkBatch batch, int chunkX, int chunkZ) {
            for(byte x = 0; x < Chunk.CHUNK_SIZE_X; x++) {
                for(byte y = 0; y < 40; y++) {
                    for(byte z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                        batch.setBlock(x, y, z, Block.STONE);
                    }
                }
            }
        }

        @Override
        public void fillBiomes(@NotNull Biome[] biomes, int chunkX, int chunkZ) {
            Arrays.fill(biomes, Biome.PLAINS);
        }

        @Override
        public @Nullable List<ChunkPopulator> getPopulators() {
            return null;
        }
    }

    public String getWorldListNames() {
        StringBuilder builder = new StringBuilder();
        if(worldList.isEmpty()) {
            return "No Worlds Loaded";
        }
        builder.append("Default World");
        for(String worldName : worldList.keySet()) {
            if(!worldName.equals(DEFAULT_WORLD_NAME)) {
                builder.append(", ");
                builder.append(worldName);
            }
        }
        return builder.toString();
    }
}
