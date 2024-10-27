package org.gabo6480.tntrunfabricclient.client.event;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.Identifier;
import org.gabo6480.tntrunfabricclient.client.packethandlers.ActionbarPacketHandler;

public class PlayConnectionHandler implements ClientPlayConnectionEvents.Init,  ClientPlayConnectionEvents.Disconnect{

    static final Identifier actionbar = new Identifier("gabo6480", "actionbar".toLowerCase());

    @Override
    public void onPlayInit(ClientPlayNetworkHandler handler, MinecraftClient client) {
        //TODO: Agregar chequeo si el servidor tiene el plugin

        ClientPlayNetworking.registerReceiver(actionbar,
                new ActionbarPacketHandler()
        );
    }


    @Override
    public void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        ClientPlayNetworking.unregisterReceiver(actionbar);
    }
}
