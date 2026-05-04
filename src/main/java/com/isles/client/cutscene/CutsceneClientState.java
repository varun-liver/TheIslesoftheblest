package com.isles.client.cutscene;

public final class CutsceneClientState {
    private CutsceneClientState() {}

    private static boolean active = false;
    private static boolean ending = false;
    private static int totalTicks = 0;
    private static int elapsedTicks = 0;

    private static int rendererChangeTicks = 0;
    private static int card1Ticks = 0;
    private static int gap1Ticks = 0;
    private static int card2Ticks = 0;
    private static int gap2Ticks = 0;
    private static int card3Ticks = 0;
    private static int gap3Ticks = 0;
    private static int card4Ticks = 0;
    private static int gap4Ticks = 0;
    private static int scrollTicks = 0;

    private static String card1Title = "";
    private static String card1Subtitle = "";
    private static String card2Title = "";
    private static String card2Subtitle = "";
    private static String card3Title = "";
    private static String card3Subtitle = "";
    private static String card4Title = "";
    private static String card4Subtitle = "";
    private static String scrollText = "";

    private static com.isles.client.AnimationLoader.BedrockAnimation currentAnimation = null;
    private static final String MODEL_ANIM_RESOURCE = "assets/theislesoftheblest/animations/modelanimation.json";
    private static final String MODEL_ANIM_NAME = "animation";

    private static com.isles.client.renderer.CutscenePlayerModel normalModel = null;
    private static com.isles.client.renderer.CutscenePlayerModel slimModel = null;
    private static net.minecraft.client.CameraType originalPerspective = net.minecraft.client.CameraType.FIRST_PERSON;
    private static net.minecraft.world.phys.Vec3 smoothedCameraPos = net.minecraft.world.phys.Vec3.ZERO;
    private static float smoothedCameraYaw = 0f;
    private static float smoothedCameraPitch = 0f;
    private static boolean cameraSmoothingInitialized = false;

    public static com.isles.client.renderer.CutscenePlayerModel getCustomModel(boolean slim) {
        if (normalModel == null) {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            normalModel = new com.isles.client.renderer.CutscenePlayerModel(mc.getEntityModels().bakeLayer(net.minecraft.client.model.geom.ModelLayers.PLAYER), false);
            slimModel = new com.isles.client.renderer.CutscenePlayerModel(mc.getEntityModels().bakeLayer(net.minecraft.client.model.geom.ModelLayers.PLAYER_SLIM), true);
        }
        return slim ? slimModel : normalModel;
    }

    public static void start(
        int totalTicks,
        int rendererChangeTicks,
        int card1Ticks,
        int gap1Ticks,
        int card2Ticks,
        int gap2Ticks,
        int card3Ticks,
        int gap3Ticks,
        int card4Ticks,
        int gap4Ticks,
        int scrollTicks,
        String card1Title,
        String card1Subtitle,
        String card2Title,
        String card2Subtitle,
        String card3Title,
        String card3Subtitle,
        String card4Title,
        String card4Subtitle,
        String scrollText
    ) {
        CutsceneClientState.active = true;
        CutsceneClientState.ending = false;
        CutsceneClientState.totalTicks = Math.max(1, totalTicks);
        CutsceneClientState.elapsedTicks = 0;
        CutsceneClientState.rendererChangeTicks = Math.max(0, rendererChangeTicks);
        CutsceneClientState.card1Ticks = Math.max(0, card1Ticks);
        CutsceneClientState.gap1Ticks = Math.max(0, gap1Ticks);
        CutsceneClientState.card2Ticks = Math.max(0, card2Ticks);
        CutsceneClientState.gap2Ticks = Math.max(0, gap2Ticks);
        CutsceneClientState.card3Ticks = Math.max(0, card3Ticks);
        CutsceneClientState.gap3Ticks = Math.max(0, gap3Ticks);
        CutsceneClientState.card4Ticks = Math.max(0, card4Ticks);
        CutsceneClientState.gap4Ticks = Math.max(0, gap4Ticks);
        CutsceneClientState.scrollTicks = Math.max(0, scrollTicks);
        CutsceneClientState.card1Title = card1Title == null ? "" : card1Title;
        CutsceneClientState.card1Subtitle = card1Subtitle == null ? "" : card1Subtitle;
        CutsceneClientState.card2Title = card2Title == null ? "" : card2Title;
        CutsceneClientState.card2Subtitle = card2Subtitle == null ? "" : card2Subtitle;
        CutsceneClientState.card3Title = card3Title == null ? "" : card3Title;
        CutsceneClientState.card3Subtitle = card3Subtitle == null ? "" : card3Subtitle;
        CutsceneClientState.card4Title = card4Title == null ? "" : card4Title;
        CutsceneClientState.card4Subtitle = card4Subtitle == null ? "" : card4Subtitle;
        CutsceneClientState.scrollText = scrollText == null ? "" : scrollText;
        CutsceneClientState.smoothedCameraPos = net.minecraft.world.phys.Vec3.ZERO;
        CutsceneClientState.smoothedCameraYaw = 0f;
        CutsceneClientState.smoothedCameraPitch = 0f;
        CutsceneClientState.cameraSmoothingInitialized = false;

        try {
            currentAnimation = com.isles.client.AnimationLoader.loadBedrockAnimation(MODEL_ANIM_RESOURCE, MODEL_ANIM_NAME);
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            originalPerspective = mc.options.getCameraType();
            mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_BACK);
        } catch (Exception e) {
            currentAnimation = null;
        }
    }

    public static void end() {
        if (active) {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            mc.options.setCameraType(originalPerspective);
        }
        CutsceneClientState.active = false;
        CutsceneClientState.ending = false;
        CutsceneClientState.totalTicks = 0;
        CutsceneClientState.elapsedTicks = 0;
        CutsceneClientState.rendererChangeTicks = 0;
        CutsceneClientState.card1Ticks = 0;
        CutsceneClientState.gap1Ticks = 0;
        CutsceneClientState.card2Ticks = 0;
        CutsceneClientState.gap2Ticks = 0;
        CutsceneClientState.card3Ticks = 0;
        CutsceneClientState.gap3Ticks = 0;
        CutsceneClientState.card4Ticks = 0;
        CutsceneClientState.gap4Ticks = 0;
        CutsceneClientState.scrollTicks = 0;
        CutsceneClientState.card1Title = "";
        CutsceneClientState.card1Subtitle = "";
        CutsceneClientState.card2Title = "";
        CutsceneClientState.card2Subtitle = "";
        CutsceneClientState.card3Title = "";
        CutsceneClientState.card3Subtitle = "";
        CutsceneClientState.card4Title = "";
        CutsceneClientState.card4Subtitle = "";
        CutsceneClientState.scrollText = "";
        currentAnimation = null;
        CutsceneClientState.smoothedCameraPos = net.minecraft.world.phys.Vec3.ZERO;
        CutsceneClientState.smoothedCameraYaw = 0f;
        CutsceneClientState.smoothedCameraPitch = 0f;
        CutsceneClientState.cameraSmoothingInitialized = false;
    }

    public static com.isles.client.AnimationLoader.BedrockAnimation getCurrentAnimation() {
        return currentAnimation;
    }

    private static float partialTicks = 0f;

    public static void setPartialTicks(float pt) {
        partialTicks = pt;
    }

    public static float getInterpolatedTicks() {
        if (!active) return 0f;
        return elapsedTicks + partialTicks;
    }

    public static float getAnimationTimeSeconds() {
        if (!active || currentAnimation == null) return 0f;
        float progress = clamp01(getInterpolatedTicks() / Math.max(1, rendererChangeTicks));
        return currentAnimation.lengthSeconds * progress;
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean isEnding() {
        return ending;
    }

    public static int getTotalTicks() {
        return totalTicks;
    }

    public static int getElapsedTicks() {
        return elapsedTicks;
    }

    public static int getRendererChangeTicks() {
        return rendererChangeTicks;
    }

    public static int getCard1Ticks() {
        return card1Ticks;
    }

    public static int getGap1Ticks() {
        return gap1Ticks;
    }

    public static int getCard2Ticks() {
        return card2Ticks;
    }

    public static int getGap2Ticks() {
        return gap2Ticks;
    }

    public static int getCard3Ticks() {
        return card3Ticks;
    }

    public static int getGap3Ticks() {
        return gap3Ticks;
    }

    public static int getCard4Ticks() {
        return card4Ticks;
    }

    public static int getGap4Ticks() {
        return gap4Ticks;
    }

    public static int getScrollTicks() {
        return scrollTicks;
    }

    public static String getCard1Title() {
        return card1Title;
    }

    public static String getCard1Subtitle() {
        return card1Subtitle;
    }

    public static String getCard2Title() {
        return card2Title;
    }

    public static String getCard2Subtitle() {
        return card2Subtitle;
    }

    public static String getCard3Title() {
        return card3Title;
    }

    public static String getCard3Subtitle() {
        return card3Subtitle;
    }

    public static String getCard4Title() {
        return card4Title;
    }

    public static String getCard4Subtitle() {
        return card4Subtitle;
    }

    public static String getScrollText() {
        return scrollText;
    }

    public static net.minecraft.world.phys.Vec3 smoothCameraPosition(net.minecraft.world.phys.Vec3 targetPos, double alpha) {
        if (!cameraSmoothingInitialized) {
            smoothedCameraPos = targetPos;
            return smoothedCameraPos;
        }
        smoothedCameraPos = smoothedCameraPos.lerp(targetPos, clamp01(alpha));
        return smoothedCameraPos;
    }

    public static float smoothCameraYaw(float targetYaw, float alpha) {
        if (!cameraSmoothingInitialized) {
            smoothedCameraYaw = targetYaw;
            return smoothedCameraYaw;
        }
        smoothedCameraYaw = net.minecraft.util.Mth.rotLerp(clamp01(alpha), smoothedCameraYaw, targetYaw);
        return smoothedCameraYaw;
    }

    public static float smoothCameraPitch(float targetPitch, float alpha) {
        if (!cameraSmoothingInitialized) {
            smoothedCameraPitch = targetPitch;
            cameraSmoothingInitialized = true;
            return smoothedCameraPitch;
        }
        smoothedCameraPitch += (targetPitch - smoothedCameraPitch) * clamp01(alpha);
        return smoothedCameraPitch;
    }

    private static float clamp01(double value) {
        if (value < 0.0) return 0f;
        if (value > 1.0) return 1f;
        return (float) value;
    }

    /**
     * Begin a fade-out-to-clear and end the overlay when complete.
     * The overlay will stop drawing text during this phase.
     */
    public static void beginEnd(int fadeOutTicks) {
        if (!active) return;
        if (ending) return;
        ending = true;
        int extra = Math.max(1, fadeOutTicks);
        totalTicks = Math.max(elapsedTicks + 1, elapsedTicks + extra);
    }

    public static void clientTick() {
        if (!active) return;
        elapsedTicks++;
        if (elapsedTicks >= totalTicks) {
            end();
        }
    }
}
