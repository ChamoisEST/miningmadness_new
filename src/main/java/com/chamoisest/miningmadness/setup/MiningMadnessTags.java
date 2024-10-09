package com.chamoisest.miningmadness.setup;

import com.chamoisest.miningmadness.MiningMadness;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class MiningMadnessTags {
    public static class Blocks {

        public static final TagKey<Block> CAN_INFUSE_EFFICIENCY = createTag("infusion/types/can_infuse_efficiency");
        public static final TagKey<Block> CAN_INFUSE_ENERGY_CAPACITY = createTag("infusion/types/can_infuse_energy_capacity");
        public static final TagKey<Block> CAN_INFUSE_FORTUNE = createTag("infusion/types/can_infuse_fortune");
        public static final TagKey<Block> CAN_INFUSE_RANGE = createTag("infusion/types/can_infuse_range");
        public static final TagKey<Block> CAN_INFUSE_SILK_TOUCH = createTag("infusion/types/can_infuse_silk_touch");
        public static final TagKey<Block> CAN_INFUSE_SPEED = createTag("infusion/types/can_infuse_speed");

        private static TagKey<Block> createTag(String name){
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> INFUSABLE = createTag("infusion/infusable");

        public static final TagKey<Item> CAN_INFUSE_EFFICIENCY = createTag("infusion/types/can_infuse_efficiency");
        public static final TagKey<Item> CAN_INFUSE_ENERGY_CAPACITY = createTag("infusion/types/can_infuse_energy_capacity");
        public static final TagKey<Item> CAN_INFUSE_FORTUNE = createTag("infusion/types/can_infuse_fortune");
        public static final TagKey<Item> CAN_INFUSE_RANGE = createTag("infusion/types/can_infuse_range");
        public static final TagKey<Item> CAN_INFUSE_SILK_TOUCH = createTag("infusion/types/can_infuse_silk_touch");
        public static final TagKey<Item> CAN_INFUSE_SPEED = createTag("infusion/types/can_infuse_speed");

        private static TagKey<Item> createTag(String name){
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, name));
        }
    }
}
