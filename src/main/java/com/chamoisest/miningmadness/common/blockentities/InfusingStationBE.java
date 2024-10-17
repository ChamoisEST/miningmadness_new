package com.chamoisest.miningmadness.common.blockentities;

import com.chamoisest.miningmadness.common.blockentities.base.enums.BEPacketSyncEnum;
import com.chamoisest.miningmadness.common.blockentities.base.BaseBE;
import com.chamoisest.miningmadness.common.blockentities.data.RedstoneData;
import com.chamoisest.miningmadness.common.blockentities.data.StatusData;
import com.chamoisest.miningmadness.common.blockentities.interfaces.*;
import com.chamoisest.miningmadness.common.capabilities.AdaptedEnergyStorage;
import com.chamoisest.miningmadness.common.capabilities.CombinedItemHandler;
import com.chamoisest.miningmadness.common.capabilities.infusion.IInfusionStorage;
import com.chamoisest.miningmadness.common.capabilities.infusion.InfusionStorage;
import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.common.items.GemOfFocusItem;
import com.chamoisest.miningmadness.common.recipes.infusing.InfusingRecipe;
import com.chamoisest.miningmadness.common.recipes.infusing.InfusingRecipeInput;
import com.chamoisest.miningmadness.setup.Config;
import com.chamoisest.miningmadness.setup.MiningMadnessCapabilities;
import com.chamoisest.miningmadness.setup.MiningMadnessRegistries;
import com.chamoisest.miningmadness.setup.Registration;
import com.chamoisest.miningmadness.util.InfusionCalculationConfig;
import com.google.common.collect.ClassToInstanceMap;
import com.ibm.icu.impl.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.*;

public class InfusingStationBE extends BaseBE implements EnergyHandlerBE, RedstoneControlledBE, ItemHandlerBE, InfusionHandlerBE, StatusBE {

    public static final int SLOT_COUNT = 6;

    public StatusData.Status status;
    public RedstoneData.RedstoneStatus redstoneStatus;

    public final RedstoneData redstoneData;
    public final StatusData statusData;

    protected Optional<RecipeHolder<InfusingRecipe>> lastTickRecipeHolder = Optional.empty();
    protected Optional<RecipeHolder<InfusingRecipe>> recipeHolder = Optional.empty();
    protected NonNullList<Ingredient> currentRecipeItems;
    protected ItemStack recipeResult = ItemStack.EMPTY;

    public boolean isInfusing = false;
    public int useItemsInSlotsFlags = 0;

    public Map<ResourceLocation, Integer> recipeOutputInfusion = new HashMap<>();

    protected List<Pair<ResourceLocation, ResourceLocation>> conflictingInfusions = new ArrayList<>();

    protected ItemStackHandler topItemHandler = createItemHandler(1);

    protected ItemStackHandler middleItemHandler = createItemHandler(4);

    protected ItemStackHandler bottomItemHandler = createItemHandler(1);

    protected CombinedItemHandler itemHandler = new CombinedItemHandler(topItemHandler, middleItemHandler, bottomItemHandler);



    public InfusingStationBE(BlockPos pos, BlockState blockState) {
        super(Registration.INFUSING_STATION_BE.get(), pos, blockState);

        statusData = new StatusData(this);
        redstoneData = new RedstoneData(this);

        this.status = StatusData.Status.ACTIVE;
        this.redstoneStatus = RedstoneData.RedstoneStatus.IGNORE;

        populateConflictingInfusions();
    }

    @Override
    public void forceHandleTicks() {
        if(!getRecipe()) clearRecipeData();

        if(infusionDataChanged()){
            this.syncData(BEPacketSyncEnum.INFUSION_CRAFTING);
            this.syncData(BEPacketSyncEnum.ENERGY);
        }

        this.lastTickRecipeHolder = this.recipeHolder;
    }

    @Override
    public void handleTicks() {
        if(hasRecipe()) {
            if (recipeOutputInfusion.isEmpty() || canGetNewInfusionFromRecipe()) {
                AdaptedEnergyStorage energyStorage = getEnergyStorage();
                if (hasEnoughEnergy(energyStorage.getEnergyPerOp()) || isInfusing) {
                    isInfusing = true;
                    completingWorkCycle = true;

                    energyStorage.extractUsagePerTick();
                    energyStorage.setEnergyPerOpLeft(energyStorage.getEnergyPerOpLeft() - energyStorage.getUsagePerTick());

                    if (energyStorage.getEnergyPerOpLeft() <= 0) {
                        infuseItem(false);
                        clearRecipeData();
                    } else {
                        infuseItem(true);
                    }
                } else {
                    setStatus(StatusData.Status.NOT_ENOUGH_FE);
                }
            }
        }
    }

    @Override
    public void doInfusionCalculations() {

        getEnergyStorage().setUsagePerTick(calculateEnergyUsagePerTick());
        getEnergyStorage().setCapacity(calculateEnergyCapacity());

        syncData(BEPacketSyncEnum.ENERGY);

        this.needInfusionUpdate = false;
    }

    private void infuseItem(boolean simulate){
        Set<Integer> extractedSlots = new HashSet<>();
        Set<Ingredient> removedIngredients = new HashSet<>();

        ItemStack infusionStack = this.recipeResult;
        IInfusionStorage stackInfusionStorage = new InfusionStorage(infusionStack);

        if(infusionStack.getItem() instanceof GemOfFocusItem){
            stackInfusionStorage = this.getInfusionStorage();
        }

        if(!simulate){
            if(stackInfusionStorage.getInfusionCount() > 0){
                for(Map.Entry<ResourceLocation, Integer> entry : this.recipeOutputInfusion.entrySet()){

                    Infusion infusion = MiningMadnessRegistries.INFUSIONS.get(entry.getKey());
                    Infusion infusionInstance = stackInfusionStorage.getInfusion(infusion);

                    if(stackInfusionStorage.hasInfusion(infusion) && !isConflictingInfusion(infusion, stackInfusionStorage.getContainedInfusions())){
                        infusionInstance.addTierPoints(entry.getValue());

                        if(infusionStack.getItem() instanceof GemOfFocusItem){
                            stackInfusionStorage.setChanged(infusionInstance);

                            this.needInfusionUpdate = true;
                        }else{
                            stackInfusionStorage.setChanged();
                        }
                    }
                }
            }
        }

        for(Ingredient ingredient : this.currentRecipeItems){
            if(removedIngredients.contains(ingredient)) continue;

            for(int slot = 0; slot < SLOT_COUNT - 1; slot++){
                if(removedIngredients.contains(ingredient)) continue;
                if(extractedSlots.contains(slot)) continue;

                if(ingredient.test(getItemHandler().getStackInSlot(slot))) {
                    removedIngredients.add(ingredient);
                    extractedSlots.add(slot);
                    if(simulate){
                        this.useItemsInSlotsFlags |= (int)Math.pow(2, slot + 1);
                    }else{
                        getItemHandler().extractItem(slot, 1, false);
                    }
                }
            }
        }

        if(!simulate){
            ItemStack currentResultStack = getItemHandler().getStackInSlot(5);
            if(currentResultStack.isEmpty()){
                getItemHandler().setStackInSlot(5, infusionStack);
            }else if(currentResultStack.getMaxStackSize() > currentResultStack.getCount()){
                currentResultStack.setCount(currentResultStack.getCount() + infusionStack.getCount());
                getItemHandler().setStackInSlot(5, currentResultStack);
            }

        }
    }

    protected boolean infusionDataChanged() {
        return this.lastTickRecipeHolder != this.recipeHolder;
    }

    private boolean canGetNewInfusionFromRecipe(){
        ItemStack stack = getItemHandler().getStackInSlot(0);

        IInfusionStorage infusionCap = stack.getCapability(MiningMadnessCapabilities.InfusionStorage.ITEM);

        if(stack.getItem() instanceof GemOfFocusItem){
            infusionCap = this.getInfusionStorage();
        }

        if(infusionCap != null){
            ClassToInstanceMap<Infusion> stackInfusionMap = infusionCap.getContainedInfusions();

            for(Map.Entry<ResourceLocation, Integer> recipeInfusion : recipeOutputInfusion.entrySet()){
                Infusion outputInfusion = MiningMadnessRegistries.INFUSIONS.get(recipeInfusion.getKey());
                if(!infusionCap.hasInfusion(outputInfusion)) continue;
                if(isConflictingInfusion(outputInfusion, stackInfusionMap)) continue;

                Infusion infusionInstance = infusionCap.getInfusion(outputInfusion);

                int maxTier = infusionInstance.getMaxTier();
                int storedInfusionPoints = infusionInstance.getTierPoints();
                int maxInfusionPoints = infusionInstance.getPointsToTier(maxTier);

                if(storedInfusionPoints < maxInfusionPoints){
                    setStatus(StatusData.Status.ACTIVE);
                    return true;
                }
            }
        }

        if(getStatus() != StatusData.Status.CONFLICTING_INFUSION) setStatus(StatusData.Status.CANT_GAIN_INFUSION);
        return false;
    }

    private boolean getRecipe(){
        if(level != null) {
            ItemStack infusingStack = getItemHandler().getStackInSlot(0);
            ItemStack stack1 = getItemHandler().getStackInSlot(1);
            ItemStack stack2 = getItemHandler().getStackInSlot(2);
            ItemStack stack3 = getItemHandler().getStackInSlot(3);
            ItemStack stack4 = getItemHandler().getStackInSlot(4);

            InfusingRecipeInput input = new InfusingRecipeInput(infusingStack, stack1, stack2, stack3, stack4);

            this.recipeHolder = level.getRecipeManager().getRecipeFor(Registration.INFUSING.get(), input, level);

            this.currentRecipeItems = this.recipeHolder
                    .map(RecipeHolder::value)
                    .map(InfusingRecipe::getIngredients)
                    .orElse(NonNullList.create());

            this.recipeResult = this.recipeHolder
                    .map(RecipeHolder::value)
                    .map(InfusingRecipe::getOutput)
                    .orElse(ItemStack.EMPTY);

            if(this.recipeResult == ItemStack.EMPTY){
                this.recipeResult = infusingStack.copy();
                this.recipeResult.setCount(1);
            }

            getEnergyStorage().setEnergyPerOp(calculateRFNeededForRecipe(this.recipeHolder
                    .map(RecipeHolder::value)
                    .map(InfusingRecipe::getEnergyUsed)
                    .orElse(0)));

            if(getEnergyStorage().getEnergyPerOpLeft() <= 0) getEnergyStorage().setEnergyPerOpLeft(getEnergyStorage().getEnergyPerOp());

            this.recipeOutputInfusion = this.recipeHolder
                    .map(RecipeHolder::value)
                    .map(InfusingRecipe::getOutputInfusion)
                    .orElse(new HashMap<>());


            if(this.recipeHolder.isPresent()){
                if(recipeResult.isEmpty()) return true;
                else return canOutputItem();
            }
        }
        return false;
    }
    private boolean hasRecipe(){
        return this.recipeHolder.isPresent();
    }

    private boolean canOutputItem(){
        ItemStack stackInOutputSlot = getItemHandler().getStackInSlot(5);
        if(stackInOutputSlot.isEmpty()) return true;
        else return stackInOutputSlot.is(recipeResult.getItem()) && stackInOutputSlot.getMaxStackSize() > stackInOutputSlot.getCount();
    }

    private void clearRecipeData(){
        this.recipeHolder = Optional.empty();
        this.recipeResult = ItemStack.EMPTY;
        this.useItemsInSlotsFlags = 0;
        this.isInfusing = false;
        this.completingWorkCycle = false;
        this.recipeOutputInfusion = new HashMap<>();

        getEnergyStorage().setEnergyPerOp(0);
        getEnergyStorage().setEnergyPerOpLeft(0);
    }

    private boolean isConflictingInfusion(Infusion recipeInfusion, ClassToInstanceMap<Infusion> stackInfusionMap){
        ResourceLocation recipeInfusionId = recipeInfusion.getId();

        for(Infusion infusion: stackInfusionMap.values()){
            ResourceLocation infusionId = infusion.getId();

            if(recipeInfusionId.equals(infusionId)){
                for(Pair<ResourceLocation, ResourceLocation> pair: conflictingInfusions){
                    ResourceLocation pairFirstId = pair.first;
                    ResourceLocation pairSecondId = pair.second;


                    if(!pairFirstId.equals(infusionId) && !pairSecondId.equals(infusionId)) continue;

                    if (pairFirstId.equals(infusionId)) {
                        if(!stackInfusionMap.containsKey(getInfusionClass(pairSecondId))) continue;
                        if(stackInfusionMap.get(getInfusionClass(pairSecondId)).getTierPoints() > 0){

                            setStatus(StatusData.Status.CONFLICTING_INFUSION);
                            return true;

                        }
                    }else{
                        if(!stackInfusionMap.containsKey(getInfusionClass(pairFirstId))) continue;
                        if(stackInfusionMap.get(getInfusionClass(pairFirstId)).getTierPoints() > 0){

                            setStatus(StatusData.Status.CONFLICTING_INFUSION);
                            return true;

                        }
                    }
                }
            }
        }

        setStatus(StatusData.Status.ACTIVE);
        return false;
    }

    private Class<? extends Infusion> getInfusionClass(ResourceLocation resourceId){
        Infusion infusion = MiningMadnessRegistries.INFUSIONS.get(resourceId);
        if(infusion != null){
            return infusion.getClass();
        }

        throw new IllegalArgumentException("Conflicting infusion contains invalid resourceLocation: " + resourceId);
    }

    private void populateConflictingInfusions(){
        conflictingInfusions.add(Pair.of(Registration.FORTUNE.getId(), Registration.SILK_TOUCH.getId()));
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

    private int calculateRFNeededForRecipe(int recipeEnergyNeeded){
        Infusion efficiencyInfusion = getInfusionStorage().getInfusion(Registration.EFFICIENCY.get());

        int efficiencyPoints = efficiencyInfusion.getTierPoints();
        int efficiencyTier = efficiencyInfusion.getTier();

        int pointsCalculated = 0;
        for (int i = 0; i < efficiencyTier; i++) {
            int pointsToTier = efficiencyInfusion.getPointsToTier(i);
            int pointsToCalculate = pointsToTier - pointsCalculated;

            recipeEnergyNeeded -= (int) (recipeEnergyNeeded * pointsToCalculate * (InfusionCalculationConfig.EFFICIENCY_PER_POINT_PERCENT / 100)); //0.1% energy needed decrease per point;
            recipeEnergyNeeded -= (int) (recipeEnergyNeeded * (InfusionCalculationConfig.EFFICIENCY_PER_TIER_PERCENT / 100)); // 10% energy needed decrease per tier;
        }

        if(efficiencyPoints > pointsCalculated) {
            recipeEnergyNeeded -= (int) (recipeEnergyNeeded * (efficiencyPoints - pointsCalculated) * (InfusionCalculationConfig.EFFICIENCY_PER_POINT_PERCENT / 100)); //0.1% energy needed decrease per point remaining;
        }

        return recipeEnergyNeeded;
    }

    //DATA STORAGE STUFF
    @Override
    public AdaptedEnergyStorage getEnergyStorage() {
        return getData(Registration.ENERGY_STORAGE);
    }

    @Override
    public int getBaseEnergyCapacity() {
        return Config.INFUSING_STATION_BASE_ENERGY.get();
    }

    @Override
    public int getBaseEnergyUsagePerTick() {
        return Config.INFUSING_STATION_BASE_ENERGY_PER_TICK.get();
    }

    public CombinedItemHandler getItemHandler() {
        return itemHandler;
    }

    public ItemStackHandler getTopItemHandler(){
        return topItemHandler;
    }
    public ItemStackHandler getBottomItemHandler(){
        return bottomItemHandler;
    }
    public ItemStackHandler getMiddleItemHandler(){
        return middleItemHandler;
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
        return false;
    }
}
