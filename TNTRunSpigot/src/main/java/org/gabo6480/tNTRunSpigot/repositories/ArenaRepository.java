package org.gabo6480.tNTRunSpigot.repositories;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import org.gabo6480.tNTRunSpigot.entities.ArenaEntity;
import org.hibernate.annotations.processing.Find;

import java.util.List;

@Repository
public interface ArenaRepository extends CrudRepository<ArenaEntity, Integer> {

    @Find
    List<ArenaEntity> findByName(String name);

}
