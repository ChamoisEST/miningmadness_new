package com.chamoisest.miningmadness.common.containers.base;

import com.chamoisest.miningmadness.common.blockentities.interfaces.ItemHandlerBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class BaseMenu extends AbstractContainerMenu {

    protected static int HOTBAR_SLOT_COUNT = 9;
    protected static int PLAYER_INVENTORY_ROW_COUNT = 3;
    protected static int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    protected static int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    protected static int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    protected static int VANILLA_FIRST_SLOT_INDEX = 0;
    protected static int HOTBAR_FIRST_SLOT_INDEX = PLAYER_INVENTORY_SLOT_COUNT;

    public static int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    protected Level level;
    protected Player player;
    protected BlockPos pos;
    protected BlockEntity blockEntity;
    protected int beSlotCount;


    protected BaseMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory pInventory, BlockPos pPos) {
        super(pMenuType, pContainerId);
        this.player = pInventory.player;
        this.level = this.player.level();
        this.pos = pPos;
        this.blockEntity = this.level.getBlockEntity(pPos);
        this.beSlotCount = beSlotCount();
    }

    @Override
    public abstract boolean stillValid(@NotNull Player pPlayer);
    public abstract int beSlotCount();

    protected void addPlayerInventorySlots(Inventory playerInventory, int pInvXOffset, int pInvYOffset, int pHotbarYOffset) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, pInvXOffset + l * 18, pInvYOffset + i * 18));
            }
        }

        for (int m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, pInvXOffset + m * 18, pHotbarYOffset));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {

        if(blockEntity instanceof ItemHandlerBE handlerBE) {

            Slot sourceSlot = slots.get(index);
            if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

            ItemStack sourceStack = sourceSlot.getItem();
            ItemStack copyStack = sourceStack.copy();

            if(index >= TE_INVENTORY_FIRST_SLOT_INDEX && handlerBE.canExtractItems()) {
                if(!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)){
                    return ItemStack.EMPTY;
                }
            }else if(index < TE_INVENTORY_FIRST_SLOT_INDEX && handlerBE.canInsertItems()) {
                if(!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + beSlotCount, false)){
                    return ItemStack.EMPTY;
                }
            }else if(index < TE_INVENTORY_FIRST_SLOT_INDEX && index >= HOTBAR_FIRST_SLOT_INDEX) {
                if(!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, HOTBAR_FIRST_SLOT_INDEX, false)){
                    return ItemStack.EMPTY;
                }
            }else if(index < HOTBAR_FIRST_SLOT_INDEX) {
                if(!moveItemStackTo(sourceStack, HOTBAR_FIRST_SLOT_INDEX, HOTBAR_FIRST_SLOT_INDEX + HOTBAR_SLOT_COUNT, false)){
                    return ItemStack.EMPTY;
                }
            }else{
                System.out.println("Invalid slotIndex: " + index);
                return ItemStack.EMPTY;
            }

            if (sourceStack.getCount() == 0) {
                sourceSlot.set(ItemStack.EMPTY);
            } else {
                sourceSlot.setChanged();
            }

            sourceSlot.onTake(player, sourceStack);
            return copyStack;
        }else{
            throw new IllegalStateException("BlockEntity is not an ItemHandlerBE!");
        }
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }


}
