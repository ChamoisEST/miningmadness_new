package com.chamoisest.miningmadness.common.network.data;

import com.chamoisest.miningmadness.MiningMadness;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public record EnergySyncToClientPayload(
    BlockPos pos,
    int storedEnergy,
    int energyCapacity,
    int energyUsage
) implements CustomPacketPayload {

    public static final Type<EnergySyncToClientPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MiningMadness.MODID, "energy_sync_to_client_packet"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, EnergySyncToClientPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, EnergySyncToClientPayload::pos,
            ByteBufCodecs.INT, EnergySyncToClientPayload::storedEnergy,
            ByteBufCodecs.INT, EnergySyncToClientPayload::energyCapacity,
            ByteBufCodecs.INT, EnergySyncToClientPayload::energyUsage,
            EnergySyncToClientPayload::new
    );
}
