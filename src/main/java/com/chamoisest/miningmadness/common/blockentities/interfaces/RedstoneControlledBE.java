package com.chamoisest.miningmadness.common.blockentities.interfaces;

import com.chamoisest.miningmadness.common.blockentities.base.BaseBE;
import com.chamoisest.miningmadness.common.blockentities.data.RedstoneData;

public interface RedstoneControlledBE {
    RedstoneData.RedstoneStatus getRedstoneStatus();
    void setRedstoneStatus(RedstoneData.RedstoneStatus redstoneStatus);
    RedstoneData getRedstoneData();
    BaseBE getThis();

    default boolean canTickRedstoneStatus(){
        boolean canTick = false;
        if(getRedstoneStatus() == RedstoneData.RedstoneStatus.IGNORE) canTick = true;
        else if(getRedstoneStatus() == RedstoneData.RedstoneStatus.HIGH && hasHighNeighboringSignal()) canTick = true;
        else if(getRedstoneStatus() == RedstoneData.RedstoneStatus.LOW && hasNeighboringSignal()) canTick = true;
        else if(getRedstoneStatus() == RedstoneData.RedstoneStatus.PULSE && (hasNeighboringPulseSignal() || getThis().completingWorkCycle)) canTick = true;

        getThis().lastNeighboringRedstoneSignal = hasNeighboringSignal();

        return canTick;

    }

    default boolean hasNeighboringPulseSignal(){
        if(!getThis().lastNeighboringRedstoneSignal && hasNeighboringSignal()) return true;
        return false;
    }

    default boolean hasNeighboringSignal(){
        if(getThis().getLevel() != null) {
            return getThis().getLevel().hasNeighborSignal(getThis().getBlockPos());
        }
        return false;
    }

    default boolean hasHighNeighboringSignal(){
        if(getThis().getLevel() != null) {
            int bestRedstoneSignal = getThis().getLevel().getBestNeighborSignal(getThis().getBlockPos());
            return bestRedstoneSignal >= 15;
        }
        return false;
    }
}
