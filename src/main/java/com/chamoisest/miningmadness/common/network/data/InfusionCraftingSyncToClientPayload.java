package com.chamoisest.miningmadness.common.network.data;

import com.chamoisest.miningmadness.MiningMadness;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record InfusionCraftingSyncToClientPayload(
        BlockPos pos,
        int recipeEnergyNeeded,
        int recipeEnergyNeededLeft,
        int useItemsInSlotsFlags,
        Map<ResourceLocation, Integer> outputInfusions,
        boolean isInfusing
) implements CustomPacketPayload {

    public static final Type<InfusionCraftingSyncToClientPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "infusion_crafting_sync_to_client_packet"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, InfusionCraftingSyncToClientPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, InfusionCraftingSyncToClientPayload::pos,
            ByteBufCodecs.INT, InfusionCraftingSyncToClientPayload::recipeEnergyNeeded,
            ByteBufCodecs.INT, InfusionCraftingSyncToClientPayload::recipeEnergyNeededLeft,
            ByteBufCodecs.INT, InfusionCraftingSyncToClientPayload::useItemsInSlotsFlags,
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.INT), InfusionCraftingSyncToClientPayload::outputInfusions,
            ByteBufCodecs.BOOL, InfusionCraftingSyncToClientPayload::isInfusing,
            InfusionCraftingSyncToClientPayload::new
    );
}
