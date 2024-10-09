package com.chamoisest.miningmadness.common.capabilities.infusion.infusions;

import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.setup.Config;
import com.chamoisest.miningmadness.setup.MiningMadnessTags;
import com.chamoisest.miningmadness.setup.Registration;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class SilkTouchInfusion extends Infusion {

    public SilkTouchInfusion() {
        super(Registration.SILK_TOUCH);
    }

    @Override
    public Infusion newInstance() {
        return new SilkTouchInfusion();
    }

    @Override
    public TagKey<Block> canInfuseBlockTag() {
        return MiningMadnessTags.Blocks.CAN_INFUSE_SILK_TOUCH;
    }

    @Override
    public TagKey<Item> canInfuseItemTag() {
        return MiningMadnessTags.Items.CAN_INFUSE_SILK_TOUCH;
    }

    @Override
    public int getMaxTier() {
        return 1;
    }

    @Override
    public int getColor() {
        return Config.SILK_TOUCH_COLOR.get();
    }
}
