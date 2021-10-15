package me.arcanewarrior.com;

import me.arcanewarrior.com.commands.CommandStarter;
import me.arcanewarrior.com.managers.WorldManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.*;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class StartServer {
    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();

        MinecraftServer.LOGGER.info("Server Base initialized, loading core components...");

        CommandStarter.registerAllCommands(MinecraftServer.getCommandManager());

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setChunkGenerator(new BasicChunkGenerator());

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerLoginEvent.class, playerLoginEvent -> {
            playerLoginEvent.setSpawningInstance(instanceContainer);
            playerLoginEvent.getPlayer().setRespawnPoint(new Pos(0, 42, 0));
        });
        server.start("0.0.0.0", 25565);
        WorldManager manager = new WorldManager();
        manager.loadMinecraftWorld("mt-velvetine");
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
}
