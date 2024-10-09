package com.chamoisest.miningmadness.common.blockentities.base;

import com.chamoisest.miningmadness.common.blockentities.data.AreaData;
import com.chamoisest.miningmadness.common.blocks.QuarryBlock;
import com.chamoisest.miningmadness.common.blocks.base.BaseMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public abstract class WorkingAreaBE extends BaseBE{
    protected AABB workingArea;
    protected BlockPos offset;

    protected int areaWidth = 0;
    protected int areaHeight = 0;
    protected int areaDepth = 0;

    protected final Direction facing;

    protected AreaData areaData;
    protected boolean displayArea = false;

    public WorkingAreaBE(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        if(this.getBlockState().getBlock() instanceof BaseMachineBlock) {
            this.facing = getBlockState().getValue(BaseMachineBlock.FACING).getOpposite();
        }else{
            this.facing = Direction.NORTH;
        }

        this.offset = new BlockPos(0, 0, 0);
        this.areaData = new AreaData(this);

    }

    protected void setArea(int areaWidth, int areaHeight, int areaDepth) {
        this.areaWidth = areaWidth;
        this.areaHeight = areaHeight;
        this.areaDepth = areaDepth;
    }

    protected void setOffset(BlockPos offset) {
        this.offset = offset;
    }

    protected void initArea() {
        if(this.areaWidth > 0 && this.areaHeight > 0 && this.areaDepth > 0) {
            double widthSplit = (double) this.areaWidth / 2;
            int widthOffset = (int) Math.floor(widthSplit);

            BlockPos startPos = new BlockPos(this.offset.getX(), this.offset.getY() + 1, this.offset.getZ());
            startPos = startPos.relative(this.facing.getCounterClockWise(), widthOffset);

            BlockPos endPos = startPos.relative(this.facing.getClockWise(), this.areaWidth);
            endPos = endPos.relative(Direction.DOWN, this.areaDepth);
            endPos = endPos.relative(this.facing, this.areaHeight);

            this.workingArea = new AABB(startPos.getX(), startPos.getY(), startPos.getZ(), endPos.getX(), endPos.getY(), endPos.getZ());

        }
    }

    public AABB getWorkingArea() {
        return this.workingArea;
    }

    public boolean getDisplayArea() {
        return this.displayArea;
    }

    public void setDisplayArea(boolean displayArea) {
        this.displayArea = displayArea;
    }

    public AreaData getAreaData(){
        return this.areaData;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("areaWidth", areaWidth);
        tag.putInt("areaHeight", areaHeight);
        tag.putInt("areaDepth", areaDepth);
        tag.put("offsetPos", NbtUtils.writeBlockPos(offset));

        tag.putBoolean("displayArea", displayArea);

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {

        if(tag.contains("areaWidth")) {
            this.areaWidth = tag.getInt("areaWidth");
        }

        if(tag.contains("areaHeight")) {
            this.areaHeight = tag.getInt("areaHeight");
        }

        if(tag.contains("areaDepth")) {
            this.areaDepth = tag.getInt("areaDepth");
        }

        if(tag.contains("offsetPos")) {
            this.offset = NbtUtils.readBlockPos(tag, "offsetPos").orElse(null);
        }

        if(tag.contains("displayArea")) {
            this.displayArea = tag.getBoolean("displayArea");
        }

        super.loadAdditional(tag, registries);
    }
}
