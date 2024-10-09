package com.chamoisest.miningmadness.common.recipes.infusing;

import com.chamoisest.miningmadness.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InfusingRecipe implements Recipe<InfusingRecipeInput> {

    private final List<Ingredient> infusionItems;
    private final Map<ResourceLocation, Integer> outputInfusion;
    private final int energyReq;

    private final Optional<ItemStack> output;

    public InfusingRecipe(List<Ingredient> infusionItems, Optional<ItemStack> output, Map<ResourceLocation, Integer> outputInfusion, int energyReq) {
        this.infusionItems = infusionItems;
        this.outputInfusion = outputInfusion;
        this.energyReq = energyReq;
        this.output = output;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.addAll(infusionItems);
        return ingredients;
    }

    @Override
    public boolean matches(@NotNull InfusingRecipeInput input, @NotNull Level level) {
        boolean hasMainItem = false;
        int matches = 0;
        int matchesRequired = infusionItems.size() - 1;
        Set<Integer> takenFromSlots = new HashSet<>();

        int slot = 0;

        for(Ingredient ingredient : infusionItems) {

            if(slot == 0 && ingredient.test(input.infusionStack())) hasMainItem = true;
            else{

                if(ingredient.test(input.slot1()) && !takenFromSlots.contains(1)){
                    takenFromSlots.add(1);
                    matches++;
                }
                else if(ingredient.test(input.slot2()) && !takenFromSlots.contains(2)){
                    takenFromSlots.add(2);
                    matches++;
                }
                else if(ingredient.test(input.slot3()) && !takenFromSlots.contains(3)){
                    takenFromSlots.add(3);
                    matches++;
                }
                else if(ingredient.test(input.slot4()) && !takenFromSlots.contains(4)){
                    takenFromSlots.add(4);
                    matches++;
                }
            }


            slot++;
        }

        return matches >= matchesRequired && hasMainItem;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull InfusingRecipeInput input, HolderLookup.@NotNull Provider registries) {
        return output.orElse(ItemStack.EMPTY).copy();

    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return output.orElse(ItemStack.EMPTY).copy();
    }

    public ItemStack getOutput(){
        return output.orElse(ItemStack.EMPTY).copy();
    }

    public Map<ResourceLocation, Integer> getOutputInfusions(){return outputInfusion;}

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Registration.INFUSING_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Registration.INFUSING.get();
    }

    public static List<Ingredient> getInputIngredients(InfusingRecipe o) {
        return o.infusionItems;
    }

    public static Optional<ItemStack> getResult(InfusingRecipe o) {
        return o.output;
    }

    public static Map<ResourceLocation, Integer> getOutputInfusion(InfusingRecipe o) {
        return o.outputInfusion;
    }

    public Integer getEnergyUsed() {
        return energyReq;
    }
}
