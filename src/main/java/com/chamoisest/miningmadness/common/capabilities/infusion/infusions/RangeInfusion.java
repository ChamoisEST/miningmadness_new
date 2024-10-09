package com.chamoisest.miningmadness.common.capabilities.infusion.infusions;

import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.setup.Config;
import com.chamoisest.miningmadness.setup.MiningMadnessTags;
import com.chamoisest.miningmadness.setup.Registration;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class RangeInfusion extends Infusion {

    public RangeInfusion() {
        super(Registration.RANGE);
    }

    @Override
    public Infusion newInstance() {
        return new RangeInfusion();
    }

    @Override
    public TagKey<Block> canInfuseBlockTag() {
        return MiningMadnessTags.Blocks.CAN_INFUSE_RANGE;
    }

    @Override
    public TagKey<Item> canInfuseItemTag() {
        return MiningMadnessTags.Items.CAN_INFUSE_RANGE;
    }

    @Override
    public int getMaxTier() {
        return 5;
    }

    @Override
    public int getColor() {
        return Config.RANGE_COLOR.get();
    }
}
