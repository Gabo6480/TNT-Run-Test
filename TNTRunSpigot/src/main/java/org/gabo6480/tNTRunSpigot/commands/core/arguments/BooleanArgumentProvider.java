package org.gabo6480.tNTRunSpigot.commands.core.arguments;

import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BooleanArgumentProvider extends AbstractArgumentProvider<Boolean> {
    public BooleanArgumentProvider(@NotNull String name, Boolean defaultValue) {
        super(name, defaultValue != null ? defaultValue.toString() : null, (num, ctx) -> true);
    }

    public BooleanArgumentProvider(String name){
        this(name, null);
    }

    @Override
    public Collection<Boolean> GetObjectCollection(CommandContext context, int currentArgumentIndex) {
        return List.of(true, false);
    }

    @Override
    public String GetObjectName(Boolean object) {
        return object.toString();
    }
}
