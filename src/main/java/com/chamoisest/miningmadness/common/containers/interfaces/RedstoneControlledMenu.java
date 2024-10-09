package com.chamoisest.miningmadness.common.containers.interfaces;

import com.chamoisest.miningmadness.common.blockentities.data.RedstoneData;

public interface RedstoneControlledMenu {
    RedstoneData getRedstoneData();

    default RedstoneData.RedstoneStatus getNewRedstoneStatus(){
        return switch(getRedstoneData().getRedstoneStatus()){
            case HIGH -> RedstoneData.RedstoneStatus.LOW;
            case LOW -> RedstoneData.RedstoneStatus.PULSE;
            case PULSE -> RedstoneData.RedstoneStatus.IGNORE;
            case IGNORE -> RedstoneData.RedstoneStatus.HIGH;
        };
    }
}
