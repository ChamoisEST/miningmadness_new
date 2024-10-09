package com.chamoisest.miningmadness.common.recipes.infusing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record InfusingRecipeInput(
        ItemStack infusionStack,
        ItemStack slot1,
        ItemStack slot2,
        ItemStack slot3,
        ItemStack slot4
) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return switch(index){
            case 0 -> infusionStack;
            case 1 -> slot1;
            case 2 -> slot2;
            case 3 -> slot3;
            case 4 -> slot4;
            default -> throw new IllegalArgumentException("Recipe does not contain slot " + index);
        };
    }

    @Override
    public int size() {
        return 5;
    }
}
