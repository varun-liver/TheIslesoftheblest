package com.isles.client.renderer;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class TheInfectionModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "theinfection"), "main");
	private final ModelPart bone;
	private final ModelPart body;
	private final ModelPart arm1;
	private final ModelPart arm11;
	private final ModelPart arm2;
	private final ModelPart arm21;
	private final ModelPart leg2;
	private final ModelPart leg1;
	private final ModelPart head;

	public TheInfectionModel(ModelPart root) {
		this.bone = root.getChild("bone");
		this.body = this.bone.getChild("body");
		this.arm1 = this.bone.getChild("arm1");
		this.arm11 = this.arm1.getChild("arm11");
		this.arm2 = this.bone.getChild("arm2");
		this.arm21 = this.arm2.getChild("arm21");
		this.leg2 = this.bone.getChild("leg2");
		this.leg1 = this.bone.getChild("leg1");
		this.head = this.bone.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(6.0F, 5.0F, 17.0F));

		PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -29.0F, -14.0F, 15.0F, 28.0F, 30.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 0.0F, -17.0F));

		PartDefinition arm1 = bone.addOrReplaceChild("arm1", CubeListBuilder.create(), PartPose.offset(-13.0F, -23.0F, 0.0F));

		PartDefinition cube_r1 = arm1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(68, 58).addBox(-12.0F, -4.0F, -5.0F, 23.0F, 9.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 5.0F, 4.0F, 0.0F, 0.0F, 0.9948F));

		PartDefinition arm11 = arm1.addOrReplaceChild("arm11", CubeListBuilder.create(), PartPose.offset(13.0F, 16.0F, 8.0F));

		PartDefinition cube_r2 = arm11.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(90, 31).addBox(-5.3437F, -2.8231F, -1.0F, 17.0F, 9.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.0F, -8.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition arm2 = bone.addOrReplaceChild("arm2", CubeListBuilder.create(), PartPose.offset(-14.0F, -20.0F, -31.0F));

		PartDefinition cube_r3 = arm2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(68, 78).addBox(-12.0F, -4.0F, -6.0F, 23.0F, 9.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 3.0F, -5.0F, 0.0F, 0.0F, 0.9948F));

		PartDefinition arm21 = arm2.addOrReplaceChild("arm21", CubeListBuilder.create(), PartPose.offset(8.0F, 13.0F, -7.0F));

		PartDefinition cube_r4 = arm21.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(46, 98).addBox(-6.0F, -13.0F, -42.0F, 17.0F, 9.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 7.0F, 38.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition leg2 = bone.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(90, 0).addBox(-6.0F, -1.0F, -4.0F, 12.0F, 20.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(-13.0F, 0.0F, -25.0F));

		PartDefinition leg1 = bone.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 92).addBox(-6.0F, -1.0F, -5.5F, 12.0F, 20.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(-13.0F, 0.0F, -7.5F));

		PartDefinition head = bone.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 58).addBox(-5.0F, -38.0F, -24.0F, 17.0F, 17.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}