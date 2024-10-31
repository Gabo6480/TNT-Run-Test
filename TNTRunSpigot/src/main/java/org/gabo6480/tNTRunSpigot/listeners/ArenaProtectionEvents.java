package org.gabo6480.tNTRunSpigot.listeners;

import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.gabo6480.PluginMessageByteBuffer;
import org.gabo6480.tNTRunSpigot.TNTRunSpigot;
import org.gabo6480.tNTRunSpigot.entities.arena.ArenaState;

import java.util.Random;

public class ArenaProtectionEvents extends EventListenerTemplate {

    public ArenaProtectionEvents(TNTRunSpigot plugin) {
        super(plugin);
    }
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (this.IsInArena(player)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onItemMove(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (this.IsInArena(player)) {
            event.setResult(Event.Result.DENY);
        }
    }
    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        final var player = event.getPlayer();

        if (!this.IsInArena(player)) {
            return;
        }

        var block = event.getClickedBlock();

        if (block != null && block.getType().isInteractable()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if(event.getEntity() instanceof FallingBlock fallingBlock && event.getBlock().getType().equals(Material.AIR)){
            if (this.IsArena(event.getBlock().getWorld())) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if(event.getEntity() instanceof TNTPrimed tntPrimed){
            if (this.IsArena(tntPrimed.getWorld())) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (this.IsArena(event.getEntity().getWorld())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (this.IsArena(event.getBlock().getWorld())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (this.IsInArena(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        if (this.IsInArena(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.IsInArena(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.IsInArena(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
