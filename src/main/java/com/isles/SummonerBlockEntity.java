package com.isles;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SummonerBlockEntity extends BlockEntity {
    private boolean summoned = false;
    private int summontype;
    public SummonerBlockEntity(BlockPos pos, BlockState state) {
        super(blest.SUMMONER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        summoned = tag.getBoolean("Summoned");
        summontype = tag.getInt("SummonType");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("Summoned", summoned);
        tag.putInt("SummonType", summontype);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SummonerBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }
        Player player = level.getNearestPlayer(
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                5.0,
                false
        );
        if (player != null) {
            if(!blockEntity.summoned) {
                for (int i = 0; i < 10; i++) {
                    Entity entity = null;
                    switch (blockEntity.summontype) {
                        case 0:
                            entity = blest.the_infection.get().create(level);
                            break;
                        case 1:
                            entity = blest.the_guardian.get().create(level);
                            break;
                        case 2:
                            entity = blest.the_cursed_ones.get().create(level);
                        default:
                            break;
                    }
                    if (entity != null) {
                        entity.moveTo(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
                        level.addFreshEntity(entity);
                    }
                }
                blockEntity.summoned = true;
                blockEntity.markDirty();
            }
        }
    }

    private void markDirty() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
