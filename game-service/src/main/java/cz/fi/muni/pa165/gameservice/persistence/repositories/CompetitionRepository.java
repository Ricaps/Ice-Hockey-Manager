package cz.fi.muni.pa165.gameservice.persistence.repositories;

import cz.fi.muni.pa165.gameservice.persistence.entities.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompetitionRepository extends JpaRepository<Competition, UUID> {

	Optional<Competition> getCompetitionByGuid(UUID guid);

}
