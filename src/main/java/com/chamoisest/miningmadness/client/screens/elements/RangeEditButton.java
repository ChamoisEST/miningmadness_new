package com.chamoisest.miningmadness.client.screens.elements;

import com.chamoisest.miningmadness.MiningMadness;
import com.chamoisest.miningmadness.common.containers.RangeProjectorMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.AreaMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

public class RangeEditButton extends ExtendedButton {

    private final ResourceLocation elements = ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "textures/gui/elements/gui_elements_small.png");
    private final RangeEditButtonType type;

    public RangeEditButton(int xPos, int yPos, RangeEditButtonType type, OnPress handler){
        this(xPos, yPos, 9, 9, Component.literal(""), type, handler);
    }

    public RangeEditButton(int xPos, int yPos, int width, int height, Component displayString, RangeEditButtonType type, OnPress handler) {
        super(xPos, yPos, width, height, displayString, handler);
        this.type = type;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, elements);

        guiGraphics.blit(elements, getX(), getY(), type.getElementX(), type.getElementY(), width, height, 128, 128);

    }

    public enum RangeEditButtonType{
        PLUS,
        MINUS,
        PLUS_10,
        MINUS_10;

        public int getElementX(){
            return switch (this){
                case PLUS -> 1;
                case MINUS -> 11;
                case PLUS_10, MINUS_10 -> 21;
            };
        }

        public int getElementY(){
            return switch (this){
                case PLUS, MINUS_10, PLUS_10, MINUS -> 1;
            };
        }

        public int getChangeValue(){
            return switch (this){
                case PLUS -> 1;
                case MINUS -> -1;
                case PLUS_10 -> 10;
                case MINUS_10 -> -10;
            };
        }
    }
}
