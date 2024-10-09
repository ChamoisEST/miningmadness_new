package com.chamoisest.miningmadness.common.network.handler;

import com.chamoisest.miningmadness.common.blockentities.data.RedstoneData;
import com.chamoisest.miningmadness.common.blockentities.data.StatusData;
import com.chamoisest.miningmadness.common.blockentities.interfaces.RedstoneControlledBE;
import com.chamoisest.miningmadness.common.blockentities.interfaces.StatusBE;
import com.chamoisest.miningmadness.common.containers.base.BaseMenu;
import com.chamoisest.miningmadness.common.network.data.MachineStatusPayload;
import com.chamoisest.miningmadness.common.network.data.RedstoneStatusPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RedstoneStatusPacket {
    public static final RedstoneStatusPacket INSTANCE = new RedstoneStatusPacket();

    public static RedstoneStatusPacket get(){
        return INSTANCE;
    }

    public void handle(final RedstoneStatusPayload payload, final IPayloadContext context){
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu menu = sender.containerMenu;

            if(menu instanceof BaseMenu baseMenu && baseMenu.getBlockEntity() instanceof RedstoneControlledBE redstoneBe){
                redstoneBe.setRedstoneStatus(RedstoneData.RedstoneStatus.getEnumValue(payload.status()));
            }
        });
    }
}
