package com.chamoisest.miningmadness.common.network.data;

import com.chamoisest.miningmadness.MiningMadness;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record AreaDisplayButtonPayload(
        boolean toggle
) implements CustomPacketPayload {

    public static final Type<AreaDisplayButtonPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "area_display_button_packet"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, AreaDisplayButtonPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, AreaDisplayButtonPayload::toggle,
            AreaDisplayButtonPayload::new
    );
}
