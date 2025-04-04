package cz.fi.muni.pa165.gameservice.persistence.repositories;

import cz.fi.muni.pa165.gameservice.persistence.entities.Arena;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArenaRepository extends JpaRepository<Arena, UUID> {

}
