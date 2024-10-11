package com.chamoisest.miningmadness.common.network.data;

import com.chamoisest.miningmadness.MiningMadness;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RangeButtonPayload(
        int buttonTypeEnumInt,
        int areaPosEnumInt
) implements CustomPacketPayload {

    public static final Type<RangeButtonPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "range_button_packet"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RangeButtonPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RangeButtonPayload::buttonTypeEnumInt,
            ByteBufCodecs.INT, RangeButtonPayload::areaPosEnumInt,
            RangeButtonPayload::new
    );
}
