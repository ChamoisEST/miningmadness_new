package com.chamoisest.miningmadness.setup;

import com.chamoisest.miningmadness.MiningMadness;
import com.chamoisest.miningmadness.common.blockentities.QuarryBE;
import com.chamoisest.miningmadness.common.blockentities.InfusingStationBE;
import com.chamoisest.miningmadness.common.blockentities.RangeProjectorBE;
import com.chamoisest.miningmadness.common.blockentities.interfaces.EnergyHandlerBE;
import com.chamoisest.miningmadness.common.blockentities.interfaces.InfusionHandlerBE;
import com.chamoisest.miningmadness.common.blocks.QuarryBlock;
import com.chamoisest.miningmadness.common.blocks.InfusingStationBlock;
import com.chamoisest.miningmadness.common.blocks.RangeProjectorBlock;
import com.chamoisest.miningmadness.common.capabilities.AdaptedEnergyStorage;
import com.chamoisest.miningmadness.common.capabilities.infusion.InfusionStorage;
import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.*;
import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.common.containers.QuarryMenu;
import com.chamoisest.miningmadness.common.containers.InfusingStationMenu;
import com.chamoisest.miningmadness.common.containers.RangeProjectorMenu;
import com.chamoisest.miningmadness.common.items.GemOfFocusItem;
import com.chamoisest.miningmadness.common.items.block.BaseMachineBlockItem;
import com.chamoisest.miningmadness.common.recipes.infusing.InfusingRecipe;
import com.chamoisest.miningmadness.common.recipes.infusing.InfusingRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class Registration {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MiningMadness.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MiningMadness.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MiningMadness.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MiningMadness.MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MiningMadness.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MiningMadness.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MiningMadness.MODID);
    public static final DeferredRegister<Infusion> INFUSIONS = DeferredRegister.create(MiningMadnessRegistries.INFUSIONS, MiningMadness.MODID);

    //BLOCKS
    public static final DeferredBlock<QuarryBlock> QUARRY = registerMachineBlock("quarry",
            QuarryBlock::new);
    public static final DeferredBlock<InfusingStationBlock> INFUSING_STATION = registerMachineBlock("infusing_station",
            InfusingStationBlock::new);
    public static final DeferredBlock<RangeProjectorBlock> RANGE_PROJECTOR = registerMachineBlock("range_projector",
            RangeProjectorBlock::new);
    public static final DeferredBlock<Block> SUGAR_BLOCK = registerBlock("sugar_block", () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(0.5F)
                    .sound(SoundType.POWDER_SNOW)
    ));

    //BLOCK ENTITIES
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuarryBE>> QUARRY_BE = BLOCK_ENTITIES.register("quarry_be",
            () -> BlockEntityType.Builder.of(QuarryBE::new, QUARRY.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfusingStationBE>> INFUSING_STATION_BE = BLOCK_ENTITIES.register("infusing_station_be",
            () -> BlockEntityType.Builder.of(InfusingStationBE::new, INFUSING_STATION.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RangeProjectorBE>> RANGE_PROJECTOR_BE = BLOCK_ENTITIES.register("range_projector_be",
            () -> BlockEntityType.Builder.of(RangeProjectorBE::new, RANGE_PROJECTOR.get()).build(null));

    //ITEMS
    public static final Supplier<GemOfFocusItem> GEM_OF_FOCUS = ITEMS.register("gem_of_focus", GemOfFocusItem::new);
    public static final Supplier<Item> COPPER_MACHINE_FRAME = ITEMS.registerSimpleItem("copper_machine_frame");
    public static final Supplier<Item> IRON_MACHINE_FRAME = ITEMS.registerSimpleItem("iron_machine_frame");

    //INFUSIONS
    public static final DeferredHolder<Infusion, Infusion> EFFICIENCY = INFUSIONS.register("efficiency", EfficiencyInfusion::new);
    public static final DeferredHolder<Infusion, Infusion> ENERGY_CAPACITY = INFUSIONS.register("energy_capacity", EnergyCapacityInfusion::new);
    public static final DeferredHolder<Infusion, Infusion> FORTUNE = INFUSIONS.register("fortune", FortuneInfusion::new);
    public static final DeferredHolder<Infusion, Infusion> RANGE = INFUSIONS.register("range", RangeInfusion::new);
    public static final DeferredHolder<Infusion, Infusion> SILK_TOUCH = INFUSIONS.register("silk_touch", SilkTouchInfusion::new);
    public static final DeferredHolder<Infusion, Infusion> SPEED = INFUSIONS.register("speed", SpeedInfusion::new);

    //MENUS
    public static final DeferredHolder<MenuType<?>, MenuType<QuarryMenu>> QUARRY_MENU = MENUS.register("quarry_menu",
            () -> IMenuTypeExtension.create(QuarryMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<InfusingStationMenu>> INFUSING_STATION_MENU = MENUS.register("infusing_station_menu",
            () -> IMenuTypeExtension.create(InfusingStationMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<RangeProjectorMenu>> RANGE_PROJECTOR_MENU = MENUS.register("range_projector_menu",
            () -> IMenuTypeExtension.create(RangeProjectorMenu::new));

    //RECIPE TYPES
    public static final Supplier<RecipeType<InfusingRecipe>> INFUSING =
            RECIPE_TYPES.register(
                "infusing",
                    () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "infusing"))
            );

    //RECIPE SERIALIZERS
    public static final Supplier<RecipeSerializer<InfusingRecipe>> INFUSING_SERIALIZER = RECIPE_SERIALIZERS.register(
            "infusing",
            InfusingRecipeSerializer::new
    );

    //ATTACHMENT TYPES
    public static final Supplier<AttachmentType<AdaptedEnergyStorage>> ENERGY_STORAGE = ATTACHMENT_TYPES.register(
        "energy_storage", () -> AttachmentType.serializable(entity -> {
                if(entity instanceof EnergyHandlerBE be){
                    int energyCapacity = be.getBaseEnergyCapacity();
                    int baseUsagePerTick = be.getBaseEnergyUsagePerTick();
                    return new AdaptedEnergyStorage(energyCapacity, baseUsagePerTick, be);
                }else{
                    throw new IllegalStateException("Cannot attach energy storage attachment to a non-powered machine.");
                }
            }).build()
    );

    public static final Supplier<AttachmentType<InfusionStorage>> INFUSION_STORAGE = ATTACHMENT_TYPES.register(
            "infusion_storage", () -> AttachmentType.serializable(entity -> {
                if(entity instanceof InfusionHandlerBE be){
                    return new InfusionStorage(be);
                }else{
                    throw new IllegalStateException("Cannot attach infusion storage attachment to a non-infusable machine.");
                }
            }).build()
    );

    //HELPER FUNCTIONS
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredBlock<T> registerMachineBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerMachineBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> void registerMachineBlockItem(String name, DeferredBlock<T> block) {
        ITEMS.register(name, () -> new BaseMachineBlockItem(block.get(), new Item.Properties()));
    }

}
