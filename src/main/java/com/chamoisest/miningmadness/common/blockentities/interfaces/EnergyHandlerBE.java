package com.chamoisest.miningmadness.common.blockentities.interfaces;

import com.chamoisest.miningmadness.common.blockentities.base.BEPacketSyncEnum;
import com.chamoisest.miningmadness.common.capabilities.AdaptedEnergyStorage;

public interface EnergyHandlerBE {

    AdaptedEnergyStorage getEnergyStorage();
    int getBaseEnergyCapacity();
    int getBaseEnergyUsagePerTick();
    void syncData(BEPacketSyncEnum syncEnum);


    default int getCurrentEnergyUsagePerTick(){
        return getEnergyStorage().getUsagePerTick();
    }

    default int getStoredEnergy(){
        return getEnergyStorage().getEnergyStored();
    }

    default void setStoredEnergy(int energy){
        getEnergyStorage().setEnergy(energy);
    }

    default int getEnergyCapacity(){
        return getEnergyStorage().getMaxEnergyStored();
    }

    default void setEnergyCapacity(int capacity){
        getEnergyStorage().setCapacity(capacity);
    }

    default boolean hasEnoughEnergy(int energyNeeded){
        return getStoredEnergy() >= energyNeeded;
    }


}
