package org.gabo6480.tNTRunSpigot;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.gabo6480.tNTRunSpigot.commands.CommandRoot;
import org.gabo6480.tNTRunSpigot.entities.ArenaEntity;
import org.gabo6480.tNTRunSpigot.listeners.EventListenerTemplate;
import org.gabo6480.tNTRunSpigot.managers.ArenaManager;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Objects;

public final class TNTRunSpigot extends JavaPlugin {

    static public TNTRunSpigot instance;
    final static public String actionbarChannel = "gabo6480:actionbar";

    @Getter
    public Location lobby;

    @Getter
    private SessionFactory sessionFactory;

    @Getter
    private ArenaManager arenaManager;

    public TNTRunSpigot() {
        instance = this;
    }


    @Override
    public void onEnable() {
        System.out.println("TNTRun Plugin Enabled");

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, actionbarChannel);

        //We ensure the file exists in the plugin
        this.saveResource("hibernate.properties", false);

        sessionFactory = new Configuration()
                .setProperty("hibernate.connection.url", "jdbc:sqlite:"+ this.getDataFolder().getAbsolutePath() + "/plugin.db")
                .addAnnotatedClass(ArenaEntity.class)
                //.configure()
                .buildSessionFactory();



        arenaManager = new ArenaManager();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            this.lobby = Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation();
            arenaManager.LoadAllArenas();
        }, 1);

        EventListenerTemplate.registerEvents(this);




        var mainCommand = this.getCommand(CommandRoot.command);

        assert mainCommand != null;

        var tabExecutor = new CommandRoot(this);

        mainCommand.setExecutor(tabExecutor);
        mainCommand.setTabCompleter(tabExecutor);
    }

    @Override
    public void onDisable() {

        var session = TNTRunSpigot.instance.getSessionFactory().openStatelessSession();

        arenaManager.UnloadAllArenas();

        if (this.sessionFactory != null) {
            this.sessionFactory.close();
        }
    }
}
