package com.chamoisest.miningmadness.common.capabilities.infusion.infusions;

import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.setup.Config;
import com.chamoisest.miningmadness.setup.MiningMadnessTags;
import com.chamoisest.miningmadness.setup.Registration;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class EnergyCapacityInfusion extends Infusion {

    public EnergyCapacityInfusion() {
        super(Registration.ENERGY_CAPACITY);
    }

    @Override
    public Infusion newInstance() {
        return new EnergyCapacityInfusion();
    }

    @Override
    public TagKey<Block> canInfuseBlockTag() {
        return MiningMadnessTags.Blocks.CAN_INFUSE_ENERGY_CAPACITY;
    }

    @Override
    public TagKey<Item> canInfuseItemTag() {
        return MiningMadnessTags.Items.CAN_INFUSE_ENERGY_CAPACITY;
    }

    @Override
    public int getMaxTier() {
        return 3;
    }

    @Override
    public int getColor() {
        return Config.ENERGY_CAPACITY_COLOR.get();
    }
}
