package com.chamoisest.miningmadness.common.containers.interfaces;

import com.chamoisest.miningmadness.common.blockentities.data.StatusData;

public interface StatusMenu {
    StatusData getStatusData();

    default StatusData.Status getNewStatus(){
        if(getStatusData().getStatus() == StatusData.Status.ACTIVE) return StatusData.Status.INACTIVE;
        else return StatusData.Status.ACTIVE;
    }
}
