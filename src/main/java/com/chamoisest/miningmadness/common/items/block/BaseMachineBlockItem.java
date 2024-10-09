package com.chamoisest.miningmadness.common.items.block;

import com.chamoisest.miningmadness.common.capabilities.infusion.IInfusionStorage;
import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.common.items.block.interfaces.InfusionItem;
import com.chamoisest.miningmadness.setup.MiningMadnessCapabilities;
import com.chamoisest.miningmadness.setup.MiningMadnessDataComponents;
import com.chamoisest.miningmadness.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.SortedMap;

public class BaseMachineBlockItem extends BlockItem implements InfusionItem {
    public BaseMachineBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {

    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        IInfusionStorage infusionCap = stack.getCapability(MiningMadnessCapabilities.InfusionStorage.ITEM);

        if(infusionCap != null) {
            if(Screen.hasShiftDown()){
                SortedMap<String, Infusion> infusionMap = infusionCap.getSortedInfusions();

                tooltipComponents.add(Component.literal(""));
                for(Infusion infusion : infusionMap.values()) {
                    int tierPoints = infusion.getTierPoints();
                    int tier = infusion.getTier();

                    tooltipComponents.add(Component.translatable("miningmadness.infusion.type_" + infusion.getName())
                            .append(Component.literal(": " + tierPoints+ "/" + infusion.getPointsToTier(tier))).withColor(infusion.getColor())
                    );
                }
            }else{
                tooltipComponents.add(Component.translatable("tooltip.miningmadness.press")
                        .append(Component.literal(" SHIFT ").withStyle(ChatFormatting.AQUA))
                        .append(Component.translatable("tooltip.miningmadness.for_infusion_info"))
                );
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
