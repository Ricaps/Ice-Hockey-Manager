package cz.fi.muni.pa165.teamservice.persistence.repositories;

import cz.fi.muni.pa165.teamservice.persistence.entities.BudgetSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

/**
 * @author Jan Martinek
 */
@Repository
public interface BudgetSystemRepository extends JpaRepository<BudgetSystem, UUID> {

	Optional<BudgetSystem> findByTeamGuid(UUID teamId);

}
