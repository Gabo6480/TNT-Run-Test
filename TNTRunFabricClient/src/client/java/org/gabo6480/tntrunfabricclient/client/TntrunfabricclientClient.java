package org.gabo6480.tntrunfabricclient.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.gabo6480.tntrunfabricclient.client.event.PlayConnectionHandler;

public class TntrunfabricclientClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PlayConnectionHandler handler = new PlayConnectionHandler();



        ClientPlayConnectionEvents.INIT.register(handler);
        ClientPlayConnectionEvents.DISCONNECT.register(handler);
    }
}
