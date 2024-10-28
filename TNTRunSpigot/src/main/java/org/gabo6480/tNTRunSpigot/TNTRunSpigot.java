package org.gabo6480.tNTRunSpigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.gabo6480.tNTRunSpigot.commands.CommandRoot;
import org.gabo6480.tNTRunSpigot.listeners.PlayerMessageListener;

public final class TNTRunSpigot extends JavaPlugin {

    static public TNTRunSpigot instance;
    final static public String actionbarChannel = "gabo6480:actionbar";

    public TNTRunSpigot() {
        instance = this;
    }


    @Override
    public void onEnable() {
        System.out.println("TNTRun Plugin Enabled");

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, actionbarChannel);
        Bukkit.getPluginManager().registerEvents(new PlayerMessageListener(), this);

        var mainCommand = this.getCommand(CommandRoot.command);

        assert mainCommand != null;

        var tabExecutor = new CommandRoot();

        mainCommand.setExecutor(tabExecutor);
        mainCommand.setTabCompleter(tabExecutor);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
