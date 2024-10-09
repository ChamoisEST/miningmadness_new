package com.chamoisest.miningmadness.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

import java.awt.*;

public class RenderHelper {

    public static void renderLineBox(PoseStack poseStack, MultiBufferSource buffer, AABB aabb, Color color) {
        Matrix4f matrix = poseStack.last().pose();
        PoseStack.Pose pose = poseStack.last();

        float x = (float) aabb.minX;
        float y = (float) aabb.minY;
        float z = (float) aabb.minZ;
        float dx = (float) aabb.maxX;
        float dy = (float) aabb.maxY;
        float dz = (float) aabb.maxZ;

        VertexConsumer builder = buffer.getBuffer(RenderTypes.LINES);

        //TOP
        renderLineVertex(builder, matrix, pose, color, x, dy, z, 0F, 1F, 1F);
        renderLineVertex(builder, matrix, pose, color, x, dy, dz, 0F, 1F, 1F);
        renderLineVertex(builder, matrix, pose, color, x, dy, dz, 1F, 1F, 0F);
        renderLineVertex(builder, matrix, pose, color, dx, dy, dz, 1F, 1F, 0F);
        renderLineVertex(builder, matrix, pose, color, dx, dy, dz, 0F, 1F, 1F);
        renderLineVertex(builder, matrix, pose, color, dx, dy, z, 0F, 1F, 1F);
        renderLineVertex(builder, matrix, pose, color, dx, dy, z, 1F, 1F, 0F);
        renderLineVertex(builder, matrix, pose, color, x, dy, z, 1F, 1F, 0F);

        //SIDES
        renderLineVertex(builder, matrix, pose, color, x, dy, z, 1F, 0F, 1F);
        renderLineVertex(builder, matrix, pose, color, x, y, z, 1F, 0F, 1F);
        renderLineVertex(builder, matrix, pose, color, x, dy, dz, 1F, 0F, 1F);
        renderLineVertex(builder, matrix, pose, color, x, y, dz, 1F, 0F, 1F);
        renderLineVertex(builder, matrix, pose, color, dx, dy, dz, 1F, 0F, 1F);
        renderLineVertex(builder, matrix, pose, color, dx, y, dz, 1F, 0F, 1F);
        renderLineVertex(builder, matrix, pose, color, dx, dy, z, 1F, 0F, 1F);
        renderLineVertex(builder, matrix, pose, color, dx, y, z, 1F, 0F, 1F);

        //BOTTOM
        renderLineVertex(builder, matrix, pose, color, x, y, z, 0F, 1F, 1F);
        renderLineVertex(builder, matrix, pose, color, x, y, dz, 0F, 1F, 1F);
        renderLineVertex(builder, matrix, pose, color, x, y, dz, 1F, 1F, 0F);
        renderLineVertex(builder, matrix, pose, color, dx, y, dz, 1F, 1F, 0F);
        renderLineVertex(builder, matrix, pose, color, dx, y, dz, 0F, 1F, 1F);
        renderLineVertex(builder, matrix, pose, color, dx, y, z, 0F, 1F, 1F);
        renderLineVertex(builder, matrix, pose, color, dx, y, z, 1F, 1F, 0F);
        renderLineVertex(builder, matrix, pose, color, x, y, z, 1F, 1F, 0F);

    }
    public static void renderTransparentBox(PoseStack poseStack, MultiBufferSource buffer, AABB aabb, Color color) {
        Matrix4f matrix = poseStack.last().pose();
        PoseStack.Pose pose = poseStack.last();

        float x = (float) aabb.minX;
        float y = (float) aabb.minY;
        float z = (float) aabb.minZ;
        float dx = (float) aabb.maxX;
        float dy = (float) aabb.maxY;
        float dz = (float) aabb.maxZ;

        VertexConsumer builder = buffer.getBuffer(RenderTypes.TRANSPARENT_BOX);

        //TOP
        renderVertex(builder, matrix, pose, color, x, dy, z);
        renderVertex(builder, matrix, pose, color, x, dy, dz);
        renderVertex(builder, matrix, pose, color, dx, dy, dz);
        renderVertex(builder, matrix, pose, color, dx, dy, z);

        //EAST
        renderVertex(builder, matrix, pose, color, dx, dy, z);
        renderVertex(builder, matrix, pose, color, dx, dy, dz);
        renderVertex(builder, matrix, pose, color, dx, y, dz);
        renderVertex(builder, matrix, pose, color, dx, y, z);

        //NORTH
        renderVertex(builder, matrix, pose, color, x, dy, z);
        renderVertex(builder, matrix, pose, color, dx, dy, z);
        renderVertex(builder, matrix, pose, color, dx, y, z);
        renderVertex(builder, matrix, pose, color, x, y, z);

        //SOUTH
        renderVertex(builder, matrix, pose, color, x, dy, dz);
        renderVertex(builder, matrix, pose, color, dx, dy, dz);
        renderVertex(builder, matrix, pose, color, dx, y, dz);
        renderVertex(builder, matrix, pose, color, x, y, dz);

        //WEST
        renderVertex(builder, matrix, pose, color, x, dy, z);
        renderVertex(builder, matrix, pose, color, x, dy, dz);
        renderVertex(builder, matrix, pose, color, x, y, dz);
        renderVertex(builder, matrix, pose, color, x, y, z);

        //BOTTOM
        renderVertex(builder, matrix, pose, color, x, y, z);
        renderVertex(builder, matrix, pose, color, x, y, dz);
        renderVertex(builder, matrix, pose, color, dx, y, dz);
        renderVertex(builder, matrix, pose, color, dx, y, z);

    }

    public static void renderLineVertex(VertexConsumer builder, Matrix4f matrix4f, PoseStack.Pose pose, Color color,
                                        float x, float y, float z, float normalX, float normalY, float normalZ) {
        builder.addVertex(matrix4f, x, y, z).setColor(color.getRGB()).setNormal(pose, normalX, normalY, normalZ);
    }

    public static void renderVertex(VertexConsumer builder, Matrix4f matrix4f, PoseStack.Pose pose, Color color, float x, float y, float z) {
        builder.addVertex(matrix4f, x, y, z).setColor(color.getRGB()).setUv(0f, 0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0f, 0f, 1f);
    };
}
