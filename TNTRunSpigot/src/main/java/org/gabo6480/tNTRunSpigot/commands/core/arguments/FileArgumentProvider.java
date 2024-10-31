package org.gabo6480.tNTRunSpigot.commands.core.arguments;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;

public class FileArgumentProvider extends AbstractArgumentProvider<File>{

    File worldContainer;

    public FileArgumentProvider(@NotNull String name, String defaultValue, BiFunction<File, CommandContext, Boolean> filter) {
        super(name, defaultValue, filter);

        worldContainer = Bukkit.getWorldContainer();
    }

    public FileArgumentProvider(){
        this("directory",null, (w, ctx) -> true);
    }

    public FileArgumentProvider(@NotNull String name){
        this(name,null,(w, ctx) -> true);
    }

    public FileArgumentProvider(@NotNull String name, String defaultValue){
        this(name,defaultValue,(w, ctx) -> true);
    }

    public FileArgumentProvider(BiFunction<File, CommandContext, Boolean> fileFilter){
        this("directory",null, fileFilter);
    }

    @Override
    public Collection<? extends File> GetObjectCollection(CommandContext context, int currentArgumentIndex) {

        var files = worldContainer.listFiles(File::isDirectory);

        if(files == null){
            return new ArrayList<>();
        }

        return Arrays.stream(files).toList();
    }

    @Override
    public String GetObjectName(File object) {
        return worldContainer.toURI().relativize(object.toURI()).getPath().replace("/", "");
    }
}
