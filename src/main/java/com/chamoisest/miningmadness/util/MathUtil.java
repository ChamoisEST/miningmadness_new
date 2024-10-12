package com.chamoisest.miningmadness.util;

import net.minecraft.core.BlockPos;

import java.awt.geom.Point2D;

public class MathUtil {
    public static boolean isFirstCloserToPoint(double p1, double p2, double resP){
        return Math.abs(p1 - resP) < Math.abs(p2 - resP);
    }

    public static Point2D.Double getCloserToPos(double x1, double z1, double x2, double z2, BlockPos pos){
        double distanceToFirst = Math.pow(pos.getX() - x1, 2) + Math.pow(pos.getZ() - z1, 2);
        double distanceToSecond = Math.pow(pos.getX() - x2, 2) + Math.pow(pos.getZ() - z2, 2);

        if(distanceToFirst > distanceToSecond) return new Point2D.Double(x2, z2);
        else return new Point2D.Double(x1, z1);
    }
}
