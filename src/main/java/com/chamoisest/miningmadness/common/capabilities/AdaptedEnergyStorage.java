package com.chamoisest.miningmadness.common.capabilities;

import com.chamoisest.miningmadness.common.blockentities.base.enums.BEPacketSyncEnum;
import com.chamoisest.miningmadness.common.blockentities.interfaces.EnergyHandlerBE;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class AdaptedEnergyStorage implements IEnergyStorage, INBTSerializable<CompoundTag> {

    EnergyHandlerBE energyHandlerBE;

    public int energy;
    public int capacity;
    public int usagePerTick = 0;
    public int energyPerOp = 0;
    public int energyPerOpLeft = 0;

    public AdaptedEnergyStorage(int capacity, int baseUsagePerTick, EnergyHandlerBE energyHandlerBE) {
        this.energyHandlerBE = energyHandlerBE;
        this.capacity = capacity;
        this.usagePerTick = baseUsagePerTick;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (!canReceive() || toReceive <= 0) {
            return 0;
        }

        int energyReceived = Mth.clamp(this.capacity - this.energy, 0, toReceive);
        if (!simulate) {
            this.energy += energyReceived;
            sendBlockUpdated();
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        if (!canExtract() || toExtract <= 0) {
            return 0;
        }

        int energyExtracted = Math.min(this.energy, toExtract);
        if (!simulate) {
            this.energy -= energyExtracted;
            sendBlockUpdated();
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public void setUsagePerTick(int usagePerTick) {
        this.usagePerTick = usagePerTick;
    }
    public void setEnergyPerOp(int energyPerOp) {
        this.energyPerOp = energyPerOp;
    }
    public void setEnergyPerOpLeft(int energyPerOpLeft) {
        this.energyPerOpLeft = energyPerOpLeft;
    }

    public int getUsagePerTick() {
        return usagePerTick;
    }
    public int getEnergyPerOp() {
        return energyPerOp;
    }
    public int getEnergyPerOpLeft() {
        return energyPerOpLeft;
    }

    public void extractUsagePerTick() {
        this.extractEnergy(usagePerTick, false);
    }

    private void sendBlockUpdated(){
        energyHandlerBE.syncData(BEPacketSyncEnum.ENERGY);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("energyStored", energy);
        tag.putInt("energyCapacity", capacity);
        tag.putInt("usagePerTick", usagePerTick);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        if (nbt.contains("energyStored")) {
            energy = nbt.getInt("energyStored");
        }

        if (nbt.contains("energyCapacity")) {
            capacity = nbt.getInt("energyCapacity");
        }

        if (nbt.contains("usagePerTick")) {
            usagePerTick = nbt.getInt("usagePerTick");
        }
    }
}
