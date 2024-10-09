package com.chamoisest.miningmadness.common.blocks;

import com.chamoisest.miningmadness.common.blockentities.QuarryBE;
import com.chamoisest.miningmadness.common.blocks.base.BaseMachineBlock;
import com.chamoisest.miningmadness.common.containers.QuarryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class QuarryBlock extends BaseMachineBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public QuarryBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(3f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new QuarryBE(pos, state);
    }

    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof QuarryBE quarry) {

            player.openMenu(new SimpleMenuProvider(
                            (containerId, playerInv, playerEntity) -> new QuarryMenu(containerId, playerInv, quarry.getBlockPos()), Component.translatable("")),
                    buf -> buf.writeBlockPos(pos)
            );
        } else {
            throw new IllegalStateException("Container provider is missing!");
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(BlockStateProperties.LIT);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(LIT, Boolean.FALSE);
    }

    @Override
    protected VoxelShape[] getShape() {
        return new VoxelShape[]{
                Stream.of(
                        Block.box(0, 0, 0, 16, 6, 16),
                        Block.box(0, 11, 0, 16, 16, 16),
                        Block.box(14, 6, 0, 16, 11, 16),
                        Block.box(0, 6, 0, 2, 11, 16),
                        Block.box(2, 6, 1, 14, 11, 16),
                        Block.box(3, 9, 0, 5, 10, 1)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//UP
                Stream.of(
                        Block.box(0, 0, 0, 16, 6, 16),
                        Block.box(0, 11, 0, 16, 16, 16),
                        Block.box(14, 6, 0, 16, 11, 16),
                        Block.box(0, 6, 0, 2, 11, 16),
                        Block.box(2, 6, 1, 14, 11, 16),
                        Block.box(3, 9, 0, 5, 10, 1)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//DOWN
                Stream.of(
                        Block.box(0, 0, 0, 16, 6, 16),
                        Block.box(0, 11, 0, 16, 16, 16),
                        Block.box(14, 6, 0, 16, 11, 16),
                        Block.box(0, 6, 0, 2, 11, 16),
                        Block.box(2, 6, 1, 14, 11, 16),
                        Block.box(3, 9, 0, 5, 10, 1)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//SOUTH
                Stream.of(
                        Block.box(0, 0, 0, 16, 6, 16),
                        Block.box(0, 11, 0, 16, 16, 16),
                        Block.box(0, 6, 0, 2, 11, 16),
                        Block.box(14, 6, 0, 16, 11, 16),
                        Block.box(2, 6, 0, 14, 11, 15),
                        Block.box(11, 9, 15, 13, 10, 16)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//NORTH
                Stream.of(
                        Block.box(0, 0, 0, 16, 6, 16),
                        Block.box(0, 11, 0, 16, 16, 16),
                        Block.box(0, 6, 0, 16, 11, 2),
                        Block.box(0, 6, 14, 16, 11, 16),
                        Block.box(1, 6, 2, 16, 11, 14),
                        Block.box(0, 9, 11, 1, 10, 13)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//EAST
                Stream.of(
                        Block.box(0, 0, 0, 16, 6, 16),
                        Block.box(0, 11, 0, 16, 16, 16),
                        Block.box(0, 6, 14, 16, 11, 16),
                        Block.box(0, 6, 0, 16, 11, 2),
                        Block.box(0, 6, 2, 15, 11, 14),
                        Block.box(15, 9, 3, 16, 10, 5)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()//WEST
        };
    }


}
