package org.gabo6480.tNTRunSpigot.commands.core.arguments;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class GameRuleValueArgumentProvider extends AbstractArgumentProvider<GameRule<?>> {
    public GameRuleValueArgumentProvider(@NotNull String name, Boolean defaultValue) {
        super(name, defaultValue != null ? defaultValue.toString() : null, (num, ctx) -> true);
    }

    public GameRuleValueArgumentProvider(@NotNull String name){
        this(name, null);
    }
    public GameRuleValueArgumentProvider(){
        this("value", null);
    }

    @Override
    public Collection<GameRule<?>> getObjectCollection(CommandContext context, int currentArgumentIndex) {
        return Arrays.stream(GameRule.values()).toList();
    }

    @Override
    public String GetObjectName(GameRule<?> object) {
        return object.getName();
    }

    @Override
    public @Nullable String GetDefaultValue(CommandContext context) {
        var gamerule = Objects.requireNonNull(context.getPreviousArgument()).toString();


        return
    }
}
