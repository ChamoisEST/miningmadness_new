package com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base;

import com.chamoisest.miningmadness.setup.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;

public abstract class Infusion {
    public static final String TIER_KEY = "tier";
    public static final String TIER_POINTS_KEY = "tier_points";

    protected int tier;
    protected int tierPoints;

    protected DeferredHolder<Infusion, Infusion> infusionHolder;
    protected String name;
    protected ResourceLocation id;

    protected Optional<TagKey<Block>> canInfuseBlockTag;
    protected Optional<TagKey<Item>> canInfuseItemTag;

    public Infusion(DeferredHolder<Infusion, Infusion> infusion) {
        infusionHolder = infusion;
        init();
    }

    public void init(){
        tier = 0;
        tierPoints = 0;
        canInfuseBlockTag = Optional.ofNullable(canInfuseBlockTag());
        canInfuseItemTag = Optional.ofNullable(canInfuseItemTag());
        id = infusionHolder.getId();

        setName(infusionHolder.getRegisteredName());
    }

    public abstract Infusion newInstance();

    protected TagKey<Block> canInfuseBlockTag(){return null;}
    protected TagKey<Item> canInfuseItemTag(){return null;}

    public Optional<TagKey<Block>> getCanInfuseBlockTag(){return canInfuseBlockTag;}
    public Optional<TagKey<Item>> getCanInfuseItemTag(){return canInfuseItemTag;}

    public int getTier(){return tier;}
    public int getTierPoints(){return tierPoints;}
    public abstract int getMaxTier();
    public int getPointsToTier(int tier){
        if(getMaxTier() == tier) tier -= 1;

        return switch(tier) {
            case 0 -> Config.POINTS_TO_TIER1.get();
            case 1 -> Config.POINTS_TO_TIER2.get();
            case 2 -> Config.POINTS_TO_TIER3.get();
            case 3 -> Config.POINTS_TO_TIER4.get();
            case 4 -> Config.POINTS_TO_TIER5.get();
            case 5 -> Config.POINTS_TO_TIER6.get();
            case 6 -> Config.POINTS_TO_TIER7.get();
            case 7 -> Config.POINTS_TO_TIER8.get();
            case 8 -> Config.POINTS_TO_TIER9.get();
            case 9 -> -1;
            default -> throw new IllegalArgumentException("Tier must be between 0 and 9");
        };
    }
    public int getPointsToTierByPoints(int tierPoints){
        int tierIterator = 0;
        while(tierPoints >= getPointsToTier(tierIterator)){
            if(isMaxTier()) break;
            tierIterator++;
        }

        return getPointsToTier(tierIterator);
    }
    public String getName(){
        if(name != null) return name;

        throw new IllegalStateException("Infusion name queried before initialization");
    }
    public ResourceLocation getId(){return id;}

    protected void addTier(){
        setTier(getTier() + 1);
    }
    public void addTierPoints(int amount){
        int totalAfterAddition = tierPoints + amount;

        int tierIterator = tier;
        while(totalAfterAddition >= getPointsToTier(tierIterator)){
            if(isMaxTier()) break;
            addTier();
            tierIterator++;
        }

        if(totalAfterAddition >= getPointsToTier(tier)){
            setTierPoints(getPointsToTier(tier));
        }else{
            setTierPoints(totalAfterAddition);
        }
    }

    public boolean isMaxTier(){return getTier() == getMaxTier();}
    public boolean isNextMaxTier(){return getTier() + 1 == getMaxTier();}

    public void setTier(int tier){this.tier = tier;}
    public void setTierPoints(int tierPoints){this.tierPoints = tierPoints;}
    protected void setName(String name){
        String nameSub = name.substring(name.indexOf(":") + 1);
        this.name = nameSub.trim();
    }

    public abstract int getColor();
}
