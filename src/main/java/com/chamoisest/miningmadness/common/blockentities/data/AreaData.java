package com.chamoisest.miningmadness.common.blockentities.data;

import com.chamoisest.miningmadness.common.blockentities.base.WorkingAreaBE;
import com.chamoisest.miningmadness.common.blockentities.interfaces.StatusBE;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;

public class AreaData implements ContainerData {
    private final WorkingAreaBE blockEntity;
    public static int DATA_SLOTS = 1;

    public AreaData(WorkingAreaBE blockEntity){
        this.blockEntity = blockEntity;
    }
    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> (blockEntity.getDisplayArea() ? 1 : 0);
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    @Override
    public void set(int index, int value) {
        switch (index) {
            case 0 -> blockEntity.setDisplayArea(value == 1);
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    @Override
    public int getCount() {
        return DATA_SLOTS;
    }

    public boolean getDisplayAreaStatus() {
        return get(0) == 1;
    }

    public void setDisplayAreaStatus(boolean status) {
        set(0, status ? 1 : 0);
    }
}
