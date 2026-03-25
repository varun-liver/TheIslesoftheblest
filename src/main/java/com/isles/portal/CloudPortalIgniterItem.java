package com.isles.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class CloudPortalIgniterItem extends Item {
    public CloudPortalIgniterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos placePos = context.getClickedPos().relative(context.getClickedFace());
        if (CloudPortalShape.trySpawnPortal(level, placePos)) {
            level.playSound(null, placePos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            context.getItemInHand().hurtAndBreak(1, context.getPlayer(),
                    player -> player.broadcastBreakEvent(context.getHand()));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
