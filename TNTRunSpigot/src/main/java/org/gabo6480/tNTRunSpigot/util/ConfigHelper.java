package org.gabo6480.tNTRunSpigot.util;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ConfigHelper {
    public static <T> YamlConfiguration Initialize(@NotNull Class<T> clazz, @NotNull Plugin plugin, @NotNull String fileName) throws IOException, InvalidConfigurationException, IllegalAccessException {
        var file = new File(plugin.getDataFolder(), fileName);

        if(file.exists()){
            var ign1 = file.getParentFile().mkdirs();
            var ign2 = file.createNewFile();
        }

        var result = YamlConfiguration.loadConfiguration(file);

        PopulateConfigFromClass(clazz, result, "", false);

        result.save(file);

        return result;
    }

    public static <T> void PopulateConfigFromClass(@NotNull Class<T> clazz, @NotNull FileConfiguration configuration, @NotNull String path, boolean overwrite) throws IllegalAccessException {
        var name = clazz.getSimpleName();
        String newPath;
        if(path.isEmpty()) {
            newPath = name;

            var header = clazz.getAnnotation(ConfigHeader.class);
            if(header != null) configuration.options().setHeader(Arrays.stream(header.value()).toList());

            var footer = clazz.getAnnotation(ConfigFooter.class);
            if(footer != null) configuration.options().setFooter(Arrays.stream(footer.value()).toList());
        }
        else newPath = path + "." + name;

        for(var field : clazz.getFields()){
            if(!Modifier.isStatic(field.getModifiers()) || !Modifier.isPublic(field.getModifiers())) continue;

            var fieldPath = newPath + "." + field.getName();
            var value = field.get(null);
            if(overwrite || !configuration.isSet(fieldPath)) configuration.set(fieldPath, value);
            else {
                field.set(null, configuration.get(fieldPath));
            }
            configuration.addDefault(fieldPath, value);

            var fieldComments = field.getAnnotation(ConfigComments.class);
            if(fieldComments != null) configuration.setComments(fieldPath, Arrays.stream(fieldComments.value()).toList());
        }

        var comments = clazz.getAnnotation(ConfigComments.class);
        if(comments != null) configuration.setComments(newPath, Arrays.stream(comments.value()).toList());

        for(var subClazz : clazz.getClasses()){
            PopulateConfigFromClass(subClazz, configuration, newPath, overwrite);
        }
    }
}
