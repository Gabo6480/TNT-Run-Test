package org.gabo6480.tNTRunSpigot.commands.core.arguments;

import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.gabo6480.tNTRunSpigot.commands.core.OptionalArgumentProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public abstract class AbstractArgumentProvider<T> implements OptionalArgumentProvider {
    String name;
    String defaultValue;
    BiFunction<T, CommandContext, Boolean> filter;

    public AbstractArgumentProvider(@NotNull String name, String defaultValue, BiFunction<T, CommandContext, Boolean> filter){
        this.name = name;
        this.filter = filter;
        this.defaultValue = defaultValue;
    }


    @Override
    public @NotNull String GetName() {
        return name;
    }

    @Override
    public List<String> GetCompletions(CommandContext context, int currentArgumentIndex) {
        return GetObjectCollection(context, currentArgumentIndex).stream()
                .filter(t -> filter.apply(t, context))
                .map(this::GetObjectName).toList();
    }

    public abstract Collection<? extends T> GetObjectCollection(CommandContext context, int currentArgumentIndex);
    public abstract String GetObjectName(T object);

    @Override
    public @Nullable String GetDefaultValue(CommandContext context) {
        return defaultValue;
    }
}
