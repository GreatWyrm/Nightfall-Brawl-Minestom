package me.arcanewarrior.com.managers;

import me.arcanewarrior.com.VoidChunkGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;
import java.util.List;

public class WorldManager {

    public static final List<InstanceContainer> worldList = new ArrayList<>();

    public InstanceContainer getWorld(int index) {
        return worldList.get(index);
    }

    public void loadMinecraftWorld(String path) {
        AnvilLoader loader = new AnvilLoader(path);
        InstanceContainer newWorld = MinecraftServer.getInstanceManager().createInstanceContainer(loader);
        newWorld.setChunkGenerator(new VoidChunkGenerator());
        worldList.add(newWorld);
    }

    public void unloadWorld(int index, boolean save) {
        if(index == 0) {
            throw new IllegalArgumentException("Cannot unload world at index 0, that's the default world!");
        }
        InstanceContainer world = worldList.remove(index);
        // Teleport any players to default world
        for(Player player : world.getPlayers()) {
            player.setInstance(worldList.get(0));
        }
        if(save) {
            // Runs async in the background
            world.saveInstance();
        }
    }

}
