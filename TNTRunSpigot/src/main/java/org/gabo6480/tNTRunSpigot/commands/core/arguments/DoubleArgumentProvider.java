package org.gabo6480.tNTRunSpigot.commands.core.arguments;

import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;

import java.util.function.BiFunction;

public class DoubleArgumentProvider extends NumberArgumentProvider<Double> {
    public DoubleArgumentProvider(String name, Double defaultValue, BiFunction<Double, CommandContext, Boolean> numberFilter) {
        super(name, defaultValue, Double::parseDouble, numberFilter);
    }

    public DoubleArgumentProvider(String name, BiFunction<Double, CommandContext, Boolean> numberFilter) {
        super(name, null, Double::parseDouble, numberFilter);
    }

    public DoubleArgumentProvider(String name) {
        super(name, null, Double::parseDouble, (num, ctx) -> true);
    }
}