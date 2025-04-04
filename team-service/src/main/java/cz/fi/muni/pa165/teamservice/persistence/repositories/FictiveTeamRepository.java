package cz.fi.muni.pa165.teamservice.persistence.repositories;

/**
 * @author Jan Martinek
 */

import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FictiveTeamRepository extends JpaRepository<FictiveTeam, UUID> {

}