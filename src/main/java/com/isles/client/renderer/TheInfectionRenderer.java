package com.isles.client.renderer;

import com.isles.blest;
import com.isles.entity.TheInfectionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TheInfectionRenderer extends MobRenderer<TheInfectionEntity, TheInfectionModel<TheInfectionEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(blest.MODID, "textures/entity/sky_guardian.png");

    public TheInfectionRenderer(EntityRendererProvider.Context context) {
        super(context, new TheInfectionModel<>(context.bakeLayer(TheInfectionModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TheInfectionEntity entity) {
        return TEXTURE;
    }
}