package org.gabo6480.tNTRunSpigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.gabo6480.PluginMessageByteBuffer;
import org.gabo6480.tNTRunSpigot.TNTRunSpigot;
import org.gabo6480.tNTRunSpigot.entities.arena.ArenaState;

import java.util.Random;

public class ArenaPlayerEvents extends EventListenerTemplate{
    public ArenaPlayerEvents(TNTRunSpigot plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();

        String message = event.getMessage() + " - " + new Random().nextInt();

        System.out.println("Received message: "+message);

        var buffer = new PluginMessageByteBuffer();

        buffer.writeString(message);

        player.sendPluginMessage(TNTRunSpigot.instance, TNTRunSpigot.actionbarChannel, buffer.asByteArray());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player victim)) return;

        final var arena = this.arenaManager.GetArena(victim.getWorld());

        if (arena == null) {
            return;
        }

        var arenaPlayer = arena.GetArenaPlayer(victim);

        if (arenaPlayer == null) {
            return;
        }

        e.setDamage(0);

        switch (e.getCause()) {
            case DROWNING, FALL, BLOCK_EXPLOSION, ENTITY_EXPLOSION -> e.setCancelled(true);

            case VOID -> {
                e.setCancelled(true);

                victim.teleport(arena.GetWaitingRoomLocation());

                if (!arena.IsArenaState(ArenaState.IN_GAME)) {
                    return;
                }

                if (!arenaPlayer.IsSpectator()) {
                    arena.PlayerDied(arenaPlayer);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event){
        Player player = event.getPlayer();

        var world = event.getFrom().getWorld();

        if(world == null || event.getTo() == null || event.getTo().getWorld() == null) return;

        final var arena = this.arenaManager.GetArena(world);

        if (arena == null) {
            return;
        }

        if(arena.getUUID().equals(event.getTo().getWorld().getUID())){
            return;
        }

        var arenaPlayer = arena.GetArenaPlayer(player);

        if (arenaPlayer == null) {
            return;
        }

        arena.PlayerLeft(arenaPlayer);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();

        final var arena = this.arenaManager.GetArena(player.getWorld());

        if (arena == null) {
            return;
        }

        var arenaPlayer = arena.GetArenaPlayer(player);

        if (arenaPlayer == null) {
            return;
        }

        arena.TeleportToLobby(arenaPlayer);
    }
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event){
        Player player = event.getPlayer();

        final var arena = this.arenaManager.GetArena(player.getWorld());

        if (arena == null) {
            return;
        }

        var arenaPlayer = arena.GetArenaPlayer(player);

        if (arenaPlayer == null) {
            return;
        }

        arena.TeleportToLobby(arenaPlayer);
    }
}
