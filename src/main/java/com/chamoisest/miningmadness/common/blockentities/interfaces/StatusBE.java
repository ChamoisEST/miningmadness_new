package com.chamoisest.miningmadness.common.blockentities.interfaces;

import com.chamoisest.miningmadness.common.blockentities.data.StatusData;

public interface StatusBE {
    StatusData.Status getStatus();
    void setStatus(StatusData.Status status);
    StatusData getStatusData();
    boolean addButton();

    default boolean canTickStatus(){
        if(getStatus() == StatusData.Status.INACTIVE) return false;
        else {
            if(getStatus() != StatusData.Status.ACTIVE) {
                setStatus(StatusData.Status.ACTIVE);
            }
        }

        return true;
    }
}
