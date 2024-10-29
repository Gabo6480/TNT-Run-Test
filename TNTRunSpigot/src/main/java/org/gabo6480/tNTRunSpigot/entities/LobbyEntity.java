package org.gabo6480.tNTRunSpigot.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.gabo6480.tNTRunSpigot.repositories.LobbyRepository;

import java.util.UUID;

@Data
@Entity
@Table(name = "lobby")
public class LobbyEntity {
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

    public boolean LoadLobby(LobbyRepository repo){
        System.out.println("Loading lobby " + name);

        var world = new WorldCreator(worldPath).createWorld();

        if (world != null) {
            world.setAutoSave(false);

            this.UUID = world.getUID();
            System.out.println("Lobby " + name + " loaded with UUID: " + UUID);
            return true;
        }
        else{
            System.out.println("World folder missing for lobby: " + name);
            this.UUID = null;
        }

        repo.update(this);

        return false;
    }

    public boolean UnloadLobby(){
        var world = Bukkit.getWorld(UUID);

        if(world == null) return true;

        //world.getPlayers()

        System.out.println("Unloading world " + world.getName());
        return Bukkit.unloadWorld(world, false);
    }
}
