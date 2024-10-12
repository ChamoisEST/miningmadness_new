package com.chamoisest.miningmadness.client.screens.elements;

import com.chamoisest.miningmadness.MiningMadness;
import com.chamoisest.miningmadness.common.blockentities.data.StatusData;
import com.chamoisest.miningmadness.common.containers.interfaces.StatusMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatusButton extends ExtendedButton {

    private final ResourceLocation elements = ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "textures/gui/elements/gui_elements.png");
    private final StatusMenu menu;

    public StatusButton(int xPos, int yPos, StatusMenu menu, OnPress handler){
        this(xPos, yPos, 16, 16, Component.literal(""), menu, handler);
    }

    public StatusButton(int xPos, int yPos, int width, int height, Component displayString, StatusMenu menu, OnPress handler) {
        super(xPos, yPos, width, height, displayString, handler);
        this.menu = menu;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, elements);

        if(StatusData.Status.getActiveStatuses().contains(this.menu.getStatusData().getStatus())){
            guiGraphics.blit(elements, getX(), getY(), 144, 0, width, height, 256, 256);
        }else{
            guiGraphics.blit(elements, getX(), getY(), 128, 0, width, height, 256, 256);
        }
    }
}
