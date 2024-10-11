package com.chamoisest.miningmadness.common.network.handler;

import com.chamoisest.miningmadness.client.screens.elements.RangeEditButton;
import com.chamoisest.miningmadness.common.blockentities.RangeProjectorBE;
import com.chamoisest.miningmadness.common.blockentities.base.WorkingAreaBE;
import com.chamoisest.miningmadness.common.blockentities.base.enums.AreaPosEnum;
import com.chamoisest.miningmadness.common.containers.base.BaseMenu;
import com.chamoisest.miningmadness.common.network.data.AreaDisplayButtonPayload;
import com.chamoisest.miningmadness.common.network.data.RangeButtonPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Arrays;

public class RangeButtonPacket {
    public static final RangeButtonPacket INSTANCE = new RangeButtonPacket();

    public static RangeButtonPacket get(){
        return INSTANCE;
    }

    public void handle(final RangeButtonPayload payload, final IPayloadContext context){
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu menu = sender.containerMenu;

            if(menu instanceof BaseMenu baseMenu && baseMenu.getBlockEntity() instanceof RangeProjectorBE rangeProjectorBE){
                WorkingAreaBE be = rangeProjectorBE.getConnectedBE();
                if(be != null){
                    switch(AreaPosEnum.values()[payload.areaPosEnumInt()]){
                        case RAD_X -> be.setArea(be.getAreaWidth(), getNewValue(be.getAreaHeight(), be.getAreaMaxHeight(), payload.buttonTypeEnumInt(), AreaPosEnum.RAD_X), be.getAreaDepth());
                        case RAD_Y -> be.setArea(be.getAreaWidth(), be.getAreaHeight(), getNewValue(be.getAreaDepth(), be.getAreaMaxDepth(), payload.buttonTypeEnumInt(), AreaPosEnum.RAD_Y));
                        case RAD_Z -> be.setArea(getNewValue(be.getAreaWidth(), be.getAreaMaxWidth(), payload.buttonTypeEnumInt(), AreaPosEnum.RAD_Z), be.getAreaHeight(), be.getAreaDepth());
                        case OFF_X -> be.setOffset(new BlockPos(getNewValue(be.getOffset().getX(), be.getAreaMaxWidth(), payload.buttonTypeEnumInt(), AreaPosEnum.OFF_X), be.getOffset().getY(), be.getOffset().getZ()));
                        case OFF_Y -> be.setOffset(new BlockPos(be.getOffset().getX(), getNewValue(be.getOffset().getY(), be.getAreaMaxDepth(), payload.buttonTypeEnumInt(), AreaPosEnum.OFF_Y), be.getOffset().getZ()));
                        case OFF_Z -> be.setOffset(new BlockPos(be.getOffset().getX(), be.getOffset().getY(), getNewValue(be.getOffset().getZ(), be.getAreaMaxHeight(), payload.buttonTypeEnumInt(), AreaPosEnum.OFF_Z)));
                    }

                    rangeProjectorBE.needsSync = true;
                }
            }
        });
    }

    public int getNewValue(int value, int maxValue, int btnTypeOrdinal, AreaPosEnum areaPosEnum){
        RangeEditButton.RangeEditButtonType type = RangeEditButton.RangeEditButtonType.values()[btnTypeOrdinal];
        AreaPosEnum[] minOneTypes = {AreaPosEnum.RAD_X, AreaPosEnum.RAD_Y, AreaPosEnum.RAD_Z};
        int newValue = value + type.getChangeValue();

        if(Arrays.stream(minOneTypes).anyMatch(posEnum -> posEnum == areaPosEnum) && newValue < 1) return 1;
        if(newValue >= maxValue) return maxValue;
        if(newValue < -maxValue) return -maxValue;

        return newValue;
    }
}
