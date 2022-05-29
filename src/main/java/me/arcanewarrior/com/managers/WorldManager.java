package me.arcanewarrior.com.managers;

import me.arcanewarrior.com.GameCore;
import me.arcanewarrior.com.VoidChunkGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WorldManager implements Manager {

    public static WorldManager getManager() { return GameCore.getGameCore().getManager(WorldManager.class); }

    private final Map<String, InstanceContainer> worldList = new HashMap<>();

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
        newWorld.setGenerator(new VoidChunkGenerator());
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

    /**
     * Gets the world spawn point from the NBT tag associated with that world, or null
     * if that world does not exist
     * @param worldName The name of the world to get the spawn point of
     * @return A Pos object that represents the spawnpoint, or null if no such world exists
     */
    public @Nullable Pos getWorldSpawnPoint(String worldName) {
        if(!doesWorldExist(worldName)) return null;

        InstanceContainer world = worldList.get(worldName);
        NBTCompound worldLevelDat = (NBTCompound) world.getTag(Tag.NBT("Data"));
        int x = 0;
        int y = 0;
        int z = 0;
        if(worldLevelDat.containsKey("SpawnX")) {
            x = worldLevelDat.getInt("SpawnX");
        }
        if(worldLevelDat.containsKey("SpawnY")) {
            y = worldLevelDat.getInt("SpawnY");
        }
        if(worldLevelDat.containsKey("SpawnZ")) {
            z = worldLevelDat.getInt("SpawnZ");
        }
        return new Pos(x, y, z);
    }

    public @NotNull Pos getDefaultWorldSpawnPos() {
        return DEFAULT_WORLD_SPAWN_POS;
    }

    private void createDefaultWorld() {
        if(!worldList.isEmpty()) {
            MinecraftServer.LOGGER.warn("WorldList not empty when creating the default world?!?!");
        }
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setGenerator(new BasicChunkGenerator());
        // Set NBT Tag for proper teleporting
        MutableNBTCompound data = new MutableNBTCompound();
        data.setInt("SpawnX", 0);
        data.setInt("SpawnY", 42);
        data.setInt("SpawnZ", 0);
        NBTCompound parent = new NBTCompound();
        parent.plus(data);
        instanceContainer.setTag(Tag.NBT("Data"),parent);
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

    private static class BasicChunkGenerator implements Generator {
        @Override
        public void generate(@NotNull GenerationUnit unit) {
            Point size = unit.size();
            for (int x = 0; x < size.blockX(); x++) {
                for(int y = 0; y < size.blockY(); y++) {
                    for (int z = 0; z < size.blockZ(); z++) {
                        if((x + z) % 7 == 0) {
                            unit.modifier().setBlock(x, y, z, Block.HAY_BLOCK);
                        } else if(x % 8 == 0) {
                            unit.modifier().setBlock(x, y, z, Block.AMETHYST_BLOCK);
                        } else if(x % 4 == 0) {
                            unit.modifier().setBlock(x, y, z, Block.BONE_BLOCK);
                        } else {
                            unit.modifier().setBlock(x, y, z, Block.STONE);
                        }
                    }
                }
            }
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
