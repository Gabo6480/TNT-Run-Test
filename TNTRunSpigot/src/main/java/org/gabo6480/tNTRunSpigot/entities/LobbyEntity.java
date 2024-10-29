package org.gabo6480.tNTRunSpigot.entities;

import jakarta.persistence.*;
import lombok.Data;

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


    //TODO: Agregar logica para cargar y descargar el mapa
    //TODO: Incluir en la logica de cargar el mapa que: Configurable de gamerules (pero que no se genere terreno de mientras mamón)
}
