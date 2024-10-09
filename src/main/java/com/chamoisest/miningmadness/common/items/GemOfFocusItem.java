package com.chamoisest.miningmadness.common.items;

import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class GemOfFocusItem extends Item {

    public GemOfFocusItem() {
        super(new Item.Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()){
            tooltipComponents.add(Component.translatable("tooltip.miningmadness.gem_of_focus_description"));
        }else{
            tooltipComponents.add(Component.translatable("tooltip.miningmadness.press")
                    .append(Component.literal(" SHIFT ").withStyle(ChatFormatting.AQUA))
                    .append(Component.translatable("tooltip.miningmadness.for_more_info"))
            );
        }
    }
}
