package org.gabo6480.tNTRunSpigot.commands.core.arguments;

import org.bukkit.Bukkit;
import org.gabo6480.tNTRunSpigot.TNTRunSpigot;
import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.gabo6480.tNTRunSpigot.entities.LobbyEntity;
import org.gabo6480.tNTRunSpigot.repositories.LobbyRepository;
import org.gabo6480.tNTRunSpigot.repositories.LobbyRepository_;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiFunction;

public class LobbyArgumentProvider  extends AbstractArgumentProvider<LobbyEntity>{
    public LobbyArgumentProvider(@NotNull String name, String defaultValue, BiFunction<LobbyEntity, CommandContext, Boolean> filter) {
        super(name, defaultValue, filter);
    }

    public LobbyArgumentProvider(){
        this("lobby",null, (w, ctx) -> true);
    }

    public LobbyArgumentProvider(@NotNull String name){
        this(name,null,(w, ctx) -> true);
    }

    public LobbyArgumentProvider(@NotNull String name, String defaultValue){
        this(name,defaultValue,(w, ctx) -> true);
    }

    public LobbyArgumentProvider(BiFunction<LobbyEntity, CommandContext, Boolean> fileFilter){
        this("lobby",null, fileFilter);
    }

    @Override
    public Collection<? extends LobbyEntity> getObjectCollection(CommandContext context, int currentArgumentIndex) {

        var session = TNTRunSpigot.instance.getSessionFactory().openStatelessSession();

        LobbyRepository repo = new LobbyRepository_(session);

        var lobbies = repo.findAll().toList();

        session.close();

        return lobbies;
    }

    @Override
    public String GetObjectName(LobbyEntity object) {
        return object.getName();
    }
}