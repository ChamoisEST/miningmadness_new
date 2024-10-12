package com.chamoisest.miningmadness.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class GeometryUtil {
    public static boolean AABBContainsWithMax(AABB aabb, BlockPos pos) {
        return pos.getX() >= aabb.minX && pos.getX() <= aabb.maxX && pos.getY() >= aabb.minY && pos.getY() <= aabb.maxY && pos.getZ() >= aabb.minZ && pos.getZ() <= aabb.maxZ;
    }
}
