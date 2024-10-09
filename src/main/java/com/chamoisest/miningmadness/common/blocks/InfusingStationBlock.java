package com.chamoisest.miningmadness.common.blocks;

import com.chamoisest.miningmadness.common.blockentities.InfusingStationBE;
import com.chamoisest.miningmadness.common.blocks.base.BaseMachineBlock;
import com.chamoisest.miningmadness.common.containers.InfusingStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class InfusingStationBlock extends BaseMachineBlock {


    public InfusingStationBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(3f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InfusingStationBE(pos, state);
    }

    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if(be instanceof InfusingStationBE blockEntity) {
            player.openMenu(new SimpleMenuProvider(
                            (containerId, playerInv, playerEntity) -> new InfusingStationMenu(containerId, playerInv, blockEntity.getBlockPos()), Component.translatable("")),
                    buf -> buf.writeBlockPos(pos)
            );
        }else{
            throw new IllegalStateException("Container provider is missing!");
        }
    }

    @Override
    protected VoxelShape[] getShape() {
        return new VoxelShape[]{
                Stream.of(
                        Block.box(0, 0, 0, 16, 6, 16),
                        Block.box(11, 6, 13, 13, 13, 16),
                        Block.box(3, 6, 11, 5, 13, 16),
                        Block.box(0, 6, 11, 3, 13, 13),
                        Block.box(11, 6, 11, 16, 13, 13),
                        Block.box(0, 6, 3, 5, 13, 5),
                        Block.box(13, 6, 3, 16, 13, 5),
                        Block.box(11, 6, 0, 13, 13, 5),
                        Block.box(3, 6, 0, 5, 13, 3),
                        Block.box(11, 13, 3, 16, 16, 13),
                        Block.box(0, 13, 3, 5, 16, 13),
                        Block.box(5, 13, 11, 11, 16, 16),
                        Block.box(11, 13, 13, 13, 16, 16),
                        Block.box(3, 13, 13, 5, 16, 16),
                        Block.box(3, 13, 0, 5, 16, 3),
                        Block.box(11, 13, 0, 13, 16, 3)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//UP
                Stream.of(
                        Block.box(0, 0, 0, 16, 6, 16),
                        Block.box(11, 6, 13, 13, 13, 16),
                        Block.box(3, 6, 11, 5, 13, 16),
                        Block.box(0, 6, 11, 3, 13, 13),
                        Block.box(11, 6, 11, 16, 13, 13),
                        Block.box(0, 6, 3, 5, 13, 5),
                        Block.box(13, 6, 3, 16, 13, 5),
                        Block.box(11, 6, 0, 13, 13, 5),
                        Block.box(3, 6, 0, 5, 13, 3),
                        Block.box(11, 13, 3, 16, 16, 13),
                        Block.box(0, 13, 3, 5, 16, 13),
                        Block.box(5, 13, 11, 11, 16, 16),
                        Block.box(11, 13, 13, 13, 16, 16),
                        Block.box(3, 13, 13, 5, 16, 16),
                        Block.box(3, 13, 0, 5, 16, 3),
                        Block.box(11, 13, 0, 13, 16, 3)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//DOWN
                Stream.of(
                        Block.box(0, 0, 0, 16, 6, 16),
                        Block.box(11, 6, 13, 13, 13, 16),
                        Block.box(3, 6, 11, 5, 13, 16),
                        Block.box(0, 6, 11, 3, 13, 13),
                        Block.box(11, 6, 11, 16, 13, 13),
                        Block.box(0, 6, 3, 5, 13, 5),
                        Block.box(13, 6, 3, 16, 13, 5),
                        Block.box(11, 6, 0, 13, 13, 5),
                        Block.box(3, 6, 0, 5, 13, 3),
                        Block.box(11, 13, 3, 16, 16, 13),
                        Block.box(0, 13, 3, 5, 16, 13),
                        Block.box(5, 13, 11, 11, 16, 16),
                        Block.box(11, 13, 13, 13, 16, 16),
                        Block.box(3, 13, 13, 5, 16, 16),
                        Block.box(3, 13, 0, 5, 16, 3),
                        Block.box(11, 13, 0, 13, 16, 3)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//SOUTH
                Stream.of(
                        Block.box(0, 0, 0, 16, 6, 16),
                        Block.box(3, 6, 0, 5, 13, 3),
                        Block.box(11, 6, 0, 13, 13, 5),
                        Block.box(13, 6, 3, 16, 13, 5),
                        Block.box(0, 6, 3, 5, 13, 5),
                        Block.box(11, 6, 11, 16, 13, 13),
                        Block.box(0, 6, 11, 3, 13, 13),
                        Block.box(3, 6, 11, 5, 13, 16),
                        Block.box(11, 6, 13, 13, 13, 16),
                        Block.box(0, 13, 3, 5, 16, 13),
                        Block.box(11, 13, 3, 16, 16, 13),
                        Block.box(5, 13, 0, 11, 16, 5),
                        Block.box(3, 13, 0, 5, 16, 3),
                        Block.box(11, 13, 0, 13, 16, 3),
                        Block.box(11, 13, 13, 13, 16, 16),
                        Block.box(3, 13, 13, 5, 16, 16)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//NORTH
                Stream.of(
                        Block.box(0, 0, 0, 16, 6, 16),
                        Block.box(13, 6, 3, 16, 13, 5),
                        Block.box(11, 6, 11, 16, 13, 13),
                        Block.box(11, 6, 13, 13, 13, 16),
                        Block.box(11, 6, 0, 13, 13, 5),
                        Block.box(3, 6, 11, 5, 13, 16),
                        Block.box(3, 6, 0, 5, 13, 3),
                        Block.box(0, 6, 3, 5, 13, 5),
                        Block.box(0, 6, 11, 3, 13, 13),
                        Block.box(3, 13, 0, 13, 16, 5),
                        Block.box(3, 13, 11, 13, 16, 16),
                        Block.box(11, 13, 5, 16, 16, 11),
                        Block.box(13, 13, 3, 16, 16, 5),
                        Block.box(13, 13, 11, 16, 16, 13),
                        Block.box(0, 13, 11, 3, 16, 13),
                        Block.box(0, 13, 3, 3, 16, 5)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),//EAST
                Stream.of(
                        Block.box(0, 0, 0, 16, 6, 16),
                        Block.box(0, 6, 11, 3, 13, 13),
                        Block.box(0, 6, 3, 5, 13, 5),
                        Block.box(3, 6, 0, 5, 13, 3),
                        Block.box(3, 6, 11, 5, 13, 16),
                        Block.box(11, 6, 0, 13, 13, 5),
                        Block.box(11, 6, 13, 13, 13, 16),
                        Block.box(11, 6, 11, 16, 13, 13),
                        Block.box(13, 6, 3, 16, 13, 5),
                        Block.box(3, 13, 11, 13, 16, 16),
                        Block.box(3, 13, 0, 13, 16, 5),
                        Block.box(0, 13, 5, 5, 16, 11),
                        Block.box(0, 13, 11, 3, 16, 13),
                        Block.box(0, 13, 3, 3, 16, 5),
                        Block.box(13, 13, 3, 16, 16, 5),
                        Block.box(13, 13, 11, 16, 16, 13)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()//WEST
        };
    }
}
