package com.chamoisest.miningmadness.client.screens.elements;

import com.chamoisest.miningmadness.common.blockentities.data.StatusData;
import com.chamoisest.miningmadness.common.containers.interfaces.StatusMenu;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class StatusText extends AbstractWidget {

    private final Font font;
    private final StatusMenu menu;

    public StatusText(int x, int y, Font font, StatusMenu menu) {
        super(x, y, 90, 10, Component.empty());
        this.font = font;
        this.menu = menu;
    }

    protected MutableComponent getWidgetText(){
        MutableComponent component = Component.empty();

        component.append(Component.translatable("miningmadness.status.status_pre"));
        component.append(": ");
        component.append(menu.getStatusData().getStatus().getTranslated());

        return component;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        guiGraphics.drawString(this.font, getWidgetText(), this.getX(), this.getY(), menu.getStatusData().getStatus().getColor());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }


}
