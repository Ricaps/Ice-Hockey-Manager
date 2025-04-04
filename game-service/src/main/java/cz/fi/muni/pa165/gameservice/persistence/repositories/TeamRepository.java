package cz.fi.muni.pa165.gameservice.persistence.repositories;

import cz.fi.muni.pa165.gameservice.persistence.entities.CompetitionHasTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<CompetitionHasTeam, UUID> {

}
