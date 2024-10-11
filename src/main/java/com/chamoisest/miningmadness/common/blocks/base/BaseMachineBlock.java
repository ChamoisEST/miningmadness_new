package com.chamoisest.miningmadness.common.blocks.base;

import com.chamoisest.miningmadness.common.blockentities.RangeProjectorBE;
import com.chamoisest.miningmadness.common.blockentities.base.BaseBE;
import com.chamoisest.miningmadness.common.blockentities.base.WorkingAreaBE;
import com.chamoisest.miningmadness.common.blockentities.data.RedstoneData;
import com.chamoisest.miningmadness.common.blockentities.interfaces.*;
import com.chamoisest.miningmadness.common.capabilities.infusion.IInfusionStorage;
import com.chamoisest.miningmadness.setup.MiningMadnessCapabilities;
import com.chamoisest.miningmadness.setup.MiningMadnessDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseMachineBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private final VoxelShape[] shapes;

    public BaseMachineBlock(Properties properties) {
        super(properties);
        this.shapes = getShape();
    }

    protected abstract void openContainer(Level level, BlockPos pos, Player player);
    protected abstract VoxelShape[] getShape();

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes[state.getValue(FACING).get3DDataValue()];
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {

            this.openContainer(level, pos, player);

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof BaseBE base) {
                base.needSyncAll = true;
                base.needInfusionUpdate = true;
            }

            if(blockEntity instanceof RangeProjectorBE be){
                be.needsSync = true;
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()) {
            return null;
        }else{
            return (lvl, pos, st, be) -> {
                if(be instanceof BaseBE tickableBE) {
                    tickableBE.tickServer();
                }
                if(be instanceof RangeProjectorBE rangeBE) {
                    rangeBE.handleTicks();
                }
            };
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if(!level.isClientSide && placer instanceof Player) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if(blockEntity instanceof EnergyHandlerBE handlerBe) {
                if(stack.get(MiningMadnessDataComponents.MACHINE_ENERGY) != null && stack.get(MiningMadnessDataComponents.MACHINE_ENERGY_CAPACITY) != null) {
                    handlerBe.setStoredEnergy(stack.get(MiningMadnessDataComponents.MACHINE_ENERGY));
                    handlerBe.setEnergyCapacity(stack.get(MiningMadnessDataComponents.MACHINE_ENERGY_CAPACITY));
                }
            }
            if(blockEntity instanceof RedstoneControlledBE redstoneBe){
                if(stack.get(MiningMadnessDataComponents.MACHINE_REDSTONE_STATUS) != null){
                    redstoneBe.setRedstoneStatus(RedstoneData.RedstoneStatus.getEnumValue(stack.get(MiningMadnessDataComponents.MACHINE_REDSTONE_STATUS)));
                }
            }

            if(blockEntity instanceof WorkingAreaBE workingAreaBE){
                if(stack.get(MiningMadnessDataComponents.AREA_MACHINE_OFFSET) != null){
                    workingAreaBE.setOffset(stack.get(MiningMadnessDataComponents.AREA_MACHINE_OFFSET));
                }

                if(stack.get(MiningMadnessDataComponents.AREA_MACHINE_WA) != null){
                    BlockPos workArea = stack.get(MiningMadnessDataComponents.AREA_MACHINE_WA);
                    workingAreaBE.setArea(workArea.getX(), workArea.getZ(), workArea.getY());
                }
            }

            if(blockEntity instanceof InfusionHandlerBE infusionBe){
                IInfusionStorage infusionCap = stack.getCapability(MiningMadnessCapabilities.InfusionStorage.ITEM);
                if(infusionCap != null){
                    infusionBe.getInfusionStorage().setContainedInfusions(infusionCap.getContainedInfusions());
                }
            }

        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof ItemHandlerBE handlerBE) {
            IItemHandler itemHandler = handlerBE.getItemHandler();
            for(int i = 0; i < itemHandler.getSlots(); i++) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemHandler.getStackInSlot(i));
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        BlockEntity be = params.getParameter(LootContextParams.BLOCK_ENTITY);

        ItemStack itemStack = new ItemStack(Item.byBlock(this));
        removeComponentsFromStack(itemStack);

        if(be instanceof EnergyHandlerBE handlerBe){
            itemStack.set(MiningMadnessDataComponents.MACHINE_ENERGY, handlerBe.getStoredEnergy());
            itemStack.set(MiningMadnessDataComponents.MACHINE_ENERGY_CAPACITY, handlerBe.getEnergyCapacity());
        }

        if(be instanceof RedstoneControlledBE redstoneBe){
            itemStack.set(MiningMadnessDataComponents.MACHINE_REDSTONE_STATUS, redstoneBe.getRedstoneStatus().getNumericalValue());
        }

        if(be instanceof WorkingAreaBE workingAreaBE){
            itemStack.set(MiningMadnessDataComponents.AREA_MACHINE_OFFSET, workingAreaBE.getOffset());
            itemStack.set(MiningMadnessDataComponents.AREA_MACHINE_WA, new BlockPos(workingAreaBE.getAreaWidth(), workingAreaBE.getAreaDepth(), workingAreaBE.getAreaHeight()));
        }

        if(be instanceof InfusionHandlerBE infusionBe){
            IInfusionStorage infusionCap = itemStack.getCapability(MiningMadnessCapabilities.InfusionStorage.ITEM);
            if(infusionCap != null) {
                infusionCap.setContainedInfusions(infusionBe.getInfusionStorage().getContainedInfusions());
            }
        }

        drops.clear();
        drops.add(itemStack);

        return drops;
    }

    protected void removeComponentsFromStack(ItemStack stack) {
        stack.remove(MiningMadnessDataComponents.MACHINE_ENERGY);
        stack.remove(MiningMadnessDataComponents.MACHINE_ENERGY_CAPACITY);
        stack.remove(MiningMadnessDataComponents.MACHINE_REDSTONE_STATUS);
        stack.remove(MiningMadnessDataComponents.AREA_MACHINE_OFFSET);
        stack.remove(MiningMadnessDataComponents.AREA_MACHINE_WA);
    }


}
