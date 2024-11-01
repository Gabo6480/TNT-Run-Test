package org.gabo6480.tNTRunSpigot.util;

import org.bukkit.entity.Player;
import org.gabo6480.PluginMessageByteBuffer;
import org.gabo6480.tNTRunSpigot.TNTRunSpigot;
import org.jetbrains.annotations.NotNull;

public class PacketUtil {
    public static <T> void SendPacketToPlayer(@NotNull Player player, @NotNull T message){
        var buffer = new PluginMessageByteBuffer();

        buffer.writeObject(message);

        player.sendPluginMessage(TNTRunSpigot.instance, TNTRunSpigot.actionbarChannel, buffer.asByteArray());
    }

    public static void SendPacketToPlayer(@NotNull Player player, @NotNull String message){
        var buffer = new PluginMessageByteBuffer();

        buffer.writeString(message);

        player.sendPluginMessage(TNTRunSpigot.instance, TNTRunSpigot.actionbarChannel, buffer.asByteArray());
    }
}
