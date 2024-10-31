package org.gabo6480.tNTRunSpigot.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.gabo6480.tNTRunSpigot.TNTRunSpigot;
import org.gabo6480.tNTRunSpigot.managers.ArenaManager;

public abstract class EventListenerTemplate implements Listener {
    protected final TNTRunSpigot plugin;
    protected final ArenaManager arenaManager;

    public EventListenerTemplate(final TNTRunSpigot plugin) {
        this.plugin = plugin;

        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);

        arenaManager = this.plugin.getArenaManager();
    }

    public static void registerEvents(final TNTRunSpigot plugin) {
        new ArenaProtectionEvents(plugin);
        new ArenaPlayerEvents(plugin);
    }

    protected final boolean IsInArena(Player player){
        return this.arenaManager.IsInArena(player);
    }

    protected final boolean IsArena(World world){
        return this.arenaManager.IsArena(world);
    }
}
