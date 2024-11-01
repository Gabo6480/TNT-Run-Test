package org.gabo6480.tNTRunSpigot.entities.arena;

import lombok.Getter;
import org.gabo6480.tNTRunSpigot.util.TranslationUtil;
import org.jetbrains.annotations.NotNull;

@Getter
public enum ArenaState {
    WAITING_FOR_PLAYERS(TranslationUtil.Arena.State.WAITING_FOR_PLAYERS),
    GAME_STARTING(TranslationUtil.Arena.State.GAME_STARTING),
    IN_GAME(TranslationUtil.Arena.State.IN_GAME),
    ENDING(TranslationUtil.Arena.State.ENDING),
    RESTARTING(TranslationUtil.Arena.State.RESTARTING),
    ERROR(TranslationUtil.Arena.State.ERROR),
    UNLOADED(TranslationUtil.Arena.State.UNLOADED);

    final @NotNull String translation;

    ArenaState(@NotNull String translation) {
        this.translation = translation;
    }
}
