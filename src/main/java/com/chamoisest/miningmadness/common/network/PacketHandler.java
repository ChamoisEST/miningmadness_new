package com.chamoisest.miningmadness.common.network;

import com.chamoisest.miningmadness.MiningMadness;
import com.chamoisest.miningmadness.common.network.data.*;
import com.chamoisest.miningmadness.common.network.handler.*;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {

    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MiningMadness.MODID);

        registrar.playToServer(MachineStatusPayload.TYPE, MachineStatusPayload.STREAM_CODEC, MachineStatusPacket.get()::handle);
        registrar.playToServer(RedstoneStatusPayload.TYPE, RedstoneStatusPayload.STREAM_CODEC, RedstoneStatusPacket.get()::handle);
        registrar.playToServer(AreaDisplayButtonPayload.TYPE, AreaDisplayButtonPayload.STREAM_CODEC, AreaDisplayButtonPacket.get()::handle);

        registrar.playToClient(InfusionSyncToClientPayload.TYPE, InfusionSyncToClientPayload.STREAM_CODEC, InfusionSyncToClientPacket.get()::handle);
        registrar.playToClient(EnergySyncToClientPayload.TYPE, EnergySyncToClientPayload.STREAM_CODEC, EnergySyncToClientPacket.get()::handle);
        registrar.playToClient(InfusionCraftingSyncToClientPayload.TYPE, InfusionCraftingSyncToClientPayload.STREAM_CODEC, InfusionCraftingSyncToClientPacket.get()::handle);
    }
}
