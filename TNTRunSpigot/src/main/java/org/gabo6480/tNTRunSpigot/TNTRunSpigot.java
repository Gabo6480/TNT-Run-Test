package org.gabo6480.tNTRunSpigot;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.gabo6480.tNTRunSpigot.commands.CommandRoot;
import org.gabo6480.tNTRunSpigot.entities.ArenaEntity;
import org.gabo6480.tNTRunSpigot.listeners.PlayerMessageListener;
import org.gabo6480.tNTRunSpigot.repositories.ArenaRepository;
import org.gabo6480.tNTRunSpigot.repositories.ArenaRepository_;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class TNTRunSpigot extends JavaPlugin {

    static public TNTRunSpigot instance;
    final static public String actionbarChannel = "gabo6480:actionbar";

    @Getter
    private SessionFactory sessionFactory;

    public TNTRunSpigot() {
        instance = this;
    }


    @Override
    public void onEnable() {
        System.out.println("TNTRun Plugin Enabled");

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, actionbarChannel);
        Bukkit.getPluginManager().registerEvents(new PlayerMessageListener(), this);

        //We ensure the file exists in the plugin
        this.saveResource("hibernate.properties", false);

        sessionFactory = new Configuration()
                .setProperty("hibernate.connection.url", "jdbc:sqlite:"+ this.getDataFolder().getAbsolutePath() + "/plugin.db")
                .addAnnotatedClass(ArenaEntity.class)
                //.configure()
                .buildSessionFactory();

        var mainCommand = this.getCommand(CommandRoot.command);

        assert mainCommand != null;

        var tabExecutor = new CommandRoot();

        mainCommand.setExecutor(tabExecutor);
        mainCommand.setTabCompleter(tabExecutor);

        var session = TNTRunSpigot.instance.getSessionFactory().openStatelessSession();
        ArenaRepository repo = new ArenaRepository_(session);

        repo.findAll().forEach(arenaEntity -> {
            arenaEntity.LoadArena(repo);
        });

        session.close();
    }

    @Override
    public void onDisable() {

        var session = TNTRunSpigot.instance.getSessionFactory().openStatelessSession();

        ArenaRepository repo = new ArenaRepository_(session);

        repo.findAll().forEach(ArenaEntity::UnloadArena);

        session.close();

        if (this.sessionFactory != null) {
            this.sessionFactory.close();
        }
    }
}
