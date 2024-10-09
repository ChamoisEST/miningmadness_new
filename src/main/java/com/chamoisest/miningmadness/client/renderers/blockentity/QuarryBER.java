package com.chamoisest.miningmadness.client.renderers.blockentity;

import com.chamoisest.miningmadness.client.renderers.blockentity.base.AreaDisplayBER;
import com.chamoisest.miningmadness.common.blockentities.QuarryBE;
import com.chamoisest.miningmadness.common.blockentities.base.WorkingAreaBE;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class QuarryBER extends AreaDisplayBER<QuarryBE> {
    public QuarryBER(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(WorkingAreaBE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
