package com.chamoisest.miningmadness.common.network.data;

import com.chamoisest.miningmadness.MiningMadness;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RedstoneStatusPayload(
        int status
) implements CustomPacketPayload {

    public static final Type<RedstoneStatusPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "redstone_status_packet"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RedstoneStatusPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RedstoneStatusPayload::status,
            RedstoneStatusPayload::new
    );
}
