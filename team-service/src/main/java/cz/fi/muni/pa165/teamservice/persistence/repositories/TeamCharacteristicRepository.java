package cz.fi.muni.pa165.teamservice.persistence.repositories;

import cz.fi.muni.pa165.teamservice.persistence.entities.TeamCharacteristic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author Jan Martinek
 */
@Repository
public interface TeamCharacteristicRepository extends JpaRepository<TeamCharacteristic, UUID> {

	List<TeamCharacteristic> findByFictiveTeamGuid(UUID teamId);

}