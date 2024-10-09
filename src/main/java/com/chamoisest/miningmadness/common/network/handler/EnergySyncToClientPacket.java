package com.chamoisest.miningmadness.common.network.handler;

import com.chamoisest.miningmadness.common.capabilities.AdaptedEnergyStorage;
import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.common.containers.base.BaseMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.EnergyMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.InfusionMenu;
import com.chamoisest.miningmadness.common.network.data.EnergySyncToClientPayload;
import com.chamoisest.miningmadness.common.network.data.InfusionSyncToClientPayload;
import com.chamoisest.miningmadness.setup.MiningMadnessRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class EnergySyncToClientPacket {
    public static final EnergySyncToClientPacket INSTANCE = new EnergySyncToClientPacket();

    public static EnergySyncToClientPacket get(){
        return INSTANCE;
    }

    public void handle(final EnergySyncToClientPayload payload, final IPayloadContext context){
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu menu = sender.containerMenu;

            if(menu instanceof BaseMenu baseMenu) {
                Level level = Minecraft.getInstance().level;
                if(level != null) {
                    BlockEntity menuBlockEntity = baseMenu.getBlockEntity();
                    BlockEntity senderBlockEntity = level.getBlockEntity(payload.pos());

                    if (menu instanceof EnergyMenu energyMenu && menuBlockEntity == senderBlockEntity) {
                        AdaptedEnergyStorage storage = energyMenu.getEnergyStorage();

                        storage.setEnergy(payload.storedEnergy());
                        storage.setCapacity(payload.energyCapacity());
                        storage.setUsagePerTick(payload.energyUsage());

                    }
                }
            }
        });
    }
}
