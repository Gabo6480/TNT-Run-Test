package org.gabo6480.tNTRunSpigot.commands.core.arguments;

import org.gabo6480.tNTRunSpigot.TNTRunSpigot;
import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.gabo6480.tNTRunSpigot.entities.ArenaEntity;
import org.gabo6480.tNTRunSpigot.repositories.ArenaRepository;
import org.gabo6480.tNTRunSpigot.repositories.ArenaRepository_;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.BiFunction;

public class ArenaArgumentProvider extends AbstractArgumentProvider<ArenaEntity>{
    public ArenaArgumentProvider(@NotNull String name, String defaultValue, BiFunction<ArenaEntity, CommandContext, Boolean> filter) {
        super(name, defaultValue, filter);
    }

    public ArenaArgumentProvider(){
        this("arena",null, (w, ctx) -> true);
    }

    public ArenaArgumentProvider(@NotNull String name){
        this(name,null,(w, ctx) -> true);
    }

    public ArenaArgumentProvider(@NotNull String name, String defaultValue){
        this(name,defaultValue,(w, ctx) -> true);
    }

    public ArenaArgumentProvider(BiFunction<ArenaEntity, CommandContext, Boolean> fileFilter){
        this("arena",null, fileFilter);
    }

    @Override
    public Collection<? extends ArenaEntity> getObjectCollection(CommandContext context, int currentArgumentIndex) {

        var session = TNTRunSpigot.instance.getSessionFactory().openStatelessSession();

        ArenaRepository repo = new ArenaRepository_(session);

        var arenas = repo.findAll().toList();

        session.close();

        return arenas;
    }

    @Override
    public String GetObjectName(ArenaEntity object) {
        return object.getName();
    }
}