package org.gabo6480.tNTRunSpigot.util;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@ConfigHeader("This file stores all the messages and texts used dynamically in the plugin")
public class TranslationUtil{

    private static YamlConfiguration configuration;
    private static final String FILE_NAME = "translation.yaml";

    /*interface Translation {
        @NotNull
        public String getValue();
    }*/

    public static void Initialize(@NotNull Plugin plugin) throws IOException, InvalidConfigurationException, IllegalAccessException {
        configuration = ConfigHelper.Initialize(TranslationUtil.class, plugin, FILE_NAME);
    }

    public static void Save() throws IllegalAccessException, IOException {
        ConfigHelper.PopulateConfigFromClass(TranslationUtil.class, configuration, "", true);
        configuration.save(FILE_NAME);
    }

    public static class Arena {

        @ConfigComments("Placeholders: %arena% is the arena's name,  %currentPlayers% is the current player count in the arena, %minPlayers% is the arena's minimum players needed")
        public static String welcome_message = "§3[§1%arena%§3] Current players %currentPlayers%/%minPlayers%";
        public static String congratulations = "CONGRATULATIONS!";
        public static String you_won = "§6YOU WON!";
        public static String run = "§4RUN!";

        public static class BossBar {
            @ConfigComments("Placeholders: %playerCount% is the current amount of players, %minPlayers% is the minimum players needed to begin the game")
            public static String waiting_for_players = "Waiting for players %aliveCount%/%minPlayers%";
            public static String starting_game = "Starting game!";
            @ConfigComments("Placeholders: %aliveCount% is the current amount of players alive, %playerCount% is the total players who began the game")
            public static String in_game = "RUN! %aliveCount%/%playerCount% remaining!";
            @ConfigComments("%player% is a placeholder for the player's name")
            public static String player_won = "%player% is the Winner!";
        }

        public static class State {
            public static String WAITING_FOR_PLAYERS  = "§b§lWaiting for players§r";
            public static String GAME_STARTING  = "§a§lGame starting§r";
            public static String IN_GAME = "§2§lIn game§r";
            public static String ENDING = "§5§lEnding§r";
            public static String RESTARTING = "§e§lRestarting§r";
            public static String ERROR = "§4§lError§r";
            public static String UNLOADED = "§7§lUnloaded§r";
        }

        @ConfigComments("%player% is a placeholder for the player's name")
        public static class Player {
            public static String lost = "§c%player% lost!";
            public static String left = "%player% left!";
            public static String won = "§6%player% won!";
        }
    }

    public static @Nullable String getTranslation(@NotNull String path, Object... args) {
        var name = TranslationUtil.class.getSimpleName();
        var completePath = path.contains(name) ? path : name + "." + path;
        var result = configuration.getString(completePath);

        if(result == null) return null;

        return String.format(result, args);
    }

    public static @NotNull String getTranslationOrDefault(@NotNull String path, @NotNull String defaultValue, Object... args) {
        var result = getTranslation(path, args);

        if(result == null) return String.format(defaultValue, args);

        return result;
    }

}
