package org.gabo6480.tntrunfabricclient.client.packethandlers;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.gabo6480.PluginMessageByteBuffer;

public class ActionbarPacketHandler implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        System.out.println("Packet received");

        var buffer = new PluginMessageByteBuffer(buf.nioBuffer());

        String message = buffer.readString();

        System.out.println("Message: " + message);

        client.inGameHud.setOverlayMessage(Text.literal(message), false);
    }
}
