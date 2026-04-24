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
        
        if (!CutsceneClientState.isActive()) return;
        AnimationLoader.BedrockAnimation anim = CutsceneClientState.getCurrentAnimation();
        if (anim == null) return;

        float time = CutsceneClientState.getInterpolatedTicks() / 20.0f;

        // Apply rotations to limbs if present in JSON
        applyBone(this.leftLeg, anim, "leg1", time);
        applyBone(this.rightLeg, anim, "leg2", time);
        applyBone(this.leftArm, anim, "arm1", time);
        applyBone(this.rightArm, anim, "arm2", time);
        
        // Armor/Outer layers
        applyBone(this.leftPants, anim, "leg1", time);
        applyBone(this.rightPants, anim, "leg2", time);
        applyBone(this.leftSleeve, anim, "arm1", time);
        applyBone(this.rightSleeve, anim, "arm2", time);
    }

    private void applyBone(ModelPart part, AnimationLoader.BedrockAnimation anim, String boneName, float time) {
        if (anim.hasBone(boneName)) {
            float[] rot = anim.sampleRotation(boneName, time);
            part.xRot = (float) Math.toRadians(rot[0]);
            part.yRot = (float) Math.toRadians(rot[1]);
            part.zRot = (float) Math.toRadians(rot[2]);
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
        
        // Skip ear and cloak for simplicity since they are private and usually not used in cutscenes
    }

    private void renderPartWithScale(ModelPart part, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, AnimationLoader.BedrockAnimation anim, String boneName) {
        if (!anim.hasBone(boneName)) {
            part.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }
        
        float time = CutsceneClientState.getInterpolatedTicks() / 20.0f;
        float[] scale = anim.sampleScale(boneName, time);
        
        poseStack.pushPose();
        poseStack.translate(part.x / 16.0F, part.y / 16.0F, part.z / 16.0F);
        poseStack.scale(scale[0], scale[1], scale[2]);
        poseStack.translate(-part.x / 16.0F, -part.y / 16.0F, -part.z / 16.0F);
        part.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }
}
