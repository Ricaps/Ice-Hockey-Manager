package cz.fi.muni.pa165.userservice.persistence.repositories;

import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BudgetOfferPackageRepository extends JpaRepository<BudgetOfferPackage, UUID> {

	@Query("SELECT p FROM BudgetOfferPackage p WHERE p.isAvailable = true ")
	List<BudgetOfferPackage> findAllActiveBudgetOfferPackages();

}
