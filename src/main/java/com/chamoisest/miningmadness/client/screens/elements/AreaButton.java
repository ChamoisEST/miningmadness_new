package com.chamoisest.miningmadness.client.screens.elements;

import com.chamoisest.miningmadness.MiningMadness;
import com.chamoisest.miningmadness.common.containers.interfaces.AreaMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.RedstoneControlledMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

public class AreaButton extends ExtendedButton {

    private final ResourceLocation elements = ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "textures/gui/elements/gui_elements.png");
    private final AreaMenu menu;
    private final Font font;

    public AreaButton(int xPos, int yPos, Font font, AreaMenu menu, OnPress handler){
        this(xPos, yPos, 16, 16, Component.literal(""), font, menu, handler);
    }

    public AreaButton(int xPos, int yPos, int width, int height, Component displayString, Font font, AreaMenu menu, OnPress handler) {
        super(xPos, yPos, width, height, displayString, handler);
        this.menu = menu;
        this.font = font;
    }

    public MutableComponent getWidgetTooltip() {
        MutableComponent component = Component.empty();

        component.append(Component.translatable("miningmadness.area_toggle.tooltip_pre"));
        component.append(Component.literal(": "));
        if(!menu.getAreaData().getDisplayAreaStatus()){
            component.append(Component.translatable("miningmadness.area_status.off"));
        }else{
            component.append(Component.translatable("miningmadness.area_status.on"));
        }

        return component;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, elements);

        if(menu.getAreaData().getDisplayAreaStatus()){
            guiGraphics.blit(elements, getX(), getY(), 96, 0, width, height, 256, 256);
        }else{
            guiGraphics.blit(elements, getX(), getY(), 80, 0, width, height, 256, 256);
        }

        if(this.isHovered()){
            guiGraphics.renderTooltip(font, getWidgetTooltip(), mouseX, mouseY);
        }
    }
}
