package com.chamoisest.miningmadness.common.blockentities;

import com.chamoisest.miningmadness.common.blockentities.base.enums.BEPacketSyncEnum;
import com.chamoisest.miningmadness.common.blockentities.base.WorkingAreaBE;
import com.chamoisest.miningmadness.common.blockentities.data.RedstoneData;
import com.chamoisest.miningmadness.common.blockentities.data.StatusData;
import com.chamoisest.miningmadness.common.blockentities.interfaces.*;
import com.chamoisest.miningmadness.common.capabilities.AdaptedEnergyStorage;
import com.chamoisest.miningmadness.common.capabilities.CombinedItemHandler;
import com.chamoisest.miningmadness.common.capabilities.infusion.InfusionStorage;
import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.setup.Config;
import com.chamoisest.miningmadness.setup.Registration;
import com.chamoisest.miningmadness.util.InfusionCalculationConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class QuarryBE extends WorkingAreaBE implements EnergyHandlerBE, ItemHandlerBE, StatusBE, RedstoneControlledBE, InfusionHandlerBE {

    public static final int SLOT_COUNT = 12;

    public StatusData.Status status;
    public RedstoneData.RedstoneStatus redstoneStatus;

    public final StatusData statusData;
    public final RedstoneData redstoneData;

    protected CombinedItemHandler itemHandler = new CombinedItemHandler(createItemHandler(SLOT_COUNT));

    public QuarryBE(BlockPos pos, BlockState blockState) {
        super(Registration.QUARRY_BE.get(), pos, blockState);

        statusData = new StatusData(this);
        redstoneData = new RedstoneData(this);

        this.status = StatusData.Status.INACTIVE;
        this.redstoneStatus = RedstoneData.RedstoneStatus.IGNORE;

        setMaxArea(50, 50);
        initArea();
    }

    public void handleTicks(){

    }

    @Override
    public void doInfusionCalculations() {
        getEnergyStorage().setUsagePerTick(calculateEnergyUsagePerTick());
        getEnergyStorage().setCapacity(calculateEnergyCapacity());

        syncData(BEPacketSyncEnum.ENERGY);
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

    @Override
    public AdaptedEnergyStorage getEnergyStorage() {
        return getData(Registration.ENERGY_STORAGE);
    }

    @Override
    public int getBaseEnergyCapacity() {
        return Config.BASIC_QUARRY_BASE_ENERGY.get();
    }

    @Override
    public int getBaseEnergyUsagePerTick() {
        return Config.BASIC_QUARRY_BASE_ENERGY_PER_TICK.get();
    }

    public StatusData.Status getStatus(){
        return status;
    }

    public void setStatus(StatusData.Status status){
        if(status != getStatus()) {
            this.status = status;
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
}
