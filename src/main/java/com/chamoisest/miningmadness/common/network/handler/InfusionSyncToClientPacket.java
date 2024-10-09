package com.chamoisest.miningmadness.common.network.handler;

import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.common.containers.InfusingStationMenu;
import com.chamoisest.miningmadness.common.containers.base.BaseMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.InfusionMenu;
import com.chamoisest.miningmadness.common.network.data.InfusionSyncToClientPayload;
import com.chamoisest.miningmadness.setup.MiningMadnessRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class InfusionSyncToClientPacket {
    public static final InfusionSyncToClientPacket INSTANCE = new InfusionSyncToClientPacket();

    public static InfusionSyncToClientPacket get(){
        return INSTANCE;
    }

    public void handle(final InfusionSyncToClientPayload payload, final IPayloadContext context){
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu menu = sender.containerMenu;

            if(menu instanceof BaseMenu baseMenu) {
                Level level = Minecraft.getInstance().level;
                if (level != null) {
                    BlockEntity menuBlockEntity = baseMenu.getBlockEntity();
                    BlockEntity senderBlockEntity = level.getBlockEntity(payload.pos());

                    if (menu instanceof InfusionMenu infusionMenu && menuBlockEntity == senderBlockEntity) {
                        int infusionId = payload.infusionId();

                        Infusion infusion = MiningMadnessRegistries.INFUSIONS.byId(infusionId);
                        if(menu instanceof InfusingStationMenu stationMenu) {
                            stationMenu.infusionStorage.setInfusionData(infusion, payload.tier(), payload.tierPoints());
                        }else {
                            infusionMenu.getInfusionStorage().setInfusionData(infusion, payload.tier(), payload.tierPoints());
                        }
                    }
                }
            }
        });
    }
}
