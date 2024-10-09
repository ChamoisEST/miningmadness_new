package com.chamoisest.miningmadness.common.network.data;

import com.chamoisest.miningmadness.MiningMadness;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
public record InfusionSyncToClientPayload(
        BlockPos pos,
        int infusionId,
        int tier,
        int tierPoints
) implements CustomPacketPayload {

    public static final Type<InfusionSyncToClientPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "infusion_sync_to_client_packet"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, InfusionSyncToClientPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, InfusionSyncToClientPayload::pos,
            ByteBufCodecs.INT, InfusionSyncToClientPayload::infusionId,
            ByteBufCodecs.INT, InfusionSyncToClientPayload::tier,
            ByteBufCodecs.INT, InfusionSyncToClientPayload::tierPoints,
            InfusionSyncToClientPayload::new
    );
}
