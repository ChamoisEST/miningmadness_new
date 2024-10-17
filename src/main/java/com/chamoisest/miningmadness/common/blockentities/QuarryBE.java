package com.chamoisest.miningmadness.common.blockentities;

import com.chamoisest.miningmadness.common.blockentities.base.enums.BEPacketSyncEnum;
import com.chamoisest.miningmadness.common.blockentities.base.WorkingAreaBE;
import com.chamoisest.miningmadness.common.blockentities.data.RedstoneData;
import com.chamoisest.miningmadness.common.blockentities.data.StatusData;
import com.chamoisest.miningmadness.common.blockentities.interfaces.*;
import com.chamoisest.miningmadness.common.blocks.QuarryBlock;
import com.chamoisest.miningmadness.common.capabilities.AdaptedEnergyStorage;
import com.chamoisest.miningmadness.common.capabilities.CombinedItemHandler;
import com.chamoisest.miningmadness.common.capabilities.infusion.InfusionStorage;
import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.setup.Config;
import com.chamoisest.miningmadness.setup.Registration;
import com.chamoisest.miningmadness.util.GeometryUtil;
import com.chamoisest.miningmadness.util.InfusionCalculationConfig;
import com.chamoisest.miningmadness.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Optional;

public class QuarryBE extends WorkingAreaBE implements EnergyHandlerBE, ItemHandlerBE, StatusBE, RedstoneControlledBE, InfusionHandlerBE {

    public static final int SLOT_COUNT = 12;

    public StatusData.Status status;
    public RedstoneData.RedstoneStatus redstoneStatus;

    public final StatusData statusData;
    public final RedstoneData redstoneData;

    protected CombinedItemHandler itemHandler = new CombinedItemHandler(createItemHandler(SLOT_COUNT));

    protected BlockPos currentPos = null;
    protected BlockPos lastPos = null;

    protected int fortuneLevel = 0;
    protected boolean silkTouch = false;

    protected Direction layerFirstDirection = null;
    protected BlockPos layerFirstPos = null;

    protected ItemStack tool = ItemStack.EMPTY;

    public QuarryBE(BlockPos pos, BlockState blockState) {
        super(Registration.QUARRY_BE.get(), pos, blockState);

        statusData = new StatusData(this);
        redstoneData = new RedstoneData(this);

        this.status = StatusData.Status.INACTIVE;
        this.redstoneStatus = RedstoneData.RedstoneStatus.IGNORE;

        setMaxArea(getBaseRange(), getBaseRange());
        this.needInfusionUpdate = true;
        initArea();
    }

    @Override
    public void forceHandleTicks() {
        if(getStatus() == StatusData.Status.INVERTORY_FULL && level != null){
            BlockState stateAtCurrent = level.getBlockState(currentPos);
            if(placeDropsIntoInventory(getDrops(stateAtCurrent), true)){
                setStatus(StatusData.Status.ACTIVE);
            }
        }
    }

    public void handleTicks(){
        if(isInBlackListedDimension()){
            setStatus(StatusData.Status.BLACKLISTED_DIMENSION);
            return;
        }

        if(currentPos == null) currentPos = resetPosToStart();

        AdaptedEnergyStorage energyStorage = getEnergyStorage();
        if(hasEnoughEnergy(energyStorage.getEnergyPerOp())){
            energyStorage.setEnergyPerOpLeft(energyStorage.getEnergyPerOpLeft() - energyStorage.getUsagePerTick());
            energyStorage.extractUsagePerTick();
            if(energyStorage.getEnergyPerOpLeft() <= 0){
                int timesLooped = 0;
                while(!canMine(currentPos)){
                    updatePos();

                    //testInfiniteLoop(timesLooped);
                    timesLooped++;
                }

                if(mine()) {
                    energyStorage.setEnergyPerOpLeft(energyStorage.getEnergyPerOp());
                    updatePos();
                }
            }
        }else{
            setStatus(StatusData.Status.NOT_ENOUGH_FE);
        }
    }

    private void testInfiniteLoop(int i){
        if(i >= 999800){
            System.out.println("Probably in infinite loop: pos - " + currentPos);
        }

        if(i >= 1000000){
            setStatus(StatusData.Status.INACTIVE);
            throw new IllegalStateException("Infinite loop in " + this + " while mining at position " + currentPos);
        }
    }

    protected boolean mine(){
        if(level == null) return false;

        BlockState state = level.getBlockState(currentPos);
        List<ItemStack> drops = getDrops(state);

        if(placeDropsIntoInventory(drops, true)){
            level.removeBlock(currentPos, false);
            level.playSound(null, currentPos, state.getSoundType(level, currentPos, null).getBreakSound(), SoundSource.BLOCKS);

            placeDropsIntoInventory(drops, false);
            return true;
        }else{
            setStatus(StatusData.Status.INVERTORY_FULL);
            return false;
        }
    }

    protected List<ItemStack> getDrops(BlockState state){
        return Block.getDrops(state, (ServerLevel) level, currentPos, null, null, this.tool);
    }

    protected boolean placeDropsIntoInventory(List<ItemStack> drops, boolean simulate){
        CombinedItemHandler handler = this.getItemHandler();
        for(ItemStack drop : drops){
            for(int slot = 0; slot < handler.getSlots(); slot++){
                ItemStack stack = handler.getStackInSlot(slot);
                if(!stack.isEmpty() && !drop.getItem().equals(stack.getItem())) continue;

                drop = handler.insertItem(slot, drop, simulate);

                if(drop.isEmpty()) break;
                if(slot >= handler.getSlots() - 1) return false;
            }
        }

        return true;
    }

    protected ItemStack applyEnchantmentsToFakeTool(ItemStack tool){
        assert level != null;

        HolderLookup.Provider provider = level.registryAccess();
        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = provider.lookupOrThrow(Registries.ENCHANTMENT);

        if(this.fortuneLevel > 0){
            Optional<Holder.Reference<Enchantment>> enchantment = enchantmentLookup.get(Enchantments.FORTUNE);
            enchantment.ifPresent(enchantmentReference -> tool.enchant(enchantmentReference, fortuneLevel));
        }else if(this.silkTouch){
            Optional<Holder.Reference<Enchantment>> enchantment = enchantmentLookup.get(Enchantments.SILK_TOUCH);
            enchantment.ifPresent(enchantmentReference -> tool.enchant(enchantmentReference, 1));
        }

        return tool;
    }

    protected boolean isInBlackListedDimension(){
        List<? extends String> blacklist = Config.QUARRY_BLOCKED_DIMENSIONS.get();
        if(blacklist.isEmpty()) return false;

        assert level != null;

        HolderLookup.Provider provider = level.registryAccess();
        HolderLookup.RegistryLookup<Level> dimensionLookup = provider.lookupOrThrow(Registries.DIMENSION);

        for(String dimension : blacklist){
            ResourceLocation loc = ResourceLocation.parse(dimension);
            ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, loc);
            if(dimensionLookup.get(dimensionKey).isPresent()){
                if(level.dimension().equals(dimensionKey)) return true;
            }else{
                throw new IllegalArgumentException("Unknown dimension in config: " + dimension);
            }
        }

        return false;
    }

    protected boolean canMine(BlockPos pos){
        Level level = this.level;
        if(level == null || pos == null) return false;

        if(level.getBlockState(pos).getBlock() == Blocks.AIR) return false;
        if(level.getBlockState(pos).getBlock() == Blocks.BEDROCK) return false;
        if(level.getBlockEntity(pos) != null) return false;

        return true;
    }

    protected void updatePos(){
        Direction nextDirection = getNextDirection();
        if(nextDirection == null) {
            setStatus(StatusData.Status.FINISHED);
            return;
        }

        if(nextDirection == Direction.DOWN) setLayerFirstPosData(null, null);
        else setLayerFirstPosData(nextDirection, currentPos);

        lastPos = currentPos;
        currentPos = currentPos.relative(nextDirection);
    }

    protected BlockPos resetPosToStart(){
        AABB area = getAbsoluteWorkingArea();

        Point2D closestCorner;

        closestCorner = getCloserToBE(area.maxX, area.maxZ, area.minX, area.maxZ);
        closestCorner = getCloserToBE(closestCorner.getX(), closestCorner.getY(), area.maxX, area.minZ);
        closestCorner = getCloserToBE(closestCorner.getX(), closestCorner.getY(), area.minX, area.minZ);

        return new BlockPos((int) closestCorner.getX(), (int) area.maxY, (int) closestCorner.getY());
    }

    protected Direction getNextDirection(){
        AABB area = getAbsoluteWorkingArea();

        if(checkDirection(area, facing.getClockWise())){return facing.getClockWise();}
        if(checkDirection(area, facing.getCounterClockWise())) return facing.getCounterClockWise();
        if(checkDirection(area, facing.getOpposite())) return facing.getOpposite();
        if(checkDirection(area, facing)) return facing;
        if(checkDirection(area, Direction.DOWN)) return Direction.DOWN;

        return null;
    }

    protected void setLayerFirstPosData(Direction firstDirection, BlockPos pos){
        if(layerFirstPos == null && layerFirstDirection == null || firstDirection == null && pos == null) {
            this.layerFirstDirection = firstDirection;
            this.layerFirstPos = pos;
        }
    }

    protected boolean checkDirection(AABB area, Direction direction){
        BlockPos newPos = currentPos.relative(direction, 1);

        if(lastPos != null) {
            if(newPos.equals(lastPos)) return false;
        }

        if(!GeometryUtil.AABBContainsWithMax(area, newPos)) return false;

        if(this.layerFirstDirection != null && this.layerFirstPos != null) {
            if (direction != this.layerFirstDirection && direction != this.layerFirstDirection.getOpposite()) {
                if(direction == Direction.EAST || direction == Direction.WEST) {
                    return MathUtil.isFirstCloserToPoint(currentPos.getX(), newPos.getX(), this.layerFirstPos.getX());
                }else if(direction == Direction.SOUTH || direction == Direction.NORTH) {
                    return MathUtil.isFirstCloserToPoint(currentPos.getZ(), newPos.getZ(), this.layerFirstPos.getZ());
                }
            }
        }

        return true;
    }

    protected Point2D.Double getCloserToBE(double x1, double z1, double x2, double z2){
        return MathUtil.getCloserToPos(x1, z1, x2, z2, getBlockPos());
    }

    protected void setEnergyNeeded(){
        getEnergyStorage().setEnergyPerOp(calculateEnergyNeededPerBlock());
        if(getEnergyStorage().getEnergyPerOpLeft() <= 0) {
            getEnergyStorage().setEnergyPerOpLeft(getEnergyStorage().getEnergyPerOp());
        }
    }

    public void resetMachineSettings(){
        initArea();
        currentPos = resetPosToStart();
        lastPos = null;
        layerFirstPos = null;
        layerFirstDirection = null;
        getEnergyStorage().setEnergyPerOpLeft(0);
    }

    @Override
    public void doInfusionCalculations() {
        getEnergyStorage().setUsagePerTick(calculateEnergyUsagePerTick());
        getEnergyStorage().setCapacity(calculateEnergyCapacity());

        setMaxArea(calculateMaxRange(), calculateMaxRange());

        this.fortuneLevel = calculateFortuneLevel();
        this.silkTouch = calculateSilkTouch();

        this.tool = applyEnchantmentsToFakeTool(new ItemStack(Items.NETHERITE_PICKAXE));

        setEnergyNeeded();

        syncData(BEPacketSyncEnum.ENERGY);
        needInfusionUpdate = false;
    }

    public CombinedItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public int getSlotCount() {
        return SLOT_COUNT;
    }

    @Override
    public boolean canInsertItems() {
        return true;
    }

    @Override
    public boolean canExtractItems() {
        return true;
    }

    //INFUSION UPGRADE CALCULATIONS
    private int calculateEnergyUsagePerTick(){
        Infusion speedInfusion = getInfusionStorage().getInfusion(Registration.SPEED.get());
        Infusion efficiencyInfusion = getInfusionStorage().getInfusion(Registration.EFFICIENCY.get());

        int energyUsagePerTick = getBaseEnergyUsagePerTick();

        int speedPoints = speedInfusion.getTierPoints();
        int speedTier = speedInfusion.getTier();

        int efficiencyPoints = efficiencyInfusion.getTierPoints();
        int efficiencyTier = efficiencyInfusion.getTier();

        int pointsCalculated = 0;
        for (int i = 0; i < speedTier; i++) {
            int pointsToTier = speedInfusion.getPointsToTier(i);
            int pointsToCalculate = pointsToTier - pointsCalculated;

            energyUsagePerTick += (int) (energyUsagePerTick * pointsToCalculate * (InfusionCalculationConfig.SPEED_PER_POINT_PERCENT / 100));
            energyUsagePerTick += (int) (energyUsagePerTick * (InfusionCalculationConfig.SPEED_PER_TIER_PERCENT / 100));

            pointsCalculated += pointsToCalculate;
        }

        if(speedPoints > pointsCalculated) {
            energyUsagePerTick += (int) (energyUsagePerTick * (speedPoints - pointsCalculated) * (InfusionCalculationConfig.SPEED_PER_POINT_PERCENT / 100));
        }

        pointsCalculated = 0;
        for (int i = 0; i < efficiencyTier; i++) {
            int pointsToTier = efficiencyInfusion.getPointsToTier(i);
            int pointsToCalculate = pointsToTier - pointsCalculated;

            energyUsagePerTick -= (int) (energyUsagePerTick * pointsToCalculate * (InfusionCalculationConfig.EFFICIENCY_PER_POINT_SPEED_REDUCTION_PERCENT / 100));
            energyUsagePerTick -= (int) (energyUsagePerTick * (InfusionCalculationConfig.EFFICIENCY_PER_TIER_SPEED_REDUCTION_PERCENT / 100));
        }

        if(efficiencyPoints > pointsCalculated) {
            energyUsagePerTick -= (int) (energyUsagePerTick * (efficiencyPoints - pointsCalculated) * (InfusionCalculationConfig.EFFICIENCY_PER_POINT_SPEED_REDUCTION_PERCENT / 100));
        }

        return energyUsagePerTick;
    }

    private int calculateEnergyCapacity(){
        Infusion energyCapInfusion = getInfusionStorage().getInfusion(Registration.ENERGY_CAPACITY.get());
        int energyCapacity = getBaseEnergyCapacity();

        int energyCapPoints = energyCapInfusion.getTierPoints();
        int energyCapTier = energyCapInfusion.getTier();

        int pointsCalculated = 0;
        for (int i = 0; i < energyCapTier; i++) {
            int pointsToTier = energyCapInfusion.getPointsToTier(i);
            int pointsToCalculate = pointsToTier - pointsCalculated;

            energyCapacity += (int) (energyCapacity * pointsToCalculate * (InfusionCalculationConfig.ENERGY_CAP_PER_POINT_PERCENT / 100)); // 1% capacity increase per point;
            energyCapacity += (int)(energyCapacity * (InfusionCalculationConfig.ENERGY_CAP_PER_TIER_PERCENT / 100)); // 50% capacity increase per tier;

            pointsCalculated += pointsToCalculate;
        }

        if(energyCapPoints > pointsCalculated) {
            energyCapacity += (int) (energyCapacity * (energyCapPoints - pointsCalculated) * (InfusionCalculationConfig.ENERGY_CAP_PER_POINT_PERCENT / 100));//1% usage increase per points remaining;
        }

        return energyCapacity;
    }

    private int calculateMaxRange(){
        Infusion rangeInfusion = getInfusionStorage().getInfusion(Registration.RANGE.get());
        int range = getBaseRange();

        int rangePoints = rangeInfusion.getTierPoints();
        int rangeTier = rangeInfusion.getTier();

        range += Math.ceilDiv(rangePoints, InfusionCalculationConfig.RANGE_POINTS_FOR_ONE);
        range += rangeTier * InfusionCalculationConfig.RANGE_PER_TIER;

        return range;
    }

    private int calculateFortuneLevel(){
        Infusion fortuneInfusion = getInfusionStorage().getInfusion(Registration.FORTUNE.get());

        return fortuneInfusion.getTier();
    }

    private boolean calculateSilkTouch(){
        Infusion silkTouchInfusion = getInfusionStorage().getInfusion(Registration.SILK_TOUCH.get());

        int silkTouchTier = silkTouchInfusion.getTier();

        return silkTouchTier == silkTouchInfusion.getMaxTier();
    }

    private int calculateEnergyNeededPerBlock(){
        int energyNeeded = getBaseEnergyNeeded();
        if(fortuneLevel > 0){
            for(int tier = 1; tier <= fortuneLevel; tier++){
                energyNeeded += (int)Math.ceil(energyNeeded * (InfusionCalculationConfig.ENERGY_NEEDED_PER_FORTUNE_TIER_INCREASE_PERCENT / 100));
            }
        }

        if(silkTouch){
            energyNeeded += (int)Math.ceil(energyNeeded * (InfusionCalculationConfig.ENERGY_NEEDED_SILK_TOUCH_INCREASE_PERCENT / 100));
        }

        Infusion efficiencyInfusion = getInfusionStorage().getInfusion(Registration.EFFICIENCY.get());
        int efficiencyPoints = efficiencyInfusion.getTierPoints();
        int efficiencyTier = efficiencyInfusion.getTier();

        int pointsCalculated = 0;
        for (int i = 0; i < efficiencyTier; i++) {
            int pointsToTier = efficiencyInfusion.getPointsToTier(i);
            int pointsToCalculate = pointsToTier - pointsCalculated;

            energyNeeded -= (int) (energyNeeded * pointsToCalculate * (InfusionCalculationConfig.EFFICIENCY_PER_POINT_PERCENT / 100)); //0.1% energy needed decrease per point;
            energyNeeded -= (int) (energyNeeded * (InfusionCalculationConfig.EFFICIENCY_PER_TIER_PERCENT / 100)); // 10% energy needed decrease per tier;
        }

        if(efficiencyPoints > pointsCalculated) {
            energyNeeded -= (int) (energyNeeded * (efficiencyPoints - pointsCalculated) * (InfusionCalculationConfig.EFFICIENCY_PER_POINT_PERCENT / 100)); //0.1% energy needed decrease per point remaining;
        }

        return energyNeeded;
    }

    @Override
    public AdaptedEnergyStorage getEnergyStorage() {
        return getData(Registration.ENERGY_STORAGE);
    }

    @Override
    public int getBaseEnergyCapacity() {
        return Config.QUARRY_BASE_ENERGY.get();
    }

    @Override
    public int getBaseEnergyUsagePerTick() {
        return Config.QUARRY_BASE_ENERGY_PER_TICK.get();
    }

    public int getBaseRange(){
        return Config.QUARRY_BASE_RANGE.get();
    }

    public int getBaseEnergyNeeded(){
        return Config.QUARRY_BASE_ENERGY_NEEDED_PER_BLOCK.get();
    }

    public StatusData.Status getStatus(){
        return status;
    }

    public void updateLit(){
        if(level != null) {
            BlockState newState = this.getBlockState();
            if (status == StatusData.Status.ACTIVE) {
                newState = newState.setValue(QuarryBlock.LIT, true);
            } else {
                newState = newState.setValue(QuarryBlock.LIT, false);
            }
            level.setBlock(getBlockPos(), newState, 3);
        }
    }

    public void setStatus(StatusData.Status status){
        if(status != getStatus()) {
            if(getStatus() == StatusData.Status.FINISHED) resetMachineSettings();
            this.status = status;
            updateLit();
            markDirty();
        }
    }

    public StatusData getStatusData(){
        return statusData;
    }

    @Override
    public boolean addButton() {
        return true;
    }

    @Override
    public RedstoneData.RedstoneStatus getRedstoneStatus() {
        return redstoneStatus;
    }

    @Override
    public void setRedstoneStatus(RedstoneData.RedstoneStatus redstoneStatus) {
        if(redstoneStatus != getRedstoneStatus()) {
            this.redstoneStatus = redstoneStatus;
            markDirty();
        }
    }

    @Override
    public RedstoneData getRedstoneData() {
        return redstoneData;
    }

    @Override
    public InfusionStorage getInfusionStorage() {
        return getData(Registration.INFUSION_STORAGE);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        if(currentPos != null)
            tag.put("currentPos", NbtUtils.writeBlockPos(currentPos));

        if(lastPos != null)
            tag.put("lastPos", NbtUtils.writeBlockPos(lastPos));

        if(layerFirstPos != null)
            tag.put("layerFirstPos", NbtUtils.writeBlockPos(layerFirstPos));

        if(layerFirstDirection != null)
            tag.putInt("layerFirstDirection", layerFirstDirection.ordinal());

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {

        if(tag.contains("currentPos")) {
            this.currentPos = NbtUtils.readBlockPos(tag, "currentPos").orElse(null);
        }

        if(tag.contains("lastPos")) {
            this.lastPos = NbtUtils.readBlockPos(tag, "lastPos").orElse(null);
        }

        if(tag.contains("layerFirstPos")) {
            this.layerFirstPos = NbtUtils.readBlockPos(tag, "layerFirstPos").orElse(null);
        }

        if(tag.contains("layerFirstDirection")) {
            this.layerFirstDirection = Direction.values()[tag.getInt("layerFirstDirection")];
        }

        super.loadAdditional(tag, registries);
    }
}
