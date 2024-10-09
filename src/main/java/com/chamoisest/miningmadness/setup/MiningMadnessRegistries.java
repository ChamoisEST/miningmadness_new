package com.chamoisest.miningmadness.setup;

import com.chamoisest.miningmadness.MiningMadness;
import com.chamoisest.miningmadness.common.capabilities.infusion.infusions.base.Infusion;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class MiningMadnessRegistries {
    public static final Registry<Infusion> INFUSIONS = new RegistryBuilder<>(Keys.INFUSIONS).sync(true).create();

    public static final class Keys {
        public static final ResourceKey<Registry<Infusion>> INFUSIONS = key("infusions");

        private static <T> ResourceKey<Registry<T>> key(String name) {
            return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, name));
        }
    }
}
