package com.chamoisest.miningmadness.common.network.handler;

import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.common.containers.InfusingStationMenu;
import com.chamoisest.miningmadness.common.containers.base.BaseMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.InfusionMenu;
import com.chamoisest.miningmadness.common.network.data.InfusionCraftingSyncToClientPayload;
import com.chamoisest.miningmadness.common.network.data.InfusionSyncToClientPayload;
import com.chamoisest.miningmadness.setup.MiningMadnessRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class InfusionCraftingSyncToClientPacket {
    public static final InfusionCraftingSyncToClientPacket INSTANCE = new InfusionCraftingSyncToClientPacket();

    public static InfusionCraftingSyncToClientPacket get(){
        return INSTANCE;
    }

    public void handle(final InfusionCraftingSyncToClientPayload payload, final IPayloadContext context){
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu menu = sender.containerMenu;

            if(menu instanceof BaseMenu baseMenu) {
                Level level = sender.level();
                if (level.isClientSide()) {
                    BlockEntity menuBlockEntity = baseMenu.getBlockEntity();
                    BlockEntity senderBlockEntity = level.getBlockEntity(payload.pos());

                    if (menu instanceof InfusingStationMenu infusingStationMenu && menuBlockEntity == senderBlockEntity) {
                        infusingStationMenu.recipeEnergyNeeded = payload.recipeEnergyNeeded();
                        infusingStationMenu.recipeEnergyNeededLeft = payload.recipeEnergyNeededLeft();
                        infusingStationMenu.useItemsInSlotsFlags = payload.useItemsInSlotsFlags();
                        infusingStationMenu.recipeOutputInfusions = payload.outputInfusions();
                        infusingStationMenu.isInfusing = payload.isInfusing();
                    }
                }
            }
        });
    }
}
