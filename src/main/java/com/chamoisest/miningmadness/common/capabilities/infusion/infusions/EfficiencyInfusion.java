package com.chamoisest.miningmadness.common.capabilities.infusion.infusions;

import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.setup.Config;
import com.chamoisest.miningmadness.setup.MiningMadnessTags;
import com.chamoisest.miningmadness.setup.Registration;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class EfficiencyInfusion extends Infusion {

    public EfficiencyInfusion() {
        super(Registration.EFFICIENCY);
    }

    @Override
    public Infusion newInstance() {
        return new EfficiencyInfusion();
    }

    @Override
    public TagKey<Block> canInfuseBlockTag() {
        return MiningMadnessTags.Blocks.CAN_INFUSE_EFFICIENCY;
    }

    @Override
    public TagKey<Item> canInfuseItemTag() {
        return MiningMadnessTags.Items.CAN_INFUSE_EFFICIENCY;
    }

    @Override
    public int getMaxTier() {
        return 3;
    }

    @Override
    public int getColor() {
        return Config.EFFICIENCY_COLOR.get();
    }
}
