package com.isles.worldgen;

import com.isles.blest;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public final class ModBiomes {
    public static final ResourceKey<Biome> SKY_FOREST =
            ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(blest.MODID, "sky_forest"));

    private ModBiomes() {
    }
}
