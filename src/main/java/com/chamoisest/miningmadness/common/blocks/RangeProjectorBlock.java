package com.chamoisest.miningmadness.common.blocks;

import com.chamoisest.miningmadness.common.blockentities.QuarryBE;
import com.chamoisest.miningmadness.common.blockentities.RangeProjectorBE;
import com.chamoisest.miningmadness.common.blockentities.base.WorkingAreaBE;
import com.chamoisest.miningmadness.common.blocks.base.BaseMachineBlock;
import com.chamoisest.miningmadness.common.containers.QuarryMenu;
import com.chamoisest.miningmadness.common.containers.RangeProjectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class RangeProjectorBlock extends BaseMachineBlock {

    public RangeProjectorBlock() {
        super(Properties.of()
                .strength(3f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RangeProjectorBE(pos, state);
    }

    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RangeProjectorBE rangeProjectorBE) {

            player.openMenu(new SimpleMenuProvider(
                            (containerId, playerInv, playerEntity) -> new RangeProjectorMenu(containerId, playerInv, rangeProjectorBE.getBlockPos()), Component.translatable("")),
                    buf -> buf.writeBlockPos(pos)
            );
        } else {
            throw new IllegalStateException("Container provider is missing!");
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity blockEntityUnder = level.getBlockEntity(pos.below());

        if(blockEntityUnder instanceof WorkingAreaBE) {
            BlockState blockStateUnder = level.getBlockState(pos.below());
            if(blockStateUnder.hasProperty(BlockStateProperties.FACING)) {
                Direction direction = blockStateUnder.getValue(BlockStateProperties.FACING).getOpposite();
                return this.defaultBlockState().setValue(BlockStateProperties.FACING, direction);
            }
        }

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        BlockPos belowPos = pos.below();
        if(neighbor.equals(belowPos)) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if(blockEntity instanceof RangeProjectorBE be) {
                be.initialized = false;
            }
        }
    }

    @Override
    protected VoxelShape[] getShape() {
        return new VoxelShape[]{
                Stream.of(
                    Block.box(6, 2, 8, 10, 12, 10),
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 7, 7, 14, 15, 8),
                    Block.box(2, 7, 6, 4, 15, 7),
                    Block.box(12, 7, 6, 14, 15, 7),
                    Block.box(4, 7, 6, 12, 9, 7),
                    Block.box(4, 13, 6, 12, 15, 7)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//UP
                Stream.of(
                    Block.box(6, 2, 8, 10, 12, 10),
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 7, 7, 14, 15, 8),
                    Block.box(2, 7, 6, 4, 15, 7),
                    Block.box(12, 7, 6, 14, 15, 7),
                    Block.box(4, 7, 6, 12, 9, 7),
                    Block.box(4, 13, 6, 12, 15, 7)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//DOWN
                Stream.of(
                    Block.box(6, 2, 8, 10, 12, 10),
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(2, 7, 7, 14, 15, 8),
                    Block.box(2, 7, 6, 4, 15, 7),
                    Block.box(12, 7, 6, 14, 15, 7),
                    Block.box(4, 7, 6, 12, 9, 7),
                    Block.box(4, 13, 6, 12, 15, 7)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//SOUTH
                Stream.of(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(6, 2, 6, 10, 12, 8),
                    Block.box(2, 7, 8, 14, 15, 9),
                    Block.box(12, 7, 9, 14, 15, 10),
                    Block.box(2, 7, 9, 4, 15, 10),
                    Block.box(4, 7, 9, 12, 9, 10),
                    Block.box(4, 13, 9, 12, 15, 10)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//NORTH
                Stream.of(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(8, 2, 6, 10, 12, 10),
                    Block.box(7, 7, 2, 8, 15, 14),
                    Block.box(6, 7, 12, 7, 15, 14),
                    Block.box(6, 7, 2, 7, 15, 4),
                    Block.box(6, 7, 4, 7, 9, 12),
                    Block.box(6, 13, 4, 7, 15, 12)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//EAST
                Stream.of(
                    Block.box(1, 0, 1, 15, 2, 15),
                    Block.box(6, 2, 6, 8, 12, 10),
                    Block.box(8, 7, 2, 9, 15, 14),
                    Block.box(9, 7, 2, 10, 15, 4),
                    Block.box(9, 7, 12, 10, 15, 14),
                    Block.box(9, 7, 4, 10, 9, 12),
                    Block.box(9, 13, 4, 10, 15, 12)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()//WEST
        };
    }


}
