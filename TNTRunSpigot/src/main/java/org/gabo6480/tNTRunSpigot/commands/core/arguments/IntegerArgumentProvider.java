package org.gabo6480.tNTRunSpigot.commands.core.arguments;

import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;

import java.util.function.BiFunction;

public class IntegerArgumentProvider extends NumberArgumentProvider<Integer> {
    public IntegerArgumentProvider(String name, Integer defaultValue, BiFunction<Integer, CommandContext, Boolean> numberFilter) {
        super(name, defaultValue, Integer::parseInt, numberFilter);
    }

    public IntegerArgumentProvider(String name, BiFunction<Integer, CommandContext, Boolean> numberFilter) {
        super(name, null, Integer::parseInt, numberFilter);
    }

    public IntegerArgumentProvider(String name, Integer defaultValue ) {
        super(name, defaultValue, Integer::parseInt, (i, ctx) -> true);
    }

    public IntegerArgumentProvider(String name) {
        super(name, null, Integer::parseInt, (num, ctx) -> true);
    }
}
