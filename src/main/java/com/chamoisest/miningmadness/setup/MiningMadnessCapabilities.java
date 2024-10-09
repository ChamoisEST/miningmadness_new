package com.chamoisest.miningmadness.setup;

import com.chamoisest.miningmadness.common.capabilities.infusion.IInfusionStorage;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public class MiningMadnessCapabilities {

    public static final class InfusionStorage {
        public static final BlockCapability<IInfusionStorage, @Nullable Direction> BLOCK = BlockCapability.createSided(create("infusion"), IInfusionStorage.class);
        public static final ItemCapability<IInfusionStorage, @Nullable Void> ITEM = ItemCapability.createVoid(create("infusion"), IInfusionStorage.class);

        private InfusionStorage() {}
    }

    private static ResourceLocation create(String path) {
        return ResourceLocation.fromNamespaceAndPath("miningmadness", path);
    }
}
