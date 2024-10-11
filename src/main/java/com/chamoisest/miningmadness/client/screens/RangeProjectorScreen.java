package com.chamoisest.miningmadness.client.screens;

import com.chamoisest.miningmadness.MiningMadness;
import com.chamoisest.miningmadness.client.screens.base.BaseScreen;
import com.chamoisest.miningmadness.client.screens.elements.RangeEditButton;
import com.chamoisest.miningmadness.common.blockentities.base.enums.AreaPosEnum;
import com.chamoisest.miningmadness.common.containers.RangeProjectorMenu;
import com.chamoisest.miningmadness.util.PacketUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class RangeProjectorScreen extends BaseScreen<RangeProjectorMenu> {

    protected boolean widgetsAdded = false;

    public RangeProjectorScreen(RangeProjectorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "textures/gui/blank_gui.png");
    }

    @Override
    protected void addWidgets() {
        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;

        if(menu.isConnected) {
            //RADIUS
            //X
            addRangeEditButton(guiX + 25, guiY + 19, RangeEditButton.RangeEditButtonType.MINUS_10, AreaPosEnum.RAD_X);
            addRangeEditButton(guiX + 35, guiY + 19, RangeEditButton.RangeEditButtonType.MINUS, AreaPosEnum.RAD_X);

            addRangeEditButton(guiX + 75, guiY + 19, RangeEditButton.RangeEditButtonType.PLUS, AreaPosEnum.RAD_X);
            addRangeEditButton(guiX + 85, guiY + 19, RangeEditButton.RangeEditButtonType.PLUS_10, AreaPosEnum.RAD_X);

            //Y
            addRangeEditButton(guiX + 25, guiY + 34, RangeEditButton.RangeEditButtonType.MINUS_10, AreaPosEnum.RAD_Y);
            addRangeEditButton(guiX + 35, guiY + 34, RangeEditButton.RangeEditButtonType.MINUS, AreaPosEnum.RAD_Y);

            addRangeEditButton(guiX + 75, guiY + 34, RangeEditButton.RangeEditButtonType.PLUS, AreaPosEnum.RAD_Y);
            addRangeEditButton(guiX + 85, guiY + 34, RangeEditButton.RangeEditButtonType.PLUS_10, AreaPosEnum.RAD_Y);

            //Z

            addRangeEditButton(guiX + 25, guiY + 49, RangeEditButton.RangeEditButtonType.MINUS_10, AreaPosEnum.RAD_Z);
            addRangeEditButton(guiX + 35, guiY + 49, RangeEditButton.RangeEditButtonType.MINUS, AreaPosEnum.RAD_Z);

            addRangeEditButton(guiX + 75, guiY + 49, RangeEditButton.RangeEditButtonType.PLUS, AreaPosEnum.RAD_Z);
            addRangeEditButton(guiX + 85, guiY + 49, RangeEditButton.RangeEditButtonType.PLUS_10, AreaPosEnum.RAD_Z);

            //OFFSET
            //X
            addRangeEditButton(guiX + 100, guiY + 19, RangeEditButton.RangeEditButtonType.MINUS_10, AreaPosEnum.OFF_X);
            addRangeEditButton(guiX + 110, guiY + 19, RangeEditButton.RangeEditButtonType.MINUS, AreaPosEnum.OFF_X);

            addRangeEditButton(guiX + 147, guiY + 19, RangeEditButton.RangeEditButtonType.PLUS, AreaPosEnum.OFF_X);
            addRangeEditButton(guiX + 157, guiY + 19, RangeEditButton.RangeEditButtonType.PLUS_10, AreaPosEnum.OFF_X);

            //Y

            addRangeEditButton(guiX + 100, guiY + 34, RangeEditButton.RangeEditButtonType.MINUS_10, AreaPosEnum.OFF_Y);
            addRangeEditButton(guiX + 110, guiY + 34, RangeEditButton.RangeEditButtonType.MINUS, AreaPosEnum.OFF_Y);

            addRangeEditButton(guiX + 147, guiY + 34, RangeEditButton.RangeEditButtonType.PLUS, AreaPosEnum.OFF_Y);
            addRangeEditButton(guiX + 157, guiY + 34, RangeEditButton.RangeEditButtonType.PLUS_10, AreaPosEnum.OFF_Y);

            //Z

            addRangeEditButton(guiX + 100, guiY + 49, RangeEditButton.RangeEditButtonType.MINUS_10, AreaPosEnum.OFF_Z);
            addRangeEditButton(guiX + 110, guiY + 49, RangeEditButton.RangeEditButtonType.MINUS, AreaPosEnum.OFF_Z);

            addRangeEditButton(guiX + 147, guiY + 49, RangeEditButton.RangeEditButtonType.PLUS, AreaPosEnum.OFF_Z);
            addRangeEditButton(guiX + 157, guiY + 49, RangeEditButton.RangeEditButtonType.PLUS_10, AreaPosEnum.OFF_Z);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;

        if(menu.isConnected) {

            if(!widgetsAdded) {
                addWidgets();
                widgetsAdded = true;
            }

            guiGraphics.drawString(font, Component.translatable("miningmadness.gui.size"), guiX + 51, guiY + 7, 4210752, false);
            guiGraphics.drawString(font, Component.translatable("miningmadness.gui.offset"), guiX + 126, guiY + 7, 4210752, false);
            guiGraphics.drawString(font, Component.literal("X"), guiX + 10, guiY + 20, 4210752, false);
            guiGraphics.drawString(font, Component.literal("Y"), guiX + 10, guiY + 35, 4210752, false);
            guiGraphics.drawString(font, Component.literal("Z"), guiX + 10, guiY + 50, 4210752, false);

            Component component = Component.literal(menu.radX + "");
            guiGraphics.drawString(font, component , guiX + 60 - font.width(component) / 2, guiY + 20 , 4210752, false);

            component = Component.literal(menu.radY + "");
            guiGraphics.drawString(font, component , guiX + 60 - font.width(component) / 2, guiY + 35 , 4210752, false);

            component = Component.literal(menu.radZ + "");
            guiGraphics.drawString(font, component , guiX + 60 - font.width(component) / 2, guiY + 50 , 4210752, false);

            component = Component.literal(menu.offX + "");
            guiGraphics.drawString(font, component , guiX + 134 - font.width(component) / 2, guiY + 20 , 4210752, false);

            component = Component.literal(menu.offY + "");
            guiGraphics.drawString(font, component , guiX + 134 - font.width(component) / 2, guiY + 35 , 4210752, false);

            component = Component.literal(menu.offZ + "");
            guiGraphics.drawString(font, component , guiX + 134 - font.width(component) / 2, guiY + 50 , 4210752, false);


        }else{
            guiGraphics.drawString(font, Component.translatable("miningmadness.gui.range_projector.not_connected"), guiX + 32, guiY + 35, 4210752, false);
        }


    }


    private void addRangeEditButton(int x, int y, RangeEditButton.RangeEditButtonType type, AreaPosEnum posType) {
        this.addRenderableWidget(new RangeEditButton(x, y, type, button -> {
            PacketUtil.syncAreaRange(type, posType);
        }));
    }
}
