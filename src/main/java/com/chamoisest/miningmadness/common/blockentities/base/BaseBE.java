package com.chamoisest.miningmadness.common.blockentities.base;

import com.chamoisest.miningmadness.common.blockentities.InfusingStationBE;
import com.chamoisest.miningmadness.common.blockentities.base.enums.BEPacketSyncEnum;
import com.chamoisest.miningmadness.common.blockentities.data.RedstoneData;
import com.chamoisest.miningmadness.common.blockentities.data.StatusData;
import com.chamoisest.miningmadness.common.blockentities.interfaces.*;
import com.chamoisest.miningmadness.common.items.GemOfFocusItem;
import com.chamoisest.miningmadness.common.items.block.interfaces.InfusionItem;
import com.chamoisest.miningmadness.util.PacketUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseBE extends BlockEntity {

    public boolean completingWorkCycle = false;
    public boolean lastNeighboringRedstoneSignal = false;

    public boolean needInfusionUpdate = false;
    public boolean needSyncAll = false;

    public BaseBE(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public abstract void handleTicks();
    public abstract void doInfusionCalculations();

    public void forceHandleTicks(){

    } //Used for operations that ignore status and redstone controls.

    public void tickServer(){
        forceHandleTicks();
        if(this.level != null && needSyncAll){
            syncAll();
        }

        if(this.level != null && needInfusionUpdate){
            doInfusionCalculations();
        }

        if(this instanceof StatusBE be){
            if(!be.canTickStatus()) return;
            be.setStatus(StatusData.Status.ACTIVE);
        }

        if(this instanceof RedstoneControlledBE be){
            if(!be.canTickRedstoneStatus()) return;
        }

        handleTicks();
    }

    public void markDirty(){
        setChanged();

        if(level != null){
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.loadAdditional(tag, lookupProvider);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        this.loadAdditional(pkt.getTag(), lookupProvider);
        if(this instanceof WorkingAreaBE areaBE)
            areaBE.initArea();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        if(this instanceof StatusBE statusBe){
            tag.putInt("machine_status", statusBe.getStatus().getNumericalValue());
        }
        if(this instanceof RedstoneControlledBE redstoneBe){
            tag.putInt("redstone_status", redstoneBe.getRedstoneStatus().getNumericalValue());
        }
        if(this instanceof ItemHandlerBE itemHandlerBE){
            tag.put("inventory", itemHandlerBE.getItemHandler().serializeNBT(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if(this instanceof StatusBE statusBe && tag.contains("machine_status")){
            statusBe.setStatus(StatusData.Status.getEnumValue(tag.getInt("machine_status")));
        }
        if(this instanceof RedstoneControlledBE redstoneBe){
            redstoneBe.setRedstoneStatus(RedstoneData.RedstoneStatus.getEnumValue(tag.getInt("redstone_status")));
        }
        if(this instanceof ItemHandlerBE handlerBE){
            handlerBE.getItemHandler().deserializeNBT(registries, tag.getCompound("inventory"));
        }

        super.loadAdditional(tag, registries);
    }

    public void syncData(BEPacketSyncEnum syncEnum){
        switch (syncEnum){
            case ENERGY: {
                if(this instanceof EnergyHandlerBE be){
                    PacketUtil.syncEnergyStorage(this.level, this.getBlockPos(), be.getStoredEnergy(), be.getEnergyCapacity(), be.getCurrentEnergyUsagePerTick());
                }
            }
            case INFUSION: {
                if(this instanceof InfusionHandlerBE be){
                    be.getInfusionStorage().setChanged();
                }
            }
            case INFUSION_CRAFTING: {
                if(this instanceof InfusingStationBE be){
                    PacketUtil.syncInfusionCraftingProgress(this.level, this.getBlockPos(), be.recipeEnergyNeeded, be.recipeEnergyNeededLeft, be.useItemsInSlotsFlags, be.recipeOutputInfusion, be.isInfusing);
                }
            }
        }
    }

    public void syncAll(){
        for(BEPacketSyncEnum syncEnum : BEPacketSyncEnum.values()){
            if(this instanceof InfusingStationBE be){
                ItemStack infusionStack = be.getItemHandler().getStackInSlot(0);
                if(infusionStack.getItem() instanceof InfusionItem && !(infusionStack.getItem() instanceof GemOfFocusItem)) continue;
            }

            syncData(syncEnum);
        }
        this.needSyncAll = false;
    }

    public ItemStackHandler createItemHandler(int slots){
        return new ItemStackHandler(slots){
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    public BaseBE getThis() {
        return this;
    }
}
