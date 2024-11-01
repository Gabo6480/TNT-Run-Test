package org.gabo6480.tNTRunSpigot.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.ExtensionMethod;
import org.bukkit.*;
import org.bukkit.block.data.type.TNT;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.gabo6480.tNTRunSpigot.TNTRunSpigot;
import org.gabo6480.tNTRunSpigot.entities.arena.ArenaPlayer;
import org.gabo6480.tNTRunSpigot.entities.arena.ArenaState;
import org.gabo6480.tNTRunSpigot.util.PacketUtil;
import org.gabo6480.tNTRunSpigot.util.TranslationUtil;
import org.gabo6480.tNTRunSpigot.util.extensions.StringExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Data
@Entity
@Table(name = "arena")
@ExtensionMethod(StringExtension.class)
public class ArenaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String worldPath;

    @Column(nullable = true)
    private java.util.UUID UUID;

    @Column(nullable = false)
    private Double waitingRoomX = 0.0D, waitingRoomY = 0.0D, waitingRoomZ = 0.0D;
    @Column(nullable = false)
    private Float waitingRoomPitch = 0.0f, waitingRoomYaw = 0.0f;

    @Column(nullable = false)
    private Double startPointX = 0.0D, startPointY = 0.0D, startPointZ = 0.0D;
    @Column(nullable = false)
    private Float startPointPitch = 0.0f, startPointYaw = 0.0f;

    @Column(nullable = false)
    int minPlayers = 0, maxPlayers = 5;

    @NotNull
    @Transient
    private ArenaState state = ArenaState.UNLOADED;

    @NotNull
    @Transient
    Integer activePlayersAtStart;
    @NotNull
    @Transient
    private Map<java.util.UUID, ArenaPlayer> activePlayers = new HashMap<>();
    @NotNull
    @Transient
    private Map<java.util.UUID, ArenaPlayer> deadPlayers = new HashMap<>();
    @Nullable
    @Transient
    ArenaPlayer winner = null;
    @NotNull
    @Transient
    private BossBar bossBar;
    @Nullable
    @Transient
    private BukkitTask currentTask;
    @Transient
    private int restartScheduleId;

    @Transient
    private int secondsTillStart = 5;

    public ArenaEntity() {
        bossBar = Bukkit.createBossBar("BossBar", BarColor.RED, BarStyle.SEGMENTED_12);
    }

    public void UpdateBossBar(){
        switch (state){
            case WAITING_FOR_PLAYERS -> {
                bossBar.setStyle(BarStyle.SOLID);
                if(activePlayers.size() < minPlayers) {
                    bossBar.setProgress((double) activePlayers.size() / (double) minPlayers);
                    bossBar.setColor(BarColor.RED);
                }
                else {
                    bossBar.setProgress(1D);
                    bossBar.setColor(BarColor.BLUE);
                }
                bossBar.setTitle(TranslationUtil.Arena.BossBar.waiting_for_players.putPlaceholder("%aliveCount%", activePlayers.size()).putPlaceholder("%minPlayers%", minPlayers));
            }
            case GAME_STARTING -> {
                bossBar.setTitle(TranslationUtil.Arena.BossBar.starting_game);
                bossBar.setProgress((double) secondsTillStart / 5D);
                bossBar.setColor(BarColor.GREEN);
            }
            case IN_GAME -> {
                bossBar.setTitle(TranslationUtil.Arena.BossBar.in_game.putPlaceholder("%aliveCount%", activePlayers.size()).putPlaceholder("%playerCount%", activePlayersAtStart));
                bossBar.setProgress((double) activePlayers.size() / (double) activePlayersAtStart);
                bossBar.setColor(BarColor.PURPLE);
            }
            case ENDING -> {
                assert winner != null;
                bossBar.setTitle(TranslationUtil.Arena.BossBar.player_won.putPlaceholder("%player%", winner.getPlayer().getDisplayName()));
                bossBar.setColor(BarColor.YELLOW);
                bossBar.setProgress(1D);
            }
            default -> {
                bossBar.setColor(BarColor.WHITE);
                bossBar.setProgress(0);
                bossBar.setTitle(state.name());
            }
        }
    }

    public boolean LoadArena(){
        System.out.println("Loading lobby " + name);

        this.state = ArenaState.RESTARTING;

        var world = new WorldCreator(worldPath).createWorld();
        boolean res = false;
        if (world != null) {
            world.setAutoSave(false);

            this.UUID = world.getUID();

            world.setKeepSpawnInMemory(false);
            world.setGameRule(GameRule.MOB_GRIEFING, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.TNT_EXPLOSION_DROP_DECAY, false);
            world.setGameRule(GameRule.BLOCK_EXPLOSION_DROP_DECAY, false);
            world.setGameRule(GameRule.MOB_EXPLOSION_DROP_DECAY, false);
            world.setDifficulty(Difficulty.PEACEFUL);

            System.out.println("Arena " + name + " loaded with UUID: " + UUID);
            res = true;
        }
        else{
            System.out.println("World folder missing for arena: " + name);
            this.UUID = null;
        }

        this.state = res ? ArenaState.WAITING_FOR_PLAYERS : ArenaState.ERROR;


        winner = null;
        secondsTillStart = 5;

        this.UpdateBossBar();

        return res;
    }
    public boolean UnloadArena(){
        var world = GetWorld();

        if(world == null) return true;

        world.getPlayers().forEach(this::TeleportToLobby);

        if(currentTask != null) currentTask.cancel();

        this.state = ArenaState.UNLOADED;
        bossBar.removeAll();
        this.UpdateBossBar();

        System.out.println("Unloading world " + world.getName());
        return Bukkit.unloadWorld(world, false);
    }
    private void RestartArena(){
        Bukkit.getScheduler().cancelTask(restartScheduleId);
        restartScheduleId = -1;
        this.UnloadArena();

        if(TNTRunSpigot.instance.isEnabled()) Bukkit.getScheduler().scheduleSyncDelayedTask(TNTRunSpigot.instance, this::LoadArena, 5 * 20);
    }

    public boolean IsInArena(Player player){
        return activePlayers.containsKey(player.getUniqueId()) || deadPlayers.containsKey(player.getUniqueId());
    }

    public final boolean IsArenaState(ArenaState state){
        return this.state.equals(state);
    }

    @Nullable
    public World GetWorld(){
        return Bukkit.getWorld(this.UUID);
    }

    @Nullable
    public ArenaPlayer GetArenaPlayer(@NotNull Player player){
        if (activePlayers.containsKey(player.getUniqueId())) {
            return activePlayers.get(player.getUniqueId());
        }
        if (deadPlayers.containsKey(player.getUniqueId())) {
            return deadPlayers.get(player.getUniqueId());
        }
        return null;
    }

    public boolean CanPlayerJoin(@NotNull Player player){
        return (state == ArenaState.WAITING_FOR_PLAYERS || state == ArenaState.GAME_STARTING) && activePlayers.size() < maxPlayers;
    }

    public boolean AddPlayer(@NotNull Player player){
        if(!this.CanPlayerJoin(player)) return false;

        activePlayers.put(player.getUniqueId(), new ArenaPlayer(player, this));

        player.sendMessage(TranslationUtil.Arena.welcome_message.putPlaceholder("%arena%",name).putPlaceholder("%currentPlayers%", activePlayers.size()).putPlaceholder("%minPlayers%",minPlayers));
        bossBar.addPlayer(player);

        this.UpdateBossBar();

        if(state == ArenaState.GAME_STARTING){
            MovePlayerToStartingGame(player);
        }
        else {
            player.teleport(GetWaitingRoomLocation());
        }

        if(activePlayers.size() >= minPlayers){
            BeginStartingGame();
        }
        return true;
    }

    public void SetWaitingRoomLocation(@Nullable Location location){

        if(location == null) return;

        this.waitingRoomX = location.getX();
        this.waitingRoomY = location.getY();
        this.waitingRoomZ = location.getZ();
        this.waitingRoomPitch = location.getPitch();
        this.waitingRoomYaw = location.getYaw();
    }
    @NotNull
    public Location GetWaitingRoomLocation(){
        return new Location(GetWorld(), waitingRoomX, waitingRoomY, waitingRoomZ, waitingRoomYaw, waitingRoomPitch);
    }

    public void SetStartPointLocation(@Nullable Location location){

        if(location == null) return;

        this.startPointX = location.getX();
        this.startPointY = location.getY();
        this.startPointZ = location.getZ();
        this.startPointPitch = location.getPitch();
        this.startPointYaw = location.getYaw();
    }
    @NotNull
    public Location GetStartPointLocation(){
        return new Location(GetWorld(), startPointX, startPointY, startPointZ, startPointYaw, startPointPitch);
    }



    public void CheckForWinner(){
        if (activePlayers.size() > 1) return;

        this.state = ArenaState.ENDING;

        if(activePlayers.size() == 1)
            this.winner = activePlayers.values().iterator().next();
        else {
            var list = deadPlayers.values().stream().toList();
            if(list.isEmpty()) return;
            this.winner = list.get(list.size() - 1);
        }

        this.UpdateBossBar();
        //arena.broadcastFormattedMessage("messages.in-game.last-one-fell-into-void", user);

        //plugin.getArenaManager().stopGame(false, arena);

        assert winner != null;

        var player =  winner.getPlayer();

        winner.setAlive(false);
        player.teleport(GetWaitingRoomLocation());
        player.setGameMode(GameMode.ADVENTURE);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.sendTitle(TranslationUtil.Arena.you_won, TranslationUtil.Arena.congratulations, 5, 35, 0);
        player.setAllowFlight(true);
        player.setFlying(true);

        this.BroadcastToPlayers(TranslationUtil.Arena.Player.won.putPlaceholder("%player%",winner.getPlayer().getDisplayName()));

        if(currentTask != null) currentTask.cancel();
        currentTask = Bukkit.getScheduler().runTaskTimer(TNTRunSpigot.instance, () -> {
            if(!IsInArena(winner.getPlayer())) return;

            Location loc = winner.getPlayer().getLocation();

            assert loc.getWorld() != null;

            Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();

            fwm.setPower(2);
            fw.setLife(2);

            var color = java.awt.Color.getHSBColor(new Random().nextFloat(0,1), 1f, 1f);

            fwm.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue())).flicker(true).build());

            fw.setFireworkMeta(fwm);
        }, 0, 10);

        restartScheduleId = Bukkit.getScheduler().scheduleSyncDelayedTask(TNTRunSpigot.instance, this::RestartArena, 15 * 20);
    }

    public void BeginStartingGame(){
        if(state != ArenaState.WAITING_FOR_PLAYERS) return;

        state = ArenaState.GAME_STARTING;

        UpdateBossBar();

        activePlayers.values().forEach(arenaPlayer -> {
            MovePlayerToStartingGame(arenaPlayer.getPlayer());
        });

        if(currentTask != null) currentTask.cancel();
        currentTask = Bukkit.getScheduler().runTaskTimerAsynchronously(TNTRunSpigot.instance, () -> {
            if(secondsTillStart == 0){
                Bukkit.getScheduler().scheduleSyncDelayedTask(TNTRunSpigot.instance, this::StartGame, 1);
                return;
            }

            UpdateBossBar();

            activePlayers.values().forEach(arenaPlayer -> {
                arenaPlayer.getPlayer().sendTitle(String.valueOf(secondsTillStart), "", 5, 15, 0);
            });

            secondsTillStart--;
        }, 0, 20);
    }

    private void StartGame(){
        if(currentTask != null) currentTask.cancel();

        state = ArenaState.IN_GAME;
        activePlayersAtStart = activePlayers.size();

        UpdateBossBar();

        activePlayers.values().forEach(arenaPlayer -> {
            var player = arenaPlayer.getPlayer();
            player.sendTitle(TranslationUtil.Arena.run, "", 5, 10, 5);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        });



        if(currentTask != null) currentTask.cancel();
        currentTask = Bukkit.getScheduler().runTaskTimerAsynchronously(TNTRunSpigot.instance, () -> {
            if (state != ArenaState.IN_GAME) {
                if(currentTask != null) currentTask.cancel();
                return;
            }
            activePlayers.values().forEach(arenaPlayer -> {
                var player = arenaPlayer.getPlayer();
                var bb = player.getBoundingBox().clone();

                var Y = bb.getMinY() - 1.2;
                var world = player.getWorld();
                var blocks = List.of(
                        world.getBlockAt(new Location(world, bb.getMinX(), Y, bb.getMinZ())),
                        world.getBlockAt(new Location(world, bb.getMinX(), Y, bb.getMaxZ())),
                        world.getBlockAt(new Location(world, bb.getMaxX(), Y, bb.getMinZ())),
                        world.getBlockAt(new Location(world, bb.getMaxX(), Y, bb.getMaxZ()))
                );

                if(TNTRunSpigot.instance.isEnabled() && blocks.stream().anyMatch(b -> b.getType().equals(Material.TNT))){
                    Bukkit.getScheduler().scheduleSyncDelayedTask(TNTRunSpigot.instance, () -> {
                        for(var block : blocks){
                            if(!block.getType().equals(Material.TNT)) continue;
                            block.setType(Material.AIR);
                            block.getWorld().playSound(block.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1);
                        }

                    }, 4);
                }
            });
        }, 5, 1);

        //CheckForWinner();
    }




    public void MovePlayerToStartingGame(@NotNull Player player){
        player.teleport(GetStartPointLocation());
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 3));
    }

    public void TeleportToLobby(ArenaPlayer arenaPlayer){
        this.PlayerLeft(arenaPlayer);
        this.TeleportToLobby(arenaPlayer.getPlayer());
    }

    public void TeleportToLobby(Player player){
        if(IsInArena(player)){
            var arenaPlayer = this.GetArenaPlayer(player);
            assert arenaPlayer != null;
            this.PlayerLeft(arenaPlayer);
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.teleport(TNTRunSpigot.instance.lobby);
    }

    public void PlayerDied(ArenaPlayer arenaPlayer){
        activePlayers.remove(arenaPlayer.GetUniqueId());
        deadPlayers.put(arenaPlayer.GetUniqueId(), arenaPlayer);
        var player = arenaPlayer.getPlayer();
        player.setGameMode(GameMode.SPECTATOR);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        arenaPlayer.setAlive(false);
        arenaPlayer.PlayerDeathEffect();

        //user.addGameItems("leave-item");

        this.BroadcastToPlayers(TranslationUtil.Arena.Player.lost.putPlaceholder("%player%", arenaPlayer.getPlayer().getDisplayName()));

        this.UpdateBossBar();

        CheckForWinner();
    }

    public void PlayerLeft(ArenaPlayer arenaPlayer){
        activePlayers.remove(arenaPlayer.GetUniqueId());
        deadPlayers.remove(arenaPlayer.GetUniqueId());

        if(this.state == ArenaState.IN_GAME && !arenaPlayer.IsSpectator()) {
            arenaPlayer.PlayerDeathEffect();
            this.BroadcastToPlayers(TranslationUtil.Arena.Player.left.putPlaceholder("%player%",arenaPlayer.getPlayer().getDisplayName()));
        }

        var player = arenaPlayer.getPlayer();

        bossBar.removePlayer(player);

        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

        if(activePlayers.isEmpty() && deadPlayers.isEmpty()){
            RestartArena();
        }

        this.UpdateBossBar();

        CheckForWinner();
    }

    private void BroadcastToPlayers(@NotNull String message){
        this.activePlayers.values().forEach(arenaPlayer -> PacketUtil.SendPacketToPlayer(arenaPlayer.getPlayer(), message));
        this.deadPlayers.values().forEach(arenaPlayer -> PacketUtil.SendPacketToPlayer(arenaPlayer.getPlayer(), message));
    }
}
