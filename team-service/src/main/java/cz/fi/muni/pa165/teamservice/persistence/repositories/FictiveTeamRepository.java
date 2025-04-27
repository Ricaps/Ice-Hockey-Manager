package cz.fi.muni.pa165.teamservice.persistence.repositories;

import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author Jan Martinek
 */
@Repository
public interface FictiveTeamRepository extends JpaRepository<FictiveTeam, UUID> {

	List<FictiveTeam> findByOwnerId(UUID ownerId);

}