package com.chamoisest.miningmadness.common.network.data;

import com.chamoisest.miningmadness.MiningMadness;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RangeProjectorSyncToClientPayload(
        BlockPos pos,
        boolean isConnected,
        int radX,
        int radY,
        int radZ,
        BlockPos offset
) implements CustomPacketPayload {

    public static final Type<RangeProjectorSyncToClientPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "range_projector_sync_to_client_packet"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RangeProjectorSyncToClientPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, RangeProjectorSyncToClientPayload::pos,
            ByteBufCodecs.BOOL, RangeProjectorSyncToClientPayload::isConnected,
            ByteBufCodecs.INT, RangeProjectorSyncToClientPayload::radX,
            ByteBufCodecs.INT, RangeProjectorSyncToClientPayload::radY,
            ByteBufCodecs.INT, RangeProjectorSyncToClientPayload::radZ,
            BlockPos.STREAM_CODEC, RangeProjectorSyncToClientPayload::offset,
            RangeProjectorSyncToClientPayload::new
    );
}
