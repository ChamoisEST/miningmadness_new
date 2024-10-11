package com.chamoisest.miningmadness.client.renderers;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

public class RenderTypes extends RenderType {

    public static final RenderType TRANSPARENT_BOX = create("transparent_box",
            DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false, true,
            CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER)  // Use the translucent shader
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)  // View offset Z layering
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)  // Enable translucent transparency
                    .setTextureState(NO_TEXTURE)  // No texture state
                    .setDepthTestState(GREATER_DEPTH_TEST)  // Depth test state
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(true));

    public RenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }
}
