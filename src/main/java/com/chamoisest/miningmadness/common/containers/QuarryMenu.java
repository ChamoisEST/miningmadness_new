package com.chamoisest.miningmadness.common.containers;

import com.chamoisest.miningmadness.common.blockentities.QuarryBE;
import com.chamoisest.miningmadness.common.blockentities.base.WorkingAreaBE;
import com.chamoisest.miningmadness.common.blockentities.data.AreaData;
import com.chamoisest.miningmadness.common.blockentities.data.RedstoneData;
import com.chamoisest.miningmadness.common.blockentities.data.StatusData;
import com.chamoisest.miningmadness.common.blockentities.interfaces.*;
import com.chamoisest.miningmadness.common.capabilities.AdaptedEnergyStorage;
import com.chamoisest.miningmadness.common.capabilities.infusion.IInfusionStorage;
import com.chamoisest.miningmadness.common.containers.base.BaseMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.*;
import com.chamoisest.miningmadness.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class QuarryMenu extends BaseMenu implements EnergyMenu, StatusMenu, RedstoneControlledMenu, InfusionMenu, AreaMenu {


    public StatusData statusData;
    public RedstoneData redstoneData;
    public AreaData areaData;

    public IInfusionStorage infusionStorage;
    public AdaptedEnergyStorage energyStorage;

    public QuarryMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, extraData.readBlockPos());
    }

    public QuarryMenu(int containerId, Inventory playerInv, BlockPos pPos) {
        super(Registration.QUARRY_MENU.get(), containerId, playerInv, pPos);

        addPlayerInventorySlots(playerInv, 8, 84, 142);

        if(this.blockEntity instanceof QuarryBE be){
            addSlot(new SlotItemHandler(be.getItemHandler(), 0, 16, 17));
            addSlot(new SlotItemHandler(be.getItemHandler(), 1, 34, 17));
            addSlot(new SlotItemHandler(be.getItemHandler(), 2, 52, 17));
            addSlot(new SlotItemHandler(be.getItemHandler(), 3, 70, 17));
            addSlot(new SlotItemHandler(be.getItemHandler(), 4, 16, 35));
            addSlot(new SlotItemHandler(be.getItemHandler(), 5, 34, 35));
            addSlot(new SlotItemHandler(be.getItemHandler(), 6, 52, 35));
            addSlot(new SlotItemHandler(be.getItemHandler(), 7, 70, 35));
            addSlot(new SlotItemHandler(be.getItemHandler(), 8, 16, 53));
            addSlot(new SlotItemHandler(be.getItemHandler(), 9, 34, 53));
            addSlot(new SlotItemHandler(be.getItemHandler(), 10, 52, 53));
            addSlot(new SlotItemHandler(be.getItemHandler(), 11, 70, 53));
        }

        if(this.blockEntity instanceof StatusBE be){
            this.statusData = be.getStatusData();
            addDataSlots(statusData);
        }
        if(this.blockEntity instanceof RedstoneControlledBE be){
            this.redstoneData = be.getRedstoneData();
            addDataSlots(redstoneData);
        }
        if(this.blockEntity instanceof WorkingAreaBE be){
            this.areaData = be.getAreaData();
            addDataSlots(areaData);
        }

        if(this.blockEntity instanceof InfusionHandlerBE be){
            this.infusionStorage = be.getInfusionStorage();
        }
        if(this.blockEntity instanceof EnergyHandlerBE be){
            this.energyStorage = be.getEnergyStorage();
        }

    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return AbstractContainerMenu.stillValid(ContainerLevelAccess.create(level, this.pos), player, Registration.QUARRY.get());
    }

    @Override
    public int beSlotCount() {
        return 12;
    }


    @Override
    public AdaptedEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public StatusData getStatusData() {
        return statusData;
    }

    @Override
    public RedstoneData getRedstoneData() {
        return redstoneData;
    }

    @Override
    public IInfusionStorage getInfusionStorage() {
        return infusionStorage;
    }

    @Override
    public AreaData getAreaData() {
        return areaData;
    }
}
