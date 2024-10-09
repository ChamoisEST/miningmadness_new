package com.chamoisest.miningmadness.client.screens.elements;

import com.chamoisest.miningmadness.common.capabilities.AdaptedEnergyStorage;
import com.chamoisest.miningmadness.common.containers.interfaces.EnergyMenu;
import com.chamoisest.miningmadness.util.TextUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnergyArea extends AbstractWidget {

    private final Rect2i energyBarArea;
    private final Font font;
    private final AdaptedEnergyStorage energyStorage;

    private final boolean isJEI;
    private int energyNeeded = 0;

    public EnergyArea(int x, int y, Font font, int energyNeeded){
        super(x, y, 14, 64, Component.empty());
        this.energyBarArea = new Rect2i(x,y,14,64);
        this.font = font;
        this.energyStorage = null;
        this.isJEI = true;
        this.energyNeeded = energyNeeded;
    }

    public EnergyArea(int x, int y, Font font, EnergyMenu menu) {
        this(x, y, 14, 64, font, menu);
    }

    public EnergyArea(int x, int y, int width, int height, Font font, EnergyMenu menu) {
        super(x, y, width, height, Component.empty());
        this.font = font;
        this.energyBarArea = new Rect2i(x, y, width, height);
        this.energyStorage = menu.getEnergyStorage();
        this.isJEI = false;
    }

    public List<Component> getWidgetTooltips() {
        List<Component> component = new ArrayList<>();
        int energyStored;
        int energyCapacity;

        if(isJEI){
            energyStored = this.energyNeeded;
            energyCapacity = this.energyNeeded;
        }else{
            energyStored = energyStorage.getEnergyStored();
            energyCapacity = energyStorage.getMaxEnergyStored();
        }

        String energyStoredText = TextUtil.formatNumber(energyStored);
        String energyCapacityText = TextUtil.formatNumber(energyCapacity);

        component.add(Component.literal(energyStoredText + "/" + energyCapacityText + "FE"));

        if(!isJEI){
            int energyUsagePerTick = energyStorage.getUsagePerTick();
            component.add(Component.literal(energyUsagePerTick + "FE/t"));
        }

        return component;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        final int width = energyBarArea.getWidth();
        final int height = energyBarArea.getHeight();
        int energyStored;
        int energyCapacity;

        if(isJEI){
            energyStored = this.energyNeeded;
            energyCapacity = this.energyNeeded;
        }else{
            energyStored = energyStorage.getEnergyStored();
            energyCapacity = energyStorage.getMaxEnergyStored();
        }


        int storedHeight = (int)(height*(energyStored/(float)energyCapacity));

        guiGraphics.fillGradient(
                energyBarArea.getX(),
                energyBarArea.getY()+(height-storedHeight),
                energyBarArea.getX() + width,
                energyBarArea.getY() + height,
                0xffb51500,
                0xff600b00
        );

        if(this.isHovered()){
            guiGraphics.renderTooltip(font, getWidgetTooltips(), Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }


}
