package org.gabo6480.tNTRunSpigot.managers;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.gabo6480.tNTRunSpigot.TNTRunSpigot;
import org.gabo6480.tNTRunSpigot.entities.ArenaEntity;
import org.gabo6480.tNTRunSpigot.repositories.ArenaRepository;
import org.gabo6480.tNTRunSpigot.repositories.ArenaRepository_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

@Getter
public class ArenaManager {

    final HashMap<UUID, ArenaEntity> arenas = new HashMap<>();

    public static boolean SecureRepositoryAccess(Function<ArenaRepository, Boolean> access){
        var session = TNTRunSpigot.instance.getSessionFactory().openStatelessSession();
        ArenaRepository arenaRepository = new ArenaRepository_(session);

        var result = access.apply(arenaRepository);

        session.close();

        return result;
    }

    @Nullable
    public ArenaEntity GetArena(UUID uuid){
        if(!arenas.containsKey(uuid)) return null;
        return arenas.get(uuid);
    }

    @Nullable
    public ArenaEntity GetArena(World world){
        return GetArena(world.getUID());
    }

    @Nullable
    public ArenaEntity GetArena(String name){
        for (var item : arenas.values()){
            if(item.getName().equals(name)) return item;
        }
        return null;
    }

    public void SaveArenas(){
        SecureRepositoryAccess(arenaRepository -> {
            arenaRepository.updateAll(arenas.values().stream().toList());
            return true;
        });
    }

    public void SaveArena(ArenaEntity arena){
        SecureRepositoryAccess(arenaRepository -> {
            arenaRepository.update(arena);
            return true;
        });

        arenas.put(arena.getUUID(), arena);
    }

    public void UnloadAllArenas(){
        arenas.values().forEach(ArenaEntity::UnloadArena);
        SaveArenas();
    }

    public void LoadAllArenas(){
        SecureRepositoryAccess(arenaRepository -> {
            arenaRepository.findAll().forEach(arenaEntity -> {
                arenas.put(arenaEntity.getUUID(), arenaEntity);
                arenaEntity.LoadArena();
            });
            return true;
        });
    }

    public boolean LoadArena(ArenaEntity arena){
        var result = arena.LoadArena();

        SaveArena(arena);

        return result;
    }

    public boolean IsInArena(Player player){
        return arenas.values().stream().anyMatch(a -> a.IsInArena(player));
    }

    public boolean IsArena(World world){
        return arenas.containsKey(world.getUID());
    }

    public boolean CreateArena(@NotNull String name, @NotNull String worldPath){
        return SecureRepositoryAccess(arenaRepository -> {
            ArenaEntity newArena = new ArenaEntity();
            newArena.setName(name);
            newArena.setWorldPath(worldPath);
            newArena.setWorldPath(worldPath);

            arenaRepository.insert(newArena);

            var res = newArena.LoadArena();

            if(res) {
                var world = newArena.GetWorld();

                assert world != null;

                arenas.put(newArena.getUUID(), newArena);
                newArena.SetWaitingRoomLocation(world.getSpawnLocation());
                newArena.SetStartPointLocation(world.getSpawnLocation());
            }

            return res;
        });

    }
}
