package com.chamoisest.miningmadness.common.containers;

import com.chamoisest.miningmadness.common.blockentities.InfusingStationBE;
import com.chamoisest.miningmadness.common.blockentities.data.RedstoneData;
import com.chamoisest.miningmadness.common.blockentities.data.StatusData;
import com.chamoisest.miningmadness.common.blockentities.interfaces.EnergyHandlerBE;
import com.chamoisest.miningmadness.common.blockentities.interfaces.InfusionHandlerBE;
import com.chamoisest.miningmadness.common.blockentities.interfaces.RedstoneControlledBE;
import com.chamoisest.miningmadness.common.blockentities.interfaces.StatusBE;
import com.chamoisest.miningmadness.common.capabilities.AdaptedEnergyStorage;
import com.chamoisest.miningmadness.common.capabilities.infusion.IInfusionStorage;
import com.chamoisest.miningmadness.common.capabilities.infusion.InfusionStorage;
import com.chamoisest.miningmadness.common.containers.base.BaseMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.EnergyMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.InfusionMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.RedstoneControlledMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.StatusMenu;
import com.chamoisest.miningmadness.setup.MiningMadnessCapabilities;
import com.chamoisest.miningmadness.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class InfusingStationMenu extends BaseMenu implements EnergyMenu, RedstoneControlledMenu, InfusionMenu, StatusMenu {

    public StatusData statusData;
    public RedstoneData redstoneData;

    public IInfusionStorage infusionStorage;
    public IInfusionStorage stackInfusionStorage;

    public AdaptedEnergyStorage energyStorage;

    public int recipeEnergyNeeded;
    public int recipeEnergyNeededLeft;
    public int useItemsInSlotsFlags;
    public Map<ResourceLocation, Integer> recipeOutputInfusions;
    public boolean isInfusing = false;

    public InfusingStationMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, extraData.readBlockPos());
    }

    public InfusingStationMenu(int containerId, Inventory playerInv, BlockPos pPos) {
        super(Registration.INFUSING_STATION_MENU.get(), containerId, playerInv, pPos);

        addPlayerInventorySlots(playerInv, 8, 97, 155);

        if(this.blockEntity instanceof InfusingStationBE be){
            addSlot(new SlotItemHandler(be.getTopItemHandler(), 0, 44, 41));
            addSlot(new SlotItemHandler(be.getMiddleItemHandler(), 0, 44, 16));
            addSlot(new SlotItemHandler(be.getMiddleItemHandler(), 1, 69, 41));
            addSlot(new SlotItemHandler(be.getMiddleItemHandler(), 2, 44, 66));
            addSlot(new SlotItemHandler(be.getMiddleItemHandler(), 3, 19, 41));
            addSlot(new SlotItemHandler(be.getBottomItemHandler(), 0, 89, 66){
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false;
                }
            });
        }

        if(this.blockEntity instanceof StatusBE be){
            this.statusData = be.getStatusData();
            addDataSlots(statusData);
        }
        if(this.blockEntity instanceof RedstoneControlledBE be){
            this.redstoneData = be.getRedstoneData();
            addDataSlots(redstoneData);
        }

        if(this.blockEntity instanceof InfusionHandlerBE be){
            this.infusionStorage = be.getInfusionStorage();
        }
        if(this.blockEntity instanceof EnergyHandlerBE be){
            this.energyStorage = be.getEnergyStorage();
        }
    }

    public float getInfusingProgressPercentage(){
        return 100 - this.recipeEnergyNeededLeft / (float)this.recipeEnergyNeeded * 100;
    }

    public boolean isInfusing(){
        return this.isInfusing;
    }

    public boolean hasCraftingIngredientInSlot(int slot){
        return (this.useItemsInSlotsFlags & (int) Math.pow(2, slot + 1)) != 0;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return AbstractContainerMenu.stillValid(ContainerLevelAccess.create(level, this.pos), player, Registration.INFUSING_STATION.get());
    }

    @Override
    public int beSlotCount() {
        return 6;
    }

    @Override
    public AdaptedEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public RedstoneData getRedstoneData() {
        return redstoneData;
    }

    @Override
    public IInfusionStorage getInfusionStorage() {
        ItemStack infusionStack = getSlot(TE_INVENTORY_FIRST_SLOT_INDEX).getItem();
        if(infusionStack.getCapability(MiningMadnessCapabilities.InfusionStorage.ITEM) != null){
            if(stackInfusionStorage == null) stackInfusionStorage = new InfusionStorage(infusionStack);
            return stackInfusionStorage;
        }

        stackInfusionStorage = null;
        return infusionStorage;
    }

    @Override
    public StatusData getStatusData() {
        return statusData;
    }
}
