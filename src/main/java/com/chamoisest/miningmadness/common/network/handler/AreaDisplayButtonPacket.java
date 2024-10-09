package com.chamoisest.miningmadness.common.network.handler;

import com.chamoisest.miningmadness.common.blockentities.base.WorkingAreaBE;
import com.chamoisest.miningmadness.common.blockentities.data.StatusData;
import com.chamoisest.miningmadness.common.blockentities.interfaces.StatusBE;
import com.chamoisest.miningmadness.common.containers.base.BaseMenu;
import com.chamoisest.miningmadness.common.network.data.AreaDisplayButtonPayload;
import com.chamoisest.miningmadness.common.network.data.MachineStatusPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class AreaDisplayButtonPacket {
    public static final AreaDisplayButtonPacket INSTANCE = new AreaDisplayButtonPacket();

    public static AreaDisplayButtonPacket get(){
        return INSTANCE;
    }

    public void handle(final AreaDisplayButtonPayload payload, final IPayloadContext context){
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu menu = sender.containerMenu;

            if(menu instanceof BaseMenu baseMenu && baseMenu.getBlockEntity() instanceof WorkingAreaBE workingAreaBe){
                workingAreaBe.setDisplayArea(payload.toggle());
            }
        });
    }
}
