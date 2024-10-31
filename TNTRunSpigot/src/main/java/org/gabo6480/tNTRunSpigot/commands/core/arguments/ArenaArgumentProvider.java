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

    public ArenaArgumentProvider(BiFunction<ArenaEntity, CommandContext, Boolean> arenaFilter){
        this("arena",null, arenaFilter);
    }

    @Override
    public Collection<? extends ArenaEntity> GetObjectCollection(CommandContext context, int currentArgumentIndex) {

        var manager = TNTRunSpigot.instance.getArenaManager();

        return manager.getArenas().values();
    }

    @Override
    public String GetObjectName(ArenaEntity object) {
        return object.getName();
    }
}