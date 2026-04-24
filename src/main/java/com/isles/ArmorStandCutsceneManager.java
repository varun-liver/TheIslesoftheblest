package com.isles;

import com.isles.client.AnimationLoader;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Rotations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import com.isles.network.CutsceneStartS2CPacket;
import com.isles.network.CutsceneEndS2CPacket;
import com.isles.network.ModNetwork;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages cinematic cutscenes using the actual player entity.
 */
@Mod.EventBusSubscriber(modid = blest.MODID)
public class ArmorStandCutsceneManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<AnimationInstance> ACTIVE_ANIMATIONS = new ArrayList<>();
    private static final Set<UUID> END_REQUESTS = ConcurrentHashMap.newKeySet();

    public static void requestEnd(ServerPlayer player) {
        if (player == null) return;
        END_REQUESTS.add(player.getUUID());
    }

    private static final String MODEL_ANIM_RESOURCE = "assets/" + blest.MODID + "/animations/modelanimation.json";
    private static final String MODEL_ANIM_NAME = "animation";
    private static volatile AnimationLoader.BedrockAnimation MODEL_ANIM;

    private static AnimationLoader.BedrockAnimation getModelAnimation() {
        try {
            AnimationLoader.BedrockAnimation anim = AnimationLoader.loadBedrockAnimation(MODEL_ANIM_RESOURCE, MODEL_ANIM_NAME);
            LOGGER.info("Loaded cutscene animation {}#{}", MODEL_ANIM_RESOURCE, MODEL_ANIM_NAME);
            return anim;
        } catch (RuntimeException e) {
            LOGGER.error("Failed to load cutscene animation {}#{}", MODEL_ANIM_RESOURCE, MODEL_ANIM_NAME, e);
            return null;
        }
    }

    public static void TheInfectionCutscene(ServerPlayer player) {
        if (player == null) return;

        // Start animation
        AnimationLoader.BedrockAnimation anim = getModelAnimation();
        float lengthSeconds = (anim == null || anim.lengthSeconds <= 0f) ? 4.0f : anim.lengthSeconds;
        int animTicks = Math.max(1, Math.round(lengthSeconds * 20.0f));

        String resolvedScrollText = CutsceneContent.SCROLL_TEXT.replace("{player}", player.getName().getString());
        int scrollTicks = CutsceneContent.estimateScrollTicks(resolvedScrollText);

        int overlayTicks = CutsceneContent.CARD1_TICKS + CutsceneContent.GAP1_TICKS +
                          CutsceneContent.CARD2_TICKS + CutsceneContent.GAP2_TICKS +
                          CutsceneContent.CARD3_TICKS + CutsceneContent.GAP3_TICKS +
                          CutsceneContent.CARD4_TICKS + CutsceneContent.GAP4_TICKS + scrollTicks;
        
        int rendererChangeTicks = animTicks;
        int totalTicks = rendererChangeTicks + overlayTicks;

        ModNetwork.sendToPlayer(player, new CutsceneStartS2CPacket(
            totalTicks,
            rendererChangeTicks,
            CutsceneContent.CARD1_TICKS,
            CutsceneContent.GAP1_TICKS,
            CutsceneContent.CARD2_TICKS,
            CutsceneContent.GAP2_TICKS,
            CutsceneContent.CARD3_TICKS,
            CutsceneContent.GAP3_TICKS,
            CutsceneContent.CARD4_TICKS,
            CutsceneContent.GAP4_TICKS,
            scrollTicks,
            CutsceneContent.CARD1_TITLE,
            CutsceneContent.CARD1_SUBTITLE,
            CutsceneContent.CARD2_TITLE,
            CutsceneContent.CARD2_SUBTITLE,
            CutsceneContent.CARD3_TITLE,
            CutsceneContent.CARD3_SUBTITLE,
            CutsceneContent.CARD4_TITLE,
            CutsceneContent.CARD4_SUBTITLE,
            resolvedScrollText
        ));
        
        ACTIVE_ANIMATIONS.add(new AnimationInstance(player, anim, animTicks, totalTicks));
        LOGGER.info("Started cinematic cutscene for player {}", player.getName().getString());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Iterator<AnimationInstance> iterator = ACTIVE_ANIMATIONS.iterator();
        while (iterator.hasNext()) {
            AnimationInstance anim = iterator.next();
            if (anim.player != null && END_REQUESTS.remove(anim.player.getUUID())) {
                iterator.remove();
                anim.finish();
                continue;
            }
            if (anim.tick()) {
                iterator.remove();
                anim.finish();
            }
        }
    }

    private static class AnimationInstance {
        private final ServerPlayer player;
        private final AnimationLoader.BedrockAnimation anim;
        private final int maxTicks;
        private final int animTicks;
        private int currentTick = 0;

        public AnimationInstance(
            ServerPlayer player,
            AnimationLoader.BedrockAnimation anim,
            int animTicks, int maxTicks
        ) {
            this.player = player;
            this.anim = anim;
            this.animTicks = animTicks;
            this.maxTicks = maxTicks;
        }

        public boolean tick() {
            if (currentTick >= maxTicks) return true;
            
            // On server, we don't need to do much if the client is handling visuals.
            // But we could force player position if desired.

            currentTick++;
            return false;
        }

        public void finish() {
            if (player != null) {
                ModNetwork.sendToPlayer(player, new CutsceneEndS2CPacket());
            }
        }
    }
}
