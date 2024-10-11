package com.chamoisest.miningmadness.util;

import com.chamoisest.miningmadness.client.screens.elements.RangeEditButton;
import com.chamoisest.miningmadness.common.blockentities.base.enums.AreaPosEnum;
import com.chamoisest.miningmadness.common.network.data.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;

public class PacketUtil {

    //Server to clients in chunk
    public static void syncInfusionCraftingProgress(Level level, BlockPos pos, int recipeEnergyNeeded, int recipeEnergyNeededLeft, int useItemsInSlotsFlags, Map<ResourceLocation, Integer> recipeOutputInfusion, boolean isInfusing) {
        if(level != null && !level.isClientSide()){
            ServerLevel serverLevel = (ServerLevel) level;

            PacketDistributor.sendToPlayersTrackingChunk(
                    serverLevel,
                    level.getChunk(pos).getPos(),
                    new InfusionCraftingSyncToClientPayload(pos, recipeEnergyNeeded, recipeEnergyNeededLeft, useItemsInSlotsFlags, recipeOutputInfusion, isInfusing)
            );
        }
    }

    public static void syncEnergyStorage(Level level, BlockPos pos, int storedEnergy, int energyCapacity, int energyUsage){
        if(level != null && !level.isClientSide()){
            ServerLevel serverLevel = (ServerLevel) level;

            PacketDistributor.sendToPlayersTrackingChunk(
                    serverLevel,
                    level.getChunk(pos).getPos(),
                    new EnergySyncToClientPayload(pos, storedEnergy, energyCapacity, energyUsage)
            );
        }
    }

    public static void syncRangeProjector(Level level, BlockPos pos, boolean isConnected, int width, int depth, int height, BlockPos offset){
        if(level != null && !level.isClientSide()){
            ServerLevel serverLevel = (ServerLevel) level;

            PacketDistributor.sendToPlayersTrackingChunk(
                    serverLevel,
                    level.getChunk(pos).getPos(),
                    new RangeProjectorSyncToClientPayload(pos, isConnected, width, depth, height, offset)
            );
        }
    }

    //Client to server
    public static void syncMachineStatus(int status){
        PacketDistributor.sendToServer(new MachineStatusPayload(status));
    }

    public static void syncMachineRedstoneStatus(int redstoneStatus){
        PacketDistributor.sendToServer(new RedstoneStatusPayload(redstoneStatus));
    }

    public static void syncAreaDisplayToggle(boolean toggleValue){
        PacketDistributor.sendToServer(new AreaDisplayButtonPayload(toggleValue));
    }

    public static void syncAreaRange(RangeEditButton.RangeEditButtonType type, AreaPosEnum posEnum) {
        PacketDistributor.sendToServer(new RangeButtonPayload(type.ordinal(), posEnum.ordinal()));
    }
}
