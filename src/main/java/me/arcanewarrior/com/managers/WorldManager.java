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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldManager implements Manager {

    public static WorldManager getManager() { return GameCore.getGameCore().getManager(WorldManager.class); }

    private static final List<InstanceContainer> worldList = new ArrayList<>();

    private static final Pos DEFAULT_WORLD_SPAWN_POS = new Pos(0, 42, 0);

    public InstanceContainer getWorld(int index) {
        return worldList.get(index);
    }

    public void loadMinecraftWorld(String path) {
        AnvilLoader loader = new AnvilLoader(path);
        InstanceContainer newWorld = MinecraftServer.getInstanceManager().createInstanceContainer(loader);
        newWorld.setChunkGenerator(new VoidChunkGenerator());
        worldList.add(newWorld);
    }

    public void unloadMinecraftWorld(int index, boolean save) {
        if(index == 0) {
            throw new IllegalArgumentException("Cannot unload world at index 0, that's the default world!");
        }
        InstanceContainer world = worldList.remove(index);
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
            MinecraftServer.LOGGER.warn("Instance at worldList index 0 before creating the default world?!?!");
        }
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setChunkGenerator(new BasicChunkGenerator());
        worldList.add(0, instanceContainer);
    }

    public InstanceContainer getDefaultWorld() {
        return worldList.get(0);
    }

    @Override
    public void initialize() {
        createDefaultWorld();
    }

    @Override
    public void stop() {
        for(int i = 1; i < worldList.size(); i++) {
            unloadMinecraftWorld(1, false);
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
        // TODO: Attach a name to the world somehow, extend InstanceContainer and add a name parameter, or convert worldList into a Map<String, InstanceContainer> ?
        for(int i = 1; i < worldList.size(); i++) {
            builder.append(", ");
            builder.append(worldList.get(i).getUniqueId());
        }
        return builder.toString();
    }
}
