package com.chamoisest.miningmadness.common.network.handler;

import com.chamoisest.miningmadness.common.containers.RangeProjectorMenu;
import com.chamoisest.miningmadness.common.containers.base.BaseMenu;
import com.chamoisest.miningmadness.common.network.data.RangeProjectorSyncToClientPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RangeProjectorSyncToClientPacket {
    public static final RangeProjectorSyncToClientPacket INSTANCE = new RangeProjectorSyncToClientPacket();

    public static RangeProjectorSyncToClientPacket get(){
        return INSTANCE;
    }

    public void handle(final RangeProjectorSyncToClientPayload payload, final IPayloadContext context){
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu menu = sender.containerMenu;


            if(menu instanceof BaseMenu baseMenu) {
                Level level = Minecraft.getInstance().level;
                if (level != null) {
                    BlockEntity menuBlockEntity = baseMenu.getBlockEntity();
                    BlockEntity senderBlockEntity = level.getBlockEntity(payload.pos());

                    if (menu instanceof RangeProjectorMenu rangeMenu && menuBlockEntity == senderBlockEntity) {
                        rangeMenu.isConnected = payload.isConnected();
                        rangeMenu.radZ = payload.radX();
                        rangeMenu.radY = payload.radY();
                        rangeMenu.radX = payload.radZ();
                        rangeMenu.offX = payload.offset().getX();
                        rangeMenu.offY = payload.offset().getY();
                        rangeMenu.offZ = payload.offset().getZ();
                    }
                }
            }
        });
    }
}
