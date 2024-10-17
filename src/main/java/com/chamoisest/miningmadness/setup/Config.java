package com.chamoisest.miningmadness.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class Config
{
    public static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();

    public static final String INFUSION = "infusion";
    public static ModConfigSpec.IntValue POINTS_TO_TIER1;
    public static ModConfigSpec.IntValue POINTS_TO_TIER2;
    public static ModConfigSpec.IntValue POINTS_TO_TIER3;
    public static ModConfigSpec.IntValue POINTS_TO_TIER4;
    public static ModConfigSpec.IntValue POINTS_TO_TIER5;
    public static ModConfigSpec.IntValue POINTS_TO_TIER6;
    public static ModConfigSpec.IntValue POINTS_TO_TIER7;
    public static ModConfigSpec.IntValue POINTS_TO_TIER8;
    public static ModConfigSpec.IntValue POINTS_TO_TIER9;

    public static final String INFUSION_COLORS = "infusion_colors";
    public static ModConfigSpec.ConfigValue<Integer> EFFICIENCY_COLOR;
    public static ModConfigSpec.ConfigValue<Integer> ENERGY_CAPACITY_COLOR;
    public static ModConfigSpec.ConfigValue<Integer> FORTUNE_COLOR;
    public static ModConfigSpec.ConfigValue<Integer> RANGE_COLOR;
    public static ModConfigSpec.ConfigValue<Integer> SILK_TOUCH_COLOR;
    public static ModConfigSpec.ConfigValue<Integer> SPEED_COLOR;

    public static final String BE_BASE_SETTINGS = "be_base_settings";
    public static ModConfigSpec.IntValue QUARRY_BASE_ENERGY;
    public static ModConfigSpec.IntValue QUARRY_BASE_ENERGY_PER_TICK;
    public static ModConfigSpec.IntValue QUARRY_BASE_ENERGY_NEEDED_PER_BLOCK;
    public static ModConfigSpec.IntValue QUARRY_BASE_RANGE;

    public static ModConfigSpec.IntValue INFUSING_STATION_BASE_ENERGY;
    public static ModConfigSpec.IntValue INFUSING_STATION_BASE_ENERGY_PER_TICK;

    public static final String BE_OTHER_SETTINGS = "other_be_settings";
    public static ModConfigSpec.ConfigValue<List<? extends String>> QUARRY_BLOCKED_DIMENSIONS;

    public static void register(ModContainer mod){
        //registerClientConfigs(mod);
        registerCommonConfigs(mod);
        //registerServerConfigs(mod);
    }

    private static void registerClientConfigs(ModContainer mod){
        mod.registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
    }

    private static void registerCommonConfigs(ModContainer mod){
        infusionConfig();
        infusionColors();
        baseConfig();
        baseOtherConfig();
        mod.registerConfig(ModConfig.Type.COMMON, COMMON_BUILDER.build());
    }

    private static void registerServerConfigs(ModContainer mod){
        mod.registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
    }

    private static void infusionConfig(){
        COMMON_BUILDER.comment("Infusion tier settings").push(INFUSION);
        POINTS_TO_TIER1 = COMMON_BUILDER.comment("Infusion points needed to reach Tier 1.").defineInRange("points_to_tier1", 100, 0, 1000000);
        POINTS_TO_TIER2 = COMMON_BUILDER.comment("Infusion points needed to reach Tier 2.").defineInRange("points_to_tier2", 200, 0, 1000000);
        POINTS_TO_TIER3 = COMMON_BUILDER.comment("Infusion points needed to reach Tier 3.").defineInRange("points_to_tier3", 300, 0, 1000000);
        POINTS_TO_TIER4 = COMMON_BUILDER.comment("Infusion points needed to reach Tier 4.").defineInRange("points_to_tier4", 600, 0, 1000000);
        POINTS_TO_TIER5 = COMMON_BUILDER.comment("Infusion points needed to reach Tier 5.").defineInRange("points_to_tier5", 1200, 0, 1000000);
        POINTS_TO_TIER6 = COMMON_BUILDER.comment("Infusion points needed to reach Tier 6.").defineInRange("points_to_tier6", 3600, 0, 1000000);
        POINTS_TO_TIER7 = COMMON_BUILDER.comment("Infusion points needed to reach Tier 7.").defineInRange("points_to_tier7", 10800, 0, 1000000);
        POINTS_TO_TIER8 = COMMON_BUILDER.comment("Infusion points needed to reach Tier 8.").defineInRange("points_to_tier8", 32400, 0, 1000000);
        POINTS_TO_TIER9 = COMMON_BUILDER.comment("Infusion points needed to reach Tier 9.").defineInRange("points_to_tier9", 97200, 0, 1000000);
        COMMON_BUILDER.pop();
    }

    private static void infusionColors(){
        COMMON_BUILDER.comment("Infusion bar color (hex)").push(INFUSION_COLORS);
        EFFICIENCY_COLOR = COMMON_BUILDER.comment("Efficiency bar color.").define("efficiency_color", 0xffebba34);
        ENERGY_CAPACITY_COLOR = COMMON_BUILDER.comment("Energy Capacity bar color.").define("energy_capacity_color", 0xffeb4034);
        FORTUNE_COLOR = COMMON_BUILDER.comment("Fortune bar color.").define("fortune_color", 0xff345feb);
        RANGE_COLOR = COMMON_BUILDER.comment("Range bar color.").define("range_color", 0xff34cdeb);
        SILK_TOUCH_COLOR = COMMON_BUILDER.comment("Silk Touch bar color.").define("silk_touch_color", 0xffeb34a8);
        SPEED_COLOR = COMMON_BUILDER.comment("Speed bar color.").define("speed_color", 0xffebeb34);
        COMMON_BUILDER.pop();
    }

    private static void baseConfig(){
        COMMON_BUILDER.comment("Machine base values").push(BE_BASE_SETTINGS);
        QUARRY_BASE_ENERGY = COMMON_BUILDER.comment("Quarry base energy capacity.").defineInRange("quarry_base_energy", 10000, 1000, 1000000);
        QUARRY_BASE_ENERGY_PER_TICK = COMMON_BUILDER.comment("Quarry base energy usage per tick.").defineInRange("quarry_base_energy_per_tick", 100, 1, 1000);
        QUARRY_BASE_RANGE = COMMON_BUILDER.comment("Quarry base range before any infusions.").defineInRange("quarry_base_range", 10, 1, 100);
        QUARRY_BASE_ENERGY_NEEDED_PER_BLOCK = COMMON_BUILDER.comment("Quarry base energy needed per mined block.").defineInRange("quarry_base_energy_needed_per_block", 2000, 1, 500000);

        INFUSING_STATION_BASE_ENERGY = COMMON_BUILDER.comment("Infusing Station base energy capacity.").defineInRange("infusing_station_base_energy", 10000, 1000, 1000000);
        INFUSING_STATION_BASE_ENERGY_PER_TICK = COMMON_BUILDER.comment("Infusing Station base energy usage per tick.").defineInRange("infusing_station_base_energy_per_tick", 30, 1, 1000);
    }

    private static void baseOtherConfig(){
        COMMON_BUILDER.comment("Other machine settings").push(BE_OTHER_SETTINGS);
        List<String> defaultBlacklistedDimensions = List.of();

        QUARRY_BLOCKED_DIMENSIONS = COMMON_BUILDER.comment("Quarry dimension blacklist.").define("quarry_blacklisted_dimensions", defaultBlacklistedDimensions);

    }
}
