package com.chamoisest.miningmadness.common.capabilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

public class CombinedItemHandler extends CombinedInvWrapper implements INBTSerializable<CompoundTag> {

    public static final String HANDLER_TAG_PRE = "item_handler_";
    public final ItemStackHandler[] handlers;

    public CombinedItemHandler(ItemStackHandler... itemHandler) {
        super(itemHandler);
        this.handlers = itemHandler;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        int i = 0;
        for (ItemStackHandler handler : handlers) {
            nbt.put(HANDLER_TAG_PRE + i, handler.serializeNBT(provider));
            i++;
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        int i = 0;
        for (ItemStackHandler handler : handlers) {
            if(nbt.contains(HANDLER_TAG_PRE + i)) {
                handler.deserializeNBT(provider, nbt.getCompound(HANDLER_TAG_PRE + i));
            }
            i++;
        }
    }
}
