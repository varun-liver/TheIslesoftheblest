package com.isles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class InfectionBlockEntity extends BlockEntity {
    private BlockState savedState = Blocks.AIR.defaultBlockState();
    private int revertDelay = -1;
    private int spreadTimer = -1;

    public InfectionBlockEntity(BlockPos pos, BlockState state) {
        super(blest.INFECTION_BLOCK_ENTITY.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        if (Config.spreadInfection) {
            if (spreadTimer == -1) {
                // Initial spread timer: 8 minutes (9600 ticks) + random offset to stagger
                spreadTimer = 9600 + level.random.nextInt(400);
            }

            if (spreadTimer > 0) {
                spreadTimer--;
            } else {
                spread(level, pos, state);
                spreadTimer = 9600; // Reset to 8 minutes
            }
        } else {
            if (revertDelay == -1) {
                revertDelay = level.random.nextInt(181) + 20; // 20 to 200
            }

            if (revertDelay > 0) {
                revertDelay--;
            } else {
                revert(level, pos);
            }
        }
    }

    private void revert(Level level, BlockPos pos) {
        if (savedState != null && !savedState.isAir()) {
            level.setBlock(pos, savedState, 3);
        } else {
            level.destroyBlock(pos, false);
        }
    }

    private void spread(Level level, BlockPos pos, BlockState state) {
        if (state.is(blest.infection.get())) {
            Direction dir = Direction.getRandom(level.random);
            BlockPos targetPos = pos.relative(dir);
            BlockState targetState = level.getBlockState(targetPos);

            if (!targetState.isAir() && !targetState.is(blest.infection.get()) && !targetState.is(blest.infection_grass.get())) {
                infect(level, targetPos, targetState);
            }
        } else if (state.is(blest.infection_grass.get())) {
            for (int i = 0; i < 4; i++) {
                BlockPos targetPos = pos.offset(
                        level.random.nextInt(3) - 1,
                        level.random.nextInt(5) - 3,
                        level.random.nextInt(3) - 1
                );
                if (level.getBlockState(targetPos).is(Blocks.DIRT)) {
                    BlockState newState = blest.infection_grass.get().defaultBlockState();
                    level.setBlockAndUpdate(targetPos, newState);
                }
            }
        }
    }

    private void infect(Level level, BlockPos pos, BlockState oldState) {
        level.setBlock(pos, blest.infection.get().defaultBlockState(), 3);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof InfectionBlockEntity infectionBE) {
            infectionBE.setSavedState(oldState);
        }
    }

    public void setSavedState(BlockState state) {
        this.savedState = state;
        this.setChanged();
    }

    public BlockState getSavedState() {
        return savedState;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("SavedState", NbtUtils.writeBlockState(savedState));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("SavedState", 10)) {
            HolderGetter<net.minecraft.world.level.block.Block> holdergetter = this.level != null ? this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
            this.savedState = NbtUtils.readBlockState(holdergetter, tag.getCompound("SavedState"));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
