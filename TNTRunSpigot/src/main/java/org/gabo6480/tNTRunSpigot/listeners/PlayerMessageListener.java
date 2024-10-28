package org.gabo6480.tNTRunSpigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.gabo6480.PluginMessageByteBuffer;
import org.gabo6480.tNTRunSpigot.TNTRunSpigot;

import java.util.Random;

public class PlayerMessageListener implements Listener {

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();

        String message = event.getMessage() + " - " + new Random().nextInt();

        System.out.println("Received message: "+message);

        var buffer = new PluginMessageByteBuffer();

        buffer.writeString(message);

        player.sendPluginMessage(TNTRunSpigot.instance, TNTRunSpigot.actionbarChannel, buffer.asByteArray());
    }
}
