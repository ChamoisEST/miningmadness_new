package com.chamoisest.miningmadness.compat.jei;

import com.chamoisest.miningmadness.MiningMadness;
import com.chamoisest.miningmadness.client.screens.elements.EnergyArea;
import com.chamoisest.miningmadness.client.screens.elements.InfusionBars;
import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import com.chamoisest.miningmadness.common.items.GemOfFocusItem;
import com.chamoisest.miningmadness.common.items.block.interfaces.InfusionItem;
import com.chamoisest.miningmadness.common.recipes.infusing.InfusingRecipe;
import com.chamoisest.miningmadness.setup.MiningMadnessRegistries;
import com.chamoisest.miningmadness.setup.MiningMadnessTags;
import com.chamoisest.miningmadness.setup.Registration;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class InfusingRecipeCategory implements IRecipeCategory<InfusingRecipe> {

    public enum InfusingCategoryType{
        INFUSION, INFUSION_CRAFTING
    }

    public static final RecipeType<InfusingRecipe> TYPE = RecipeType.create(MiningMadness.MODID, "infusing", InfusingRecipe.class);
    public static final RecipeType<InfusingRecipe> TYPE_CRAFTING = RecipeType.create(MiningMadness.MODID, "infusion_crafting", InfusingRecipe.class);

    public static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "textures/gui/infusing_station_gui.png");
    private static final String INFUSION_SLOT_NAME = "infusion_main_slot";

    private final Component title;
    private final IDrawable background;
    private final IDrawable icon;

    private final InfusingCategoryType type;

    public InfusingRecipeCategory(IGuiHelper helper, InfusingCategoryType type) {
        if(type == InfusingCategoryType.INFUSION_CRAFTING) {
            this.title = Component.translatable("miningmadness.jei.infusion_crafting_title");
        }else{
            this.title = Component.translatable("miningmadness.jei.infusing_title");
        }
        this.background = helper.createDrawable(GUI_TEXTURE, 4, 4, 167, 89);
        this.icon = helper.createDrawableItemStack(new ItemStack(Registration.INFUSING_STATION.get()));
        this.type = type;
    }

    public ItemStack focusedStack = ItemStack.EMPTY;
    public Map<ResourceLocation, Integer> outputInfusions = new HashMap<>();

    @Override
    public RecipeType<InfusingRecipe> getRecipeType() {
        return (type == InfusingCategoryType.INFUSION) ? TYPE : TYPE_CRAFTING;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(InfusingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        ItemStack currentDisplayedStack = getCurrentDisplayedStack(recipeSlotsView);
        Font font = Minecraft.getInstance().font;

        InfusionBars infusionBars = new InfusionBars(119, 16, font, currentDisplayedStack, recipe.getOutputInfusions());
        infusionBars.render(guiGraphics, (int)mouseX, (int)mouseY, 0);

        EnergyArea energyArea = new EnergyArea(149, 13, font, recipe.getEnergyUsed());
        energyArea.render(guiGraphics, (int)mouseX, (int)mouseY, 0);
    }

    public ItemStack getCurrentDisplayedStack(IRecipeSlotsView recipeSlotsView) {
        Optional<IRecipeSlotView> slotView = recipeSlotsView.findSlotByName(INFUSION_SLOT_NAME);
        if(slotView.isPresent()) {
            Optional<ItemStack> stackOptional = slotView.get().getDisplayedItemStack();
            if(stackOptional.isPresent()) {
                return stackOptional.get();
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean stackCanAcceptAnyInfusionFromRecipe(ItemStack stack) {
        for(Map.Entry<ResourceLocation, Integer> entry : outputInfusions.entrySet()) {
            Infusion infusion = MiningMadnessRegistries.INFUSIONS.get(entry.getKey());

            if(infusion != null) {
                if(stack.getItem() instanceof BlockItem item){
                    Block block = item.getBlock();
                    BlockState state = block.defaultBlockState();

                    if (infusion.getCanInfuseBlockTag().isPresent() && state.is(infusion.getCanInfuseBlockTag().get())) {
                        return true;
                    }
                }else{
                    if (infusion.getCanInfuseItemTag().isPresent() && stack.is(infusion.getCanInfuseItemTag().get())) {
                        return true;
                    }
                }
            }else{
                throw new IllegalArgumentException("Infusion with key " + entry.getKey() + " not found!");
            }
        }
        return false;
    }

    public Ingredient getRealInputIngredients(InfusingRecipe recipe) {
        List<ItemStack> allowedItems = new ArrayList<>();
        ItemStack[] inputStacks = recipe.getIngredients().getFirst().getItems();

        for(ItemStack stack : inputStacks) {
            if(stack.getItem() instanceof GemOfFocusItem){
                ItemStack checkStack = new ItemStack(Registration.INFUSING_STATION.get());
                if(stackCanAcceptAnyInfusionFromRecipe(checkStack)) allowedItems.add(stack);
            }else if(!(stack.getItem() instanceof InfusionItem) || stackCanAcceptAnyInfusionFromRecipe(stack))
                allowedItems.add(stack);

        }

        Stream<ItemStack> streamAllowedItems = Stream.of(allowedItems.toArray(new ItemStack[0]));
        return Ingredient.of(streamAllowedItems);
    }

    @Override
    public boolean isHandled(InfusingRecipe recipe) {
        if(type == InfusingCategoryType.INFUSION_CRAFTING && !recipe.getOutputInfusions().isEmpty()) return false;
        if(type == InfusingCategoryType.INFUSION && recipe.getOutputInfusions().isEmpty()) return false;
        return true;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InfusingRecipe recipe, IFocusGroup focuses) {

        this.outputInfusions = recipe.getOutputInfusions();

        Stream<IFocus<ItemStack>> focusStream = focuses.getItemStackFocuses();
        Optional<IFocus<ItemStack>> focus = focusStream.findFirst();
        focus.ifPresent(itemStackIFocus -> this.focusedStack = itemStackIFocus.getTypedValue().getIngredient());

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        Ingredient realInputIngredient = getRealInputIngredients(recipe);
        IRecipeSlotBuilder infusionSlot = builder.addSlot(RecipeIngredientRole.INPUT, 40, 37).addIngredients(realInputIngredient).setSlotName(INFUSION_SLOT_NAME);

        int slot = 0;
        for(int i = 0; i < ingredients.size(); i++) {
            if(slot == 0) {
                slot++;
                continue;
            }
            else{
                if(slot < ingredients.size()){
                    if(slot == 1) builder.addSlot(RecipeIngredientRole.INPUT, 40, 12).addIngredients(recipe.getIngredients().get(slot));
                    else if (slot == 2) builder.addSlot(RecipeIngredientRole.INPUT, 65, 37).addIngredients(recipe.getIngredients().get(slot));
                    else if (slot == 3) builder.addSlot(RecipeIngredientRole.INPUT, 40, 62).addIngredients(recipe.getIngredients().get(slot));
                    else if (slot == 4) builder.addSlot(RecipeIngredientRole.INPUT, 15, 37).addIngredients(recipe.getIngredients().get(slot));
                    else throw new IllegalArgumentException("Infusion recipe slot " + slot + " not supported!");
                }
            }
            slot++;
        }

        ItemStack output = recipe.getOutput();
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 62);

        if(output == ItemStack.EMPTY){
            outputSlot.addIngredients(realInputIngredient);
            if(this.focusedStack != ItemStack.EMPTY && this.focusedStack.is(MiningMadnessTags.Items.INFUSABLE)){
                builder.createFocusLink(infusionSlot, outputSlot);
            }
        }else{
            outputSlot.addItemStack(output);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, InfusingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {

    }
}
