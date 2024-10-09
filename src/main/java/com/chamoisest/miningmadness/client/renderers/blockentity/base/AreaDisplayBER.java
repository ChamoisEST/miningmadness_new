package com.chamoisest.miningmadness.client.renderers.blockentity.base;

import com.chamoisest.miningmadness.client.renderers.RenderHelper;
import com.chamoisest.miningmadness.common.blockentities.base.WorkingAreaBE;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class AreaDisplayBER<T extends WorkingAreaBE> implements BlockEntityRenderer<T> {

    @Override
    public void render(WorkingAreaBE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if(blockEntity.getDisplayArea()){
            AABB aabb = blockEntity.getWorkingArea();

            Color color = new Color(0.2F, 0.57F, 0.92F, 0.175F);
            Color lineColor = new Color(0.2F, 0.57F, 0.92F, 1f);

            RenderHelper.renderLineBox(poseStack, bufferSource, aabb, lineColor);
            RenderHelper.renderTransparentBox(poseStack, bufferSource, aabb, color);
        }
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(WorkingAreaBE blockEntity) {
        return blockEntity.getWorkingArea();
    }
}
