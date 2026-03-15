package com.isles.worldgen;

import com.isles.blest;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public final class BlestSurfaceRules {
    private BlestSurfaceRules() {
    }

    public static SurfaceRules.RuleSource makeRules() {
        SurfaceRules.RuleSource skyGrass = SurfaceRules.state(blest.sky_grass.get().defaultBlockState());
        SurfaceRules.RuleSource dirt = SurfaceRules.state(Blocks.DIRT.defaultBlockState());

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(ModBiomes.SKY_FOREST),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, skyGrass),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, dirt)
                        )
                )
        );
    }
}
