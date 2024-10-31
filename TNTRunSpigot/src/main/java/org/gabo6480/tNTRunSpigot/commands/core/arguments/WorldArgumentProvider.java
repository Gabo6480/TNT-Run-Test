package org.gabo6480.tNTRunSpigot.commands.core.arguments;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.BiFunction;

public class WorldArgumentProvider extends AbstractArgumentProvider<World>{

    public WorldArgumentProvider(@NotNull String name, String defaultValue, BiFunction<World, CommandContext, Boolean> filter) {
        super(name, defaultValue, filter);
    }

    public WorldArgumentProvider(){
        super("world",null, (w, ctx) -> true);
    }

    public WorldArgumentProvider(@NotNull String name){
        super(name,null,(w, ctx) -> true);
    }

    public WorldArgumentProvider(@NotNull String name, String defaultValue){
        super(name,defaultValue,(w, ctx) -> true);
    }

    public WorldArgumentProvider(BiFunction<World, CommandContext, Boolean> warpFilter){
        super("world",null, warpFilter);
    }

    @Override
    public Collection<? extends World> GetObjectCollection(CommandContext context, int currentArgumentIndex) {
        return Bukkit.getWorlds();
    }

    @Override
    public String GetObjectName(World object) {
        return object.getName();
    }
}
