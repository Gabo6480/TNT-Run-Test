package org.gabo6480.tNTRunSpigot.commands.core.arguments;

import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ListStringArgumentProvider extends AbstractArgumentProvider<String>{

    List<String> values;

    public ListStringArgumentProvider(String name, String defaultValue, String... values){
        this(name, defaultValue, Arrays.stream(values).toList());
    }

    public ListStringArgumentProvider(String name, List<String> values){
        this(name, null, values);
    }

    public ListStringArgumentProvider(String name, String defaultValue, List<String> values){
        super(name, defaultValue, ((s, context) -> true));
        this.values = new ArrayList<>(values);
    }

    @Override
    public Collection<? extends String> GetObjectCollection(CommandContext context, int currentArgumentIndex) {
        return values;
    }

    @Override
    public String GetObjectName(String object) {
        return object;
    }
}
