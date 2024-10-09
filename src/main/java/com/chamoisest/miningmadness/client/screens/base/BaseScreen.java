package com.chamoisest.miningmadness.client.screens.base;

import com.chamoisest.miningmadness.client.screens.elements.*;
import com.chamoisest.miningmadness.common.containers.base.BaseMenu;
import com.chamoisest.miningmadness.common.containers.interfaces.*;
import com.chamoisest.miningmadness.common.network.data.MachineStatusPayload;
import com.chamoisest.miningmadness.common.network.data.RedstoneStatusPayload;
import com.chamoisest.miningmadness.util.MouseUtil;
import com.chamoisest.miningmadness.util.PacketUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseScreen<T extends BaseMenu> extends AbstractContainerScreen<T> {

    protected T menu;
    private final ResourceLocation guiTexture;
    protected List<AbstractWidget> activeWidgets = new ArrayList<>();


    public BaseScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.menu = menu;
        guiTexture = getGuiTexture();
    }

    protected abstract ResourceLocation getGuiTexture();
    protected abstract void addWidgets();

    @Override
    protected void init() {
        super.init();
        addWidgets();
    }

    @Override
    protected void containerTick() {

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(this.guiTexture, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }

    public int calculateButtonY(int index, int guiY){
        return guiY + 2 + (index * 18);
    }

    //WIDGETS
    protected void addEnergyWidget(int startX, int startY) {
        if(this.menu instanceof EnergyMenu energyMenu){
            EnergyArea energyArea = new EnergyArea(startX, startY, this.font, energyMenu);

            this.addRenderableWidget(energyArea);
            this.activeWidgets.add(energyArea);
        }
    }

    protected void addStatusWidget(int startX, int startY, int btnX, int btnY) {
        if(this.menu instanceof StatusMenu statusMenu){
            StatusText statusText = new StatusText(startX, startY, this.font, statusMenu);

            if(statusMenu.getStatusData().getAddButton()) {
                StatusButton statusButton = new StatusButton(btnX, btnY, statusMenu, button -> {
                    PacketUtil.syncMachineStatus(statusMenu.getNewStatus().getNumericalValue());
                });

                this.addRenderableWidget(statusButton);
                this.activeWidgets.add(statusButton);
            }

            this.addRenderableWidget(statusText);

            this.activeWidgets.add(statusText);
        }
    }

    protected void addRedstoneWidget(int btnX, int btnY) {
        if(this.menu instanceof RedstoneControlledMenu redstoneMenu){
            RedstoneButton redstoneButton = new RedstoneButton(btnX, btnY, this.font, redstoneMenu, button -> {
                PacketUtil.syncMachineRedstoneStatus(redstoneMenu.getNewRedstoneStatus().getNumericalValue());
            });

            this.addRenderableWidget(redstoneButton);
            this.activeWidgets.add(redstoneButton);
        }
    }

    protected void addInfusionBarsWidget(int startX, int startY){
        if(this.menu instanceof InfusionMenu infusionMenu){
            InfusionBars infusionBars = new InfusionBars(startX, startY, this.font, infusionMenu);

            this.addRenderableWidget(infusionBars);
            this.activeWidgets.add(infusionBars);
        }
    }

    protected void addAreaDisplayWidget(int btnX, int btnY){
        if(this.menu instanceof AreaMenu areaMenu){
            AreaButton areaButton = new AreaButton(btnX, btnY, this.font, areaMenu, button -> {
                PacketUtil.syncAreaDisplayToggle(!areaMenu.getAreaData().getDisplayAreaStatus());
            });

            this.addRenderableWidget(areaButton);
            this.activeWidgets.add(areaButton);
        }
    }

    public void updateWidgets(){
        for(AbstractWidget widget : activeWidgets){
            removeWidget(widget);
        }

        activeWidgets.clear();
        addWidgets();
    }
}
