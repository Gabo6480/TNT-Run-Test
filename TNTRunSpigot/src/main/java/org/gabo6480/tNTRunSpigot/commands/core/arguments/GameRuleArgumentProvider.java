package org.gabo6480.tNTRunSpigot.commands.core.arguments;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GameRuleArgumentProvider extends AbstractArgumentProvider<GameRule<?>> {
    public GameRuleArgumentProvider(@NotNull String name, Boolean defaultValue) {
        super(name, defaultValue != null ? defaultValue.toString() : null, (num, ctx) -> true);
    }

    public GameRuleArgumentProvider(@NotNull String name){
        this(name, null);
    }
    public GameRuleArgumentProvider(){
        this("gamerule", null);
    }

    @Override
    public Collection<GameRule<?>> getObjectCollection(CommandContext context, int currentArgumentIndex) {
        return Arrays.stream(GameRule.values()).toList();
    }

    @Override
    public String GetObjectName(GameRule<?> object) {
        return object.getName();
    }
}
