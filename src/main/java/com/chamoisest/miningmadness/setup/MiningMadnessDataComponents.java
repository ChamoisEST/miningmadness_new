package com.chamoisest.miningmadness.setup;

import com.chamoisest.miningmadness.MiningMadness;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;
import java.util.function.UnaryOperator;

public class MiningMadnessDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MiningMadness.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MACHINE_ENERGY = register("machine_energy",
            builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MACHINE_ENERGY_CAPACITY = register("machine_energy_capacity",
            builder -> builder.persistent(Codec.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MACHINE_REDSTONE_STATUS = register("machine_redstone_status",
            builder -> builder.persistent(Codec.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Map<String, Map<String, Integer>>>> INFUSION = register("infusion",
            builder -> builder.persistent(Codec.unboundedMap(Codec.STRING, Codec.unboundedMap(Codec.STRING, Codec.INT))));

    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }
}
