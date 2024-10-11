package com.chamoisest.miningmadness.common.blockentities;

import com.chamoisest.miningmadness.common.blockentities.base.WorkingAreaBE;
import com.chamoisest.miningmadness.setup.Registration;
import com.chamoisest.miningmadness.util.PacketUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class RangeProjectorBE extends BlockEntity {

    public WorkingAreaBE connectedBE = null;
    public boolean initialized = false;
    public boolean needsSync = false;

    public RangeProjectorBE(BlockPos pos, BlockState blockState) {
        super(Registration.RANGE_PROJECTOR_BE.get(), pos, blockState);
        initConnectedBE();
    }

    protected void initConnectedBE(){
        if(this.level != null) {
            BlockPos underPos = getBlockPos().below();
            BlockEntity underBlockEntity = this.level.getBlockEntity(underPos);
            if(underBlockEntity instanceof WorkingAreaBE be) {
                setConnectedBE(be);
                setFacing(be);
            }else{
                setConnectedBE(null);
            }
            this.initialized = true;
        }
    }

    public void handleTicks(){
        if(!initialized) initConnectedBE();
        if(needsSync && initialized && connectedBE != null) syncRangeData();
    }

    protected void syncRangeData(){
        PacketUtil.syncRangeProjector(this.level, this.getBlockPos(), true, connectedBE.getAreaWidth(), connectedBE.getAreaDepth(), connectedBE.getAreaHeight(), connectedBE.getOffset());
        needsSync = false;
    }

    public void setFacing(WorkingAreaBE connectedBE){
        BlockState belowState = connectedBE.getBlockState();
        if(belowState.hasProperty(BlockStateProperties.FACING)){

            assert level != null;
            BlockState state = this.getBlockState();
            BlockState newState = state.setValue(BlockStateProperties.FACING, belowState.getValue(BlockStateProperties.FACING).getOpposite());
            level.setBlockAndUpdate(this.getBlockPos(), newState);
        }
    }

    public void setConnectedBE(WorkingAreaBE connectedBE){
        this.connectedBE = connectedBE;
    }

    public WorkingAreaBE getConnectedBE(){
        return connectedBE;
    }
}
