package com.isles.block;

import com.isles.blest;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;

public class InfectionGrassBlock extends SpreadingSnowyDirtBlock {
    public InfectionGrassBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        if (!level.isAreaLoaded(pos, 3)) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            BlockPos targetPos = pos.offset(
                    random.nextInt(3) - 1,
                    random.nextInt(5) - 3,
                    random.nextInt(3) - 1
            );
            if (!level.getBlockState(targetPos).is(blest.infection_grass.get())) {
                continue;
            }
            if (!canSurvive(state, level, targetPos)) {
                continue;
            }

            BlockState newState = blest.infection_grass.get().defaultBlockState();
            if (newState.hasProperty(SNOWY)) {
                newState = newState.setValue(SNOWY, level.getBlockState(targetPos.above()).is(Blocks.SNOW));
            }
            level.setBlockAndUpdate(targetPos, newState);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return super.canSurvive(state, level, pos);
    }
}
