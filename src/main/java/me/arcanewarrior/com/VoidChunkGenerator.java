package me.arcanewarrior.com;

import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.biomes.Biome;
import net.minestom.server.world.biomes.BiomeEffects;
import org.jetbrains.annotations.NotNull;

public class VoidChunkGenerator implements Generator {

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
    public void generate(@NotNull GenerationUnit unit) {
        unit.modifier().fill(Block.AIR);
        unit.modifier().fillBiome(VOID);
    }
}
