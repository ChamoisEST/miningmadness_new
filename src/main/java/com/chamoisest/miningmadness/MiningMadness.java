package com.chamoisest.miningmadness;

import com.chamoisest.miningmadness.common.blockentities.interfaces.EnergyHandlerBE;
import com.chamoisest.miningmadness.common.blockentities.interfaces.InfusionHandlerBE;
import com.chamoisest.miningmadness.common.blockentities.interfaces.ItemHandlerBE;
import com.chamoisest.miningmadness.common.capabilities.infusion.InfusionStorage;
import com.chamoisest.miningmadness.common.items.block.interfaces.InfusionItem;
import com.chamoisest.miningmadness.setup.*;
import com.chamoisest.miningmadness.common.network.PacketHandler;
import net.minecraft.core.Direction;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MiningMadness.MODID)
public class MiningMadness
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "miningmadness";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public MiningMadness(IEventBus modEventBus, ModContainer modContainer)
    {

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        Registration.BLOCKS.register(modEventBus);
        Registration.ITEMS.register(modEventBus);
        Registration.MENUS.register(modEventBus);
        Registration.BLOCK_ENTITIES.register(modEventBus);
        Registration.ATTACHMENT_TYPES.register(modEventBus);

        Registration.RECIPE_TYPES.register(modEventBus);
        Registration.RECIPE_SERIALIZERS.register(modEventBus);

        Registration.INFUSIONS.register(modEventBus);

        ModSetup.CREATIVE_MODE_TABS.register(modEventBus);

        MiningMadnessDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(PacketHandler::registerPackets);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        Config.register(modContainer);

        if(FMLLoader.getDist().isClient()) {
            modEventBus.addListener(ClientSetup::init);
        }

    }

    private void registerRegistries(NewRegistryEvent event){
        event.register(MiningMadnessRegistries.INFUSIONS);
    }


    private void registerCapabilities(RegisterCapabilitiesEvent event){

        event.registerBlock(Capabilities.ItemHandler.BLOCK,
                (level, pos, state, be, side) -> {
                    if(be instanceof ItemHandlerBE handlerBe)
                        return handlerBe.getItemHandler();
                    return null;
                },
                Registration.QUARRY.get()
        );

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                Registration.INFUSING_STATION_BE.get(),
                (be, side) -> {
                    if (side == Direction.UP) return be.getTopItemHandler();
                    else if (side == Direction.DOWN) return be.getBottomItemHandler();
                    else return be.getMiddleItemHandler();
                }
        );

        event.registerBlock(Capabilities.EnergyStorage.BLOCK,
                (level, pos, state, be, side) -> {
                    if (be instanceof EnergyHandlerBE)
                        return be.getData(Registration.ENERGY_STORAGE);
                    return null;
                },
                Registration.QUARRY.get(),
                Registration.INFUSING_STATION.get()
        );

        event.registerBlock(MiningMadnessCapabilities.InfusionStorage.BLOCK,
                (level, pos, state, be, side) -> {
                    if (be instanceof InfusionHandlerBE) {
                        return be.getData(Registration.INFUSION_STORAGE);
                    }
                    return null;
                },
                Registration.QUARRY.get(),
                Registration.INFUSING_STATION.get()
        );

        event.registerItem(MiningMadnessCapabilities.InfusionStorage.ITEM, (itemStack, provider) -> {
                    if(itemStack.getItem() instanceof InfusionItem){
                        return new InfusionStorage(itemStack);
                    }
                    return null;
                },
                Registration.QUARRY.get(),
                Registration.INFUSING_STATION.get()
        );
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }
}
