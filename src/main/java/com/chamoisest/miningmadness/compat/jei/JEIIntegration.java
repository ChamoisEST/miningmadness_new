package com.chamoisest.miningmadness.compat.jei;

import com.chamoisest.miningmadness.MiningMadness;
import com.chamoisest.miningmadness.common.recipes.infusing.InfusingRecipe;
import com.chamoisest.miningmadness.setup.Registration;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class JEIIntegration implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "jei_integration");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new InfusingRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<InfusingRecipe> infusingRecipes = recipeManager.getAllRecipesFor(Registration.INFUSING.get()).stream().map(RecipeHolder::value).toList();

        registration.addRecipes(InfusingRecipeCategory.TYPE, infusingRecipes);
    }


}
