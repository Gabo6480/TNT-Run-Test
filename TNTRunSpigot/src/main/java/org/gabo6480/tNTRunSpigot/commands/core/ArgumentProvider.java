package org.gabo6480.tNTRunSpigot.commands.core;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ArgumentProvider {
    @NotNull
    String GetName();

    List<String> GetCompletions(CommandContext context, int currentArgumentIndex);
}
