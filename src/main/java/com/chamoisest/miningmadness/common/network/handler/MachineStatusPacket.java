package com.chamoisest.miningmadness.common.network.handler;

import com.chamoisest.miningmadness.common.blockentities.data.StatusData;
import com.chamoisest.miningmadness.common.blockentities.interfaces.StatusBE;
import com.chamoisest.miningmadness.common.containers.base.BaseMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.StatusMenu;
import com.chamoisest.miningmadness.common.network.data.MachineStatusPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MachineStatusPacket {
    public static final MachineStatusPacket INSTANCE = new MachineStatusPacket();

    public static MachineStatusPacket get(){
        return INSTANCE;
    }

    public void handle(final MachineStatusPayload payload, final IPayloadContext context){
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu menu = sender.containerMenu;

            if(menu instanceof BaseMenu baseMenu && baseMenu.getBlockEntity() instanceof StatusBE statusBe){
                statusBe.setStatus(StatusData.Status.getEnumValue(payload.status()));
            }
        });
    }
}
