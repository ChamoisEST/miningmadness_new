package com.chamoisest.miningmadness.client.renderers.blockentity.base;

import com.chamoisest.miningmadness.client.renderers.RenderHelper;
import com.chamoisest.miningmadness.common.blockentities.QuarryBE;
import com.chamoisest.miningmadness.common.blockentities.base.WorkingAreaBE;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
    public @NotNull AABB getRenderBoundingBox(T blockEntity) {
        AABB aabb = blockEntity.getWorkingArea();
        BlockPos pos = blockEntity.getBlockPos();
        return AABB.encapsulatingFullBlocks(pos.offset((int) aabb.minX, (int) aabb.minY, (int) aabb.minZ), pos.offset((int) aabb.maxX, (int) aabb.maxY, (int) aabb.maxZ));
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull T blockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(T blockEntity, Vec3 cameraPos) {
        return true;
    }


}
