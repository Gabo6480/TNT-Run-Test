package org.gabo6480.tNTRunSpigot.repositories;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;
import org.gabo6480.tNTRunSpigot.entities.LobbyEntity;
import org.hibernate.annotations.processing.Find;

import javax.annotation.Nullable;
import java.util.List;

@Repository
public interface LobbyRepository extends CrudRepository<LobbyEntity, Integer> {

    @Find
    List<LobbyEntity> findByName(String name);

}
