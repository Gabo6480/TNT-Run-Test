package org.gabo6480.tNTRunSpigot.util.extensions;

import org.jetbrains.annotations.NotNull;

public class StringExtension {
    public static <T> String putPlaceholder(String in, String placeholder, T value) {
        return in.replace(placeholder, value.toString());
    }
}
