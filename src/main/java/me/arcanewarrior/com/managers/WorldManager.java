package me.arcanewarrior.com.managers;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.VoidChunkGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.*;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public Set<String> getLoadedWorldNames() { return worldList.keySet(); }

    public void loadMinecraftWorld(String path) {
        if(worldList.containsKey(path)) {
            throw new IllegalArgumentException("World already exists with name: " + path + "!");
        }
        AnvilLoader loader = new AnvilLoader(path);
        InstanceContainer newWorld = MinecraftServer.getInstanceManager().createInstanceContainer(loader);
        // Required for loading in the level.dat file information
        loader.loadInstance(newWorld);
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

    public Pos getWorldSpawnPoint(String worldName) {
        if(!doesWorldExist(worldName)) return null;

        InstanceContainer world = worldList.get(worldName);
        NBTCompound worldLevelDat = world.getTag(Tag.NBT);
        NBTCompound data = worldLevelDat.getCompound("Data");
        int x = data.getInt("SpawnX");
        int y = data.getInt("SpawnY");
        int z = data.getInt("SpawnZ");
        return new Pos(x, y, z);
    }

    private void createDefaultWorld() {
        if(!worldList.isEmpty()) {
            MinecraftServer.LOGGER.warn("WorldList not empty when creating the default world?!?!");
        }
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setChunkGenerator(new BasicChunkGenerator());
        // Set NBT Tag for proper teleporting
        MutableNBTCompound data = new MutableNBTCompound();
        data.setInt("SpawnX", 0);
        data.setInt("SpawnY", 42);
        data.setInt("SpawnZ", 0);
        NBTCompound parent = new NBTCompound();
        parent.plus(data);
        instanceContainer.setTag(Tag.NBT,parent);
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
                        if((x + z) % 7 == 0) {
                            batch.setBlock(x, y, z, Block.HAY_BLOCK);
                        } else if(x % 8 == 0) {
                            batch.setBlock(x, y, z, Block.AMETHYST_BLOCK);
                        } else if(x % 4 == 0) {
                            batch.setBlock(x, y, z, Block.BONE_BLOCK);
                        } else {
                            batch.setBlock(x, y, z, Block.STONE);
                        }
                    }
                }
            }
        }

        @Override
        public @Nullable List<ChunkPopulator> getPopulators() {
            return null;
        }
    }

    public String getFormattedWorldNameString() {
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
