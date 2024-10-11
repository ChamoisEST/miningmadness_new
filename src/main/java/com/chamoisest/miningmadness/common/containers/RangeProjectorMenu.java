package com.chamoisest.miningmadness.common.containers;

import com.chamoisest.miningmadness.common.containers.base.BaseMenu;
import com.chamoisest.miningmadness.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.jetbrains.annotations.NotNull;

public class RangeProjectorMenu extends BaseMenu{

    public int radX;
    public int radY;
    public int radZ;
    public int offX;
    public int offY;
    public int offZ;

    public boolean isConnected = false;

    public RangeProjectorMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, extraData.readBlockPos());
    }

    public RangeProjectorMenu(int containerId, Inventory playerInv, BlockPos pPos) {
        super(Registration.RANGE_PROJECTOR_MENU.get(), containerId, playerInv, pPos);

        addPlayerInventorySlots(playerInv, 8, 84, 142);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return AbstractContainerMenu.stillValid(ContainerLevelAccess.create(level, this.pos), player, Registration.RANGE_PROJECTOR.get());
    }

    @Override
    public int beSlotCount() {
        return 0;
    }

}
