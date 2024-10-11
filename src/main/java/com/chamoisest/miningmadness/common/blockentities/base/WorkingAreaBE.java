package com.chamoisest.miningmadness.common.blockentities.base;

import com.chamoisest.miningmadness.common.blockentities.data.AreaData;
import com.chamoisest.miningmadness.common.blocks.base.BaseMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public abstract class WorkingAreaBE extends BaseBE{
    protected AABB workingArea;
    protected BlockPos offset;

    protected int maxAreaWidth;
    protected int maxAreaHeight;
    protected int maxAreaDepth;

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

    protected void setMaxArea(int maxWidth, int maxHeight) {
        this.maxAreaWidth = maxWidth;
        this.maxAreaHeight = maxHeight;
        this.maxAreaDepth = getBlockPos().getY() + 64 + this.offset.getY();
    }

    protected void setMaxArea(int maxWidth, int maxHeight, int maxDepth){
        this.maxAreaWidth = maxWidth;
        this.maxAreaHeight = maxHeight;
        this.maxAreaDepth = maxDepth;
    }

    public void setArea(int areaWidth, int areaHeight, int areaDepth) {
        this.areaWidth = areaWidth;
        this.areaHeight = areaHeight;
        this.areaDepth = areaDepth;

        markDirty();
    }

    public void setOffset(BlockPos offset) {
        this.offset = offset;

        markDirty();
    }

    protected void initArea() {
        if(this.areaWidth <= 0) areaWidth = this.maxAreaWidth;
        if(this.areaHeight <= 0) areaHeight = this.maxAreaHeight;
        if(this.areaDepth <= 0) areaDepth = this.maxAreaDepth;

        BlockPos startPos = new BlockPos(0, 0, 0).relative(this.facing, 1);
        BlockPos endPos = new BlockPos(0,0,0);

        if(this.facing == Direction.EAST || this.facing == Direction.WEST) {
            double widthSplit = (double) this.areaWidth / 2;
            int widthOffset = (int) Math.floor(widthSplit);

            startPos = startPos.south(widthOffset);
            endPos = startPos.north(this.areaWidth);
            endPos = endPos.relative(this.facing, this.areaHeight);
        }else if(this.facing == Direction.NORTH || this.facing == Direction.SOUTH) {
            double heightSplit = (double) this.areaHeight / 2;
            int heightOffset = (int) Math.floor(heightSplit);

            startPos = startPos.east(heightOffset);
            endPos = startPos.west(this.areaHeight);
            endPos = endPos.relative(this.facing, this.areaWidth);
        }

        endPos = endPos.below(this.areaDepth);

        this.workingArea = AABB.encapsulatingFullBlocks(startPos, endPos);
        fixAreaSize();
        this.workingArea = this.workingArea.move(offset);
    }

    //AABB.encapsulatingFullBlocks messes up the area size, but is necessary to fix the positions of the area. This fixes the area size.
    public void fixAreaSize(){
        double minX = this.workingArea.minX;
        double minY = this.workingArea.minY;
        double minZ = this.workingArea.minZ;
        double maxX = this.workingArea.maxX;
        double maxY = this.workingArea.maxY;
        double maxZ = this.workingArea.maxZ;

        double yDiff = this.areaDepth - Math.abs(this.workingArea.minY - this.workingArea.maxY);
        double xDiff = this.areaHeight - Math.abs(this.workingArea.minX - this.workingArea.maxX);
        double zDiff = this.areaWidth - Math.abs(this.workingArea.minZ - this.workingArea.maxZ);

        if(Math.abs(minX) >= Math.abs(maxX)){ minX -= xDiff; } else maxX += xDiff;
        if(Math.abs(minY) >= Math.abs(maxY)){ minY -= yDiff; } else maxY += yDiff;
        if(Math.abs(minZ) >= Math.abs(maxZ)){ minZ -= zDiff; } else maxZ += zDiff;
        this.workingArea = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
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

    public int getAreaWidth(){
        return this.areaWidth;
    }

    public int getAreaHeight(){
        return this.areaHeight;
    }

    public int getAreaDepth(){
        return this.areaDepth;
    }

    public int getAreaMaxWidth(){
        return this.maxAreaWidth;
    }

    public int getAreaMaxHeight(){
        return this.maxAreaHeight;
    }

    public int getAreaMaxDepth(){
        return this.maxAreaDepth;
    }

    public BlockPos getOffset(){
        return this.offset;
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

        initArea();

        super.loadAdditional(tag, registries);
    }
}
