package com.chamoisest.miningmadness.client.screens;

import com.chamoisest.miningmadness.MiningMadness;
import com.chamoisest.miningmadness.client.screens.base.BaseScreen;
import com.chamoisest.miningmadness.common.containers.InfusingStationMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class InfusingStationScreen extends BaseScreen<InfusingStationMenu> {


    public InfusingStationScreen(InfusingStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = 175;
        this.imageHeight = 178;
        this.titleLabelY = this.titleLabelY - 1;
        this.inventoryLabelY = this.inventoryLabelY + 13;
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "textures/gui/infusing_station_gui.png");
    }

    @Override
    protected void addWidgets() {
        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;

        addEnergyWidget(guiX + 153, guiY + 17);
        addInfusionBarsWidget(guiX + 124, guiY + 17);

        addRedstoneWidget(guiX - 18, calculateButtonY(0, guiY));
        addStatusWidget(guiX + 2, guiY - 10, guiX - 18, calculateButtonY(-1, guiY));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        if(menu.isInfusing()){
            renderProgress(guiGraphics);
        }
    }

    protected void renderProgress(GuiGraphics guiGraphics) {
        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.getGuiTexture());

        float progress = menu.getInfusingProgressPercentage();

        float centralBarsProgress = Math.min(progress / 20 * 100, 100);
        int centralScaled = (int)(3 * (centralBarsProgress / 100));

        if(menu.hasCraftingIngredientInSlot(1)) guiGraphics.blit(this.getGuiTexture(), guiX + 45, guiY + 34, 176, 32, 14, centralScaled);
        if(menu.hasCraftingIngredientInSlot(2)) guiGraphics.blit(this.getGuiTexture(), guiX + 64 + (3 - centralScaled), guiY + 42, 176 + (3 - centralScaled), 18, centralScaled, 14);
        if(menu.hasCraftingIngredientInSlot(3)) guiGraphics.blit(this.getGuiTexture(), guiX + 45, guiY + 61 + (3 - centralScaled), 176, 49 + (3 - centralScaled), 14, centralScaled);
        if(menu.hasCraftingIngredientInSlot(4)) guiGraphics.blit(this.getGuiTexture(), guiX + 37, guiY + 42, 176, 35, centralScaled, 14);

        float bottomBarProgress = (progress - 20) / 70 * 100;
        int bottomScaledX = (int)(22 * (bottomBarProgress / 100));
        int bottomScaledY = (int)(16 * (bottomBarProgress / 100));

        guiGraphics.blit(this.getGuiTexture(), guiX + 62, guiY + 59, 176, 0, bottomScaledX, bottomScaledY);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
    }
}
