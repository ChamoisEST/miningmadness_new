package com.chamoisest.miningmadness.common.network.data;

import com.chamoisest.miningmadness.MiningMadness;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MachineStatusPayload(
        int status
) implements CustomPacketPayload {

    public static final Type<MachineStatusPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "machine_status_packet"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, MachineStatusPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, MachineStatusPayload::status,
            MachineStatusPayload::new
    );
}
