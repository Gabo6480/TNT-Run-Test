package org.gabo6480.tNTRunSpigot.commands.core.arguments;

import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class NumberArgumentProvider <N extends Number> extends AbstractArgumentProvider<N> {

    Function<String, N> parseFunction;

    public NumberArgumentProvider(@NotNull String name, N defaultValue, Function<String, N> parseFunction, BiFunction<N, CommandContext, Boolean> numberFilter) {
        super(name, defaultValue != null ? defaultValue.toString() : null, numberFilter);
        this.parseFunction = parseFunction;
    }

    public NumberArgumentProvider(String name, Function<String, N> parseFunction, BiFunction<N, CommandContext, Boolean> numberFilter){
        this(name, null, parseFunction, numberFilter);
    }

    public NumberArgumentProvider(String name, Function<String, N> parseFunction){
        this(name, null, parseFunction, (num, ctx) -> true);
    }

    @Override
    public Collection<? extends N> getObjectCollection(CommandContext context, int currentArgumentIndex) {

        List<N> completions = new ArrayList<>();

        String valueString = Objects.requireNonNull(context.getArguments().get(currentArgumentIndex)).toString();

        N value;

        try {
            value = parseFunction.apply(valueString);

            for (int i = 0; i < 10; i++) {
                completions.add(parseFunction.apply(valueString + i));
            }

            if(value instanceof Double || value instanceof Float)
                for (int i = 0; i < 10; i++) {
                    completions.add(parseFunction.apply(valueString + "." + i));
                }
        }
        catch (Exception ignored){}

        return completions;
    }

    @Override
    public String GetObjectName(N object) {
        return object.toString();
    }
}
