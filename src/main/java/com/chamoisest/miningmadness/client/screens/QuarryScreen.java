package com.chamoisest.miningmadness.client.screens;

import com.chamoisest.miningmadness.MiningMadness;
import com.chamoisest.miningmadness.client.screens.base.BaseScreen;
import com.chamoisest.miningmadness.common.containers.QuarryMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class QuarryScreen extends BaseScreen<QuarryMenu> {

    public QuarryScreen(QuarryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "textures/gui/quarry_gui.png");
    }

    @Override
    protected void addWidgets() {
        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;

        addEnergyWidget(guiX + 153, guiY + 11);
        addInfusionBarsWidget(guiX + 124, guiY + 17);

        addStatusWidget(guiX + 2, guiY - 10, guiX - 18, calculateButtonY(0, guiY));
        addRedstoneWidget(guiX - 18, calculateButtonY(1, guiY));
        addAreaDisplayWidget(guiX - 18, calculateButtonY(2, guiY));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
    }
}
