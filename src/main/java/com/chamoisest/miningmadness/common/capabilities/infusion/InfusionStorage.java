package com.chamoisest.miningmadness.common.capabilities.infusion;

import com.chamoisest.miningmadness.common.blockentities.interfaces.InfusionHandlerBE;
import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.common.items.GemOfFocusItem;
import com.chamoisest.miningmadness.common.network.data.InfusionSyncToClientPayload;
import com.chamoisest.miningmadness.setup.MiningMadnessDataComponents;
import com.chamoisest.miningmadness.setup.MiningMadnessRegistries;
import com.chamoisest.miningmadness.setup.Registration;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MutableClassToInstanceMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class InfusionStorage implements IInfusionStorage, INBTSerializable<CompoundTag> {

    public InfusionHandlerBE be;
    public ItemStack infusionStack;

    protected ClassToInstanceMap<Infusion> containedInfusions = MutableClassToInstanceMap.create();

    public InfusionStorage(ItemStack infusionStack) {
        this.infusionStack = infusionStack;

        if(infusionStack.has(MiningMadnessDataComponents.INFUSION)){
            containedInfusions = prepareInfusionFromDataAttachment();
        }else{
            init();
        }
    }

    public InfusionStorage(InfusionHandlerBE be) {
        this.be = be;
        init();
    }

    private void init(){
        if(be != null){
            initializeInfusions();
        }else if(infusionStack != null){
            initializeItemInfusions();
            syncItemStackDataComponents();
        }
    }

    protected void initializeInfusions() {
        BlockEntity blockEntity = (BlockEntity) this.be;
        BlockState thisEntityBlock = blockEntity.getBlockState();

        for(DeferredHolder<Infusion, ? extends Infusion> infusion: Registration.INFUSIONS.getEntries()){
            Optional<TagKey<Block>> infusionTag = infusion.get().getCanInfuseBlockTag();
            if(infusionTag.isEmpty()) continue;

            boolean isInTag = thisEntityBlock.is(infusionTag.get());

            if(isInTag){
                initializeInfusion(infusion);
            }
        }
    }

    protected void initializeItemInfusions() {
        for(DeferredHolder<Infusion, ? extends Infusion> infusion: Registration.INFUSIONS.getEntries()){

            if (infusionStack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();

                Optional<TagKey<Block>> infusionTag = infusion.get().getCanInfuseBlockTag();
                if(infusionTag.isEmpty()) continue;

                boolean isInTag = block.getStateDefinition().any().is(infusionTag.get());

                if(isInTag){
                    initializeInfusion(infusion);
                }

            }else{
                Optional<TagKey<Item>> infusionTag = infusion.get().getCanInfuseItemTag();
                if(infusionTag.isEmpty()) continue;

                boolean isInTag = infusionStack.is(infusionTag.get());

                if(isInTag){
                    initializeInfusion(infusion);
                }
            }
        }
    }

    protected void initializeInfusion(DeferredHolder<Infusion, ? extends Infusion> infusion){
        Infusion infusionInstance = infusion.get().newInstance();
        containedInfusions.put(infusion.get().getClass(), infusionInstance);
    }

    public <T extends Infusion> Infusion getInfusion(T infusion) {
        if(containedInfusions.containsKey(infusion.getClass())){
            return containedInfusions.get(infusion.getClass());
        }

        throw new IllegalArgumentException("InfusionStorage does not contain infusion of type ´" + infusion + "´!");
    }

    public <T extends Infusion> boolean hasInfusion(T infusion) {return containedInfusions.containsKey(infusion.getClass());}

    @Override
    public <T extends Infusion> void setInfusionData(T infusion, int tier, int tierPoints) {
        Infusion infusionInstance = getInfusion(infusion);

        infusionInstance.setTier(tier);
        infusionInstance.setTierPoints(tierPoints);

        setChanged(infusion);
    }

    public void setContainedInfusions(ClassToInstanceMap<Infusion> containedInfusions) {
        this.containedInfusions = containedInfusions;
        setChanged();
    }

    public ClassToInstanceMap<Infusion> getContainedInfusions() {return containedInfusions;}

    public SortedMap<String, Infusion> getSortedInfusions(){
        SortedMap<String, Infusion> sortedInfusions = new TreeMap<>();

        for(Infusion infusion : containedInfusions.values()){
            sortedInfusions.put(infusion.getName(), infusion);
        }

        return sortedInfusions;
    }

    public int getInfusionCount() {return containedInfusions.size();}

    protected <T extends Infusion> int getInfusionId(T infusion){
        return MiningMadnessRegistries.INFUSIONS.getId(infusion.getId());
    }

    protected Infusion getInfusionById(int infusionId) {
        return MiningMadnessRegistries.INFUSIONS.byId(infusionId);
    }

    public void setChanged(){
        for(Infusion infusion : containedInfusions.values()){
            sendInfusionSyncPacket(infusion);
        }
        syncItemStackDataComponents();
    }

    public <T extends Infusion> void setChanged(T infusion){
        sendInfusionSyncPacket(infusion);
        syncItemStackDataComponents();
    }

    protected <T extends @NonNull Infusion> void sendInfusionSyncPacket(T infusion) {
        if(this.be != null){
            BlockEntity blockEntity = (BlockEntity) this.be;
            Level level = blockEntity.getLevel();

            if(level != null && !level.isClientSide()){
                ServerLevel serverLevel = (ServerLevel) level;
                int infusionId = getInfusionId(infusion);
                Infusion infusionInstance = getInfusion(infusion);

                PacketDistributor.sendToPlayersTrackingChunk(
                        serverLevel,
                        level.getChunk(blockEntity.getBlockPos()).getPos(),
                        new InfusionSyncToClientPayload(blockEntity.getBlockPos(), infusionId, infusionInstance.getTier(), infusionInstance.getTierPoints())
                );
            }
        }
    }

    protected void syncItemStackDataComponents(){
        if(infusionStack != null && !(infusionStack.getItem() instanceof GemOfFocusItem)){
            infusionStack.set(MiningMadnessDataComponents.INFUSION, prepareInfusionToDataAttachment());
        }
    }

    protected Map<String, Map<String, Integer>> prepareInfusionToDataAttachment(){
        Map<String, Map<String, Integer>> stackInfusionData = new HashMap<>();
        ClassToInstanceMap<Infusion> stackInfusionMap = containedInfusions;

        if(stackInfusionMap != null){
            for(Infusion infusion : stackInfusionMap.values()){

                Map<String, Integer> innerData = ImmutableMap.of(
                        Infusion.TIER_KEY, infusion.getTier(),
                        Infusion.TIER_POINTS_KEY, infusion.getTierPoints()
                );

                stackInfusionData.put(getInfusionId(infusion) + "", innerData);
            }
        }

        return stackInfusionData;
    }

    protected ClassToInstanceMap<Infusion> prepareInfusionFromDataAttachment(){

        ClassToInstanceMap<Infusion> infusionMap = MutableClassToInstanceMap.create();

        if(infusionStack.has(MiningMadnessDataComponents.INFUSION)){
            Map<String, Map<String, Integer>> stackInfusionData = infusionStack.get(MiningMadnessDataComponents.INFUSION);

            for(Map.Entry<String, Map<String, Integer>> entry: stackInfusionData.entrySet()){
                int infusionId = Integer.parseInt(entry.getKey());
                Infusion infusion = getInfusionById(infusionId).newInstance();

                if(infusion != null) {
                    Map<String, Integer> data = entry.getValue();

                    infusion.setTier(data.get(Infusion.TIER_KEY));
                    infusion.setTierPoints(data.get(Infusion.TIER_POINTS_KEY));

                    infusionMap.put(infusion.getClass(), infusion);
                }else{
                    throw new IllegalArgumentException("Invalid infusion ID in Data Attachment!");
                }
            }
        }
        return infusionMap;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        for(Map.Entry<Class<? extends Infusion>, Infusion> entry: containedInfusions.entrySet()){
            String name = entry.getValue().getName();

            tag.putInt(name + "_" + Infusion.TIER_KEY, entry.getValue().getTier());
            tag.putInt(name + "_" + Infusion.TIER_POINTS_KEY, entry.getValue().getTierPoints());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        for(Map.Entry<Class<? extends Infusion>, Infusion> entry: containedInfusions.entrySet()){
            String name = entry.getValue().getName();
            Infusion infusion = entry.getValue().newInstance();

            if(nbt.contains(name + "_" + Infusion.TIER_KEY)){
                int tier = nbt.getInt(name + "_" + Infusion.TIER_KEY);
                infusion.setTier(tier);
            }

            if(nbt.contains(name + "_" + Infusion.TIER_POINTS_KEY)){
                int tierPoints = nbt.getInt(name + "_" + Infusion.TIER_POINTS_KEY);
                infusion.setTierPoints(tierPoints);
            }

            containedInfusions.put(entry.getKey(), infusion);
        }
    }
}
