package org.gabo6480.tNTRunSpigot.entities.arena;


import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.gabo6480.tNTRunSpigot.entities.ArenaEntity;

import java.util.UUID;

@Getter
public class ArenaPlayer {
    Player player;
    ArenaEntity arena;
    @Setter
    boolean isAlive = true;

    public ArenaPlayer(Player player, ArenaEntity arena) {
        this.player = player;
        this.arena = arena;

        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFlySpeed(.1F);
        player.setWalkSpeed(.2F);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }

    public void PlayerDeathEffect(){
        player.getWorld().createExplosion(player.getLocation(), 1, false, false);
    }

    public boolean IsSpectator(){
        return player.getGameMode() == GameMode.SPECTATOR;
    }

    public UUID GetUniqueId(){
        return player.getUniqueId();
    }
}
