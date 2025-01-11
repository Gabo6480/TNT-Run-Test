package org.gabo6480.tNTRunSpigot;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.Getter;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.CommandListenerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.gabo6480.tNTRunSpigot.commands.CommandRoot;
import org.gabo6480.tNTRunSpigot.entities.ArenaEntity;
import org.gabo6480.tNTRunSpigot.listeners.EventListenerTemplate;
import org.gabo6480.tNTRunSpigot.managers.ArenaManager;
import org.gabo6480.tNTRunSpigot.util.TranslationUtil;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.util.Objects;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;


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

        try {
            TranslationUtil.Initialize(this);
        } catch (IOException | IllegalAccessException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }



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

        Bukkit.getPluginManager().addPermission(new Permission("A"));


        var mainCommand = this.getCommand(CommandRoot.command);

        assert mainCommand != null;

        var tabExecutor = new CommandRoot(this);

        mainCommand.setExecutor(tabExecutor);
        mainCommand.setTabCompleter(tabExecutor);

        var dispatcher = ((CraftServer) Bukkit.getServer()).getServer().vanillaCommandDispatcher;

        dispatcher.a().register((LiteralArgumentBuilder<CommandListenerWrapper>) ((LiteralArgumentBuilder) CommandDispatcher.a("foo"))
                        .then(
                                argument("bar", integer())
                                        .executes(c -> {
                                            System.out.println("Bar is " + getInteger(c, "bar"));
                                            return 1;
                                        })
                        )
                        .executes(c -> {
                            System.out.println("Called foo with no arguments");
                            return 1;
                        }));
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
