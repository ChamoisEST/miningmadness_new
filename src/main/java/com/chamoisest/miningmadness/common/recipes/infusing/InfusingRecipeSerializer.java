package com.chamoisest.miningmadness.common.recipes.infusing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.HashMap;

public class InfusingRecipeSerializer implements RecipeSerializer<InfusingRecipe> {

    public static final MapCodec<InfusingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.listOf(2, 5).fieldOf("ingredients").forGetter(InfusingRecipe::getInputIngredients),
            ItemStack.CODEC.optionalFieldOf("output").forGetter(InfusingRecipe::getResult),
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).fieldOf("output_infusion").forGetter(InfusingRecipe::getOutputInfusion),
            Codec.INT.fieldOf("energy_used").forGetter(InfusingRecipe::getEnergyUsed)
    ).apply(inst, InfusingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, InfusingRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list(5)), InfusingRecipe::getInputIngredients,
                    ItemStack.STREAM_CODEC.apply(ByteBufCodecs::optional), InfusingRecipe::getResult,
                    ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.INT), InfusingRecipe::getOutputInfusion,
                    ByteBufCodecs.INT, InfusingRecipe::getEnergyUsed,
                    InfusingRecipe::new
            );

    @Override
    public MapCodec<InfusingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, InfusingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
