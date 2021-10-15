package me.arcanewarrior.com;

import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.biomes.Biome;
import net.minestom.server.world.biomes.BiomeEffects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class VoidChunkGenerator implements ChunkGenerator {

    private static final BiomeEffects VOID_EFFECTS = BiomeEffects.builder()
            .fogColor(0x2C2F33)
            .skyColor(0x78A7FF)
            .waterColor(0x3F76E4)
            .waterFogColor(0x50533)
            .build();

    private static final Biome VOID = Biome.builder()
            .category(Biome.Category.THE_END)
            .name((NamespaceID.from("minecraft:the_end")))
            .temperature(0.8F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(VOID_EFFECTS)
            .build();

    @Override
    public void generateChunkData(@NotNull ChunkBatch batch, int chunkX, int chunkZ) {
        batch.clear();
    }

    @Override
    public void fillBiomes(@NotNull Biome[] biomes, int chunkX, int chunkZ) {
        Arrays.fill(biomes, VOID);
    }

    @Override
    public @Nullable List<ChunkPopulator> getPopulators() {
        return null;
    }
}
