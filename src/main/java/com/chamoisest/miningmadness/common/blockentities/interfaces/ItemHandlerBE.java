package com.chamoisest.miningmadness.common.blockentities.interfaces;

import com.chamoisest.miningmadness.common.capabilities.CombinedItemHandler;

public interface ItemHandlerBE {
    CombinedItemHandler getItemHandler();
    int getSlotCount();
    void markDirty();
    boolean canInsertItems();
    boolean canExtractItems();
}
