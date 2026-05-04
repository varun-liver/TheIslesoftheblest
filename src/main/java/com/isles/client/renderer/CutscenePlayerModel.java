package com.isles.client.renderer;

import com.isles.client.cutscene.CutsceneClientState;
import com.isles.client.AnimationLoader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.player.Player;

public class CutscenePlayerModel extends PlayerModel<Player> {
    public CutscenePlayerModel(ModelPart root, boolean slim) {
        super(root, slim);
    }

    @Override
    public void setupAnim(Player entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        applyCutscenePose(this);
    }

    public static void applyCutscenePose(PlayerModel<?> model) {
        if (!CutsceneClientState.isActive()) return;
        AnimationLoader.BedrockAnimation anim = CutsceneClientState.getCurrentAnimation();
        if (anim == null) return;

        float time = CutsceneClientState.getAnimationTimeSeconds();

        // Apply rotations and positions to all parts
        applyBone(model.head, anim, "head", time);
        applyBone(model.hat, anim, "head", time);

        applyBone(model.body, anim, "body", time);
        applyBone(model.jacket, anim, "body", time);

        // Arms - support both arm1/arm2 and left_arm/right_arm
        String leftArmName = anim.hasBone("arm1") ? "arm1" : "left_arm";
        applyBone(model.leftArm, anim, leftArmName, time);
        applyBone(model.leftSleeve, anim, leftArmName, time);

        String rightArmName = anim.hasBone("arm2") ? "arm2" : "right_arm";
        applyBone(model.rightArm, anim, rightArmName, time);
        applyBone(model.rightSleeve, anim, rightArmName, time);

        // Legs - support both leg1/leg2 and left_leg/right_leg
        String leftLegName = anim.hasBone("leg1") ? "leg1" : "left_leg";
        applyBone(model.leftLeg, anim, leftLegName, time);
        applyBone(model.leftPants, anim, leftLegName, time);

        String rightLegName = anim.hasBone("leg2") ? "leg2" : "right_leg";
        applyBone(model.rightLeg, anim, rightLegName, time);
        applyBone(model.rightPants, anim, rightLegName, time);
    }

    private static void applyBone(ModelPart part, AnimationLoader.BedrockAnimation anim, String boneName, float time) {
        if (anim.hasBone(boneName)) {
            // Rotation (Degrees to Radians)
            float[] rot = anim.sampleRotation(boneName, time);
            part.xRot = (float) Math.toRadians(rot[0]);
            part.yRot = (float) Math.toRadians(rot[1]);
            part.zRot = (float) Math.toRadians(rot[2]);

            // Position (Bedrock relative offset)
            float[] pos = anim.samplePosition(boneName, time);
            // We apply position as an offset to the part's default pivot
            // Note: In Minecraft model space, Y is down, so we invert Bedrock's Up Y.
            part.x += pos[0];
            part.y -= pos[1]; 
            part.z += pos[2];
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (!CutsceneClientState.isActive()) {
            super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }

        AnimationLoader.BedrockAnimation anim = CutsceneClientState.getCurrentAnimation();
        if (anim == null) {
            super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }

        float time = CutsceneClientState.getAnimationTimeSeconds();
        float[] groupPos = anim.samplePosition("group", time);
        float[] groupRot = anim.sampleRotation("group", time);

        poseStack.pushPose();
        poseStack.translate(groupPos[0] / 16.0F, 0.0F, groupPos[2] / 16.0F);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-groupRot[1]));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(groupRot[0]));
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(groupRot[2]));

        // Render parts with scaling
        renderPartWithScale(this.head, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha, anim, "head");
        renderPartWithScale(this.hat, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha, anim, "head");
        renderPartWithScale(this.body, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha, anim, "body");
        renderPartWithScale(this.jacket, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha, anim, "body");

        renderPartWithScale(this.leftArm, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha, anim, "arm1");
        renderPartWithScale(this.leftSleeve, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha, anim, "arm1");
        
        renderPartWithScale(this.rightArm, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha, anim, "arm2");
        renderPartWithScale(this.rightSleeve, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha, anim, "arm2");

        renderPartWithScale(this.leftLeg, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha, anim, "leg1");
        renderPartWithScale(this.leftPants, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha, anim, "leg1");

        renderPartWithScale(this.rightLeg, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha, anim, "leg2");
        renderPartWithScale(this.rightPants, poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha, anim, "leg2");

        poseStack.popPose();

        // Skip ear and cloak for simplicity since they are private and usually not used in cutscenes
    }

    private void renderPartWithScale(ModelPart part, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, AnimationLoader.BedrockAnimation anim, String boneName) {
        if (!anim.hasBone(boneName)) {
            part.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }
        
        float time = CutsceneClientState.getAnimationTimeSeconds();
        float[] scale = anim.sampleScale(boneName, time);
        
        poseStack.pushPose();
        poseStack.translate(part.x / 16.0F, part.y / 16.0F, part.z / 16.0F);
        poseStack.scale(scale[0], scale[1], scale[2]);
        poseStack.translate(-part.x / 16.0F, -part.y / 16.0F, -part.z / 16.0F);
        part.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }
}
