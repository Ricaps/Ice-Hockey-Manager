package cz.fi.muni.pa165.gameservice.persistence.repositories;

import cz.fi.muni.pa165.gameservice.persistence.entities.Match;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {

	List<Match> getMatchesByCompetition_Guid(@NotNull UUID competitionGuid);

	Optional<Match> getMatchByGuid(UUID guid);

	@Query("FROM Match WHERE result IS NULL AND endAt IS NULL AND startAt <= :maxTime")
	List<Match> getMatchesForScheduling(OffsetDateTime maxTime);

}
