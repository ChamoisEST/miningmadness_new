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
                        case RAD_X -> be.setArea(be.getAreaWidth(), getNewValue(be.getAreaHeight(), payload.buttonTypeEnumInt()), be.getAreaDepth(), true);
                        case RAD_Y -> be.setArea(be.getAreaWidth(), be.getAreaHeight(), getNewValue(be.getAreaDepth(),payload.buttonTypeEnumInt()), true);
                        case RAD_Z -> be.setArea(getNewValue(be.getAreaWidth(), payload.buttonTypeEnumInt()), be.getAreaHeight(), be.getAreaDepth(), true);
                        case OFF_X -> be.setOffset(new BlockPos(getNewValue(be.getOffset().getX(), payload.buttonTypeEnumInt()), be.getOffset().getY(), be.getOffset().getZ()), true);
                        case OFF_Y -> be.setOffset(new BlockPos(be.getOffset().getX(), getNewValue(be.getOffset().getY(), payload.buttonTypeEnumInt()), be.getOffset().getZ()), true);
                        case OFF_Z -> be.setOffset(new BlockPos(be.getOffset().getX(), be.getOffset().getY(), getNewValue(be.getOffset().getZ(), payload.buttonTypeEnumInt())), true);
                    }

                    rangeProjectorBE.needsSync = true;
                }
            }
        });
    }

    public int getNewValue(int value, int btnTypeOrdinal){
        RangeEditButton.RangeEditButtonType type = RangeEditButton.RangeEditButtonType.values()[btnTypeOrdinal];

        return value + type.getChangeValue();
    }
}
