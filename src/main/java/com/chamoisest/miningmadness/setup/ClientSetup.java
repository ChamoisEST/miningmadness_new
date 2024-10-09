package com.chamoisest.miningmadness.setup;

import com.chamoisest.miningmadness.MiningMadness;
import com.chamoisest.miningmadness.client.renderers.RenderHelper;
import com.chamoisest.miningmadness.client.renderers.blockentity.QuarryBER;
import com.chamoisest.miningmadness.client.renderers.blockentity.base.AreaDisplayBER;
import com.chamoisest.miningmadness.client.screens.QuarryScreen;
import com.chamoisest.miningmadness.client.screens.InfusingStationScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

@EventBusSubscriber(modid = MiningMadness.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(Registration.QUARRY_MENU.get(), QuarryScreen::new);
        event.register(Registration.INFUSING_STATION_MENU.get(), InfusingStationScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Registration.QUARRY_BE.get(), QuarryBER::new);
    }
}
