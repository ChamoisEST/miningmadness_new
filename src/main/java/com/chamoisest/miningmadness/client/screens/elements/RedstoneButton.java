package com.chamoisest.miningmadness.client.screens.elements;

import com.chamoisest.miningmadness.MiningMadness;
import com.chamoisest.miningmadness.common.blockentities.data.StatusData;
import com.chamoisest.miningmadness.common.containers.interfaces.RedstoneControlledMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.StatusMenu;
import com.chamoisest.miningmadness.util.TextUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

public class RedstoneButton extends ExtendedButton {

    private final ResourceLocation elements = ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "textures/gui/elements/gui_elements.png");
    private final RedstoneControlledMenu menu;
    private final Font font;

    public RedstoneButton(int xPos, int yPos, Font font, RedstoneControlledMenu menu, OnPress handler){
        this(xPos, yPos, 16, 16, Component.literal(""), font, menu, handler);
    }

    public RedstoneButton(int xPos, int yPos, int width, int height, Component displayString, Font font, RedstoneControlledMenu menu, OnPress handler) {
        super(xPos, yPos, width, height, displayString, handler);
        this.menu = menu;
        this.font = font;
    }

    public MutableComponent getWidgetTooltip() {
        MutableComponent component = Component.empty();

        component.append(Component.translatable("miningmadness.redstone.tooltip_pre"));
        component.append(Component.literal(": "));
        component.append(menu.getRedstoneData().getRedstoneStatus().getTranslated());

        return component;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, elements);

        switch(menu.getRedstoneData().getRedstoneStatus()){
            case HIGH -> guiGraphics.blit(elements, getX(), getY(), 16, 0, width, height, 256, 256);
            case LOW -> guiGraphics.blit(elements, getX(), getY(), 0, 0, width, height, 256, 256);
            case PULSE -> guiGraphics.blit(elements, getX(), getY(), 48, 0, width, height, 256, 256);
            case IGNORE -> guiGraphics.blit(elements, getX(), getY(), 32, 0, width, height, 256, 256);
        }

        if(this.isHovered()){
            guiGraphics.renderTooltip(font, getWidgetTooltip(), mouseX, mouseY);
        }
    }
}
