package cz.fi.muni.pa165.userservice.unit.persistence.repository;

import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import cz.fi.muni.pa165.userservice.persistence.repositories.BudgetOfferPackageRepository;
import cz.fi.muni.pa165.userservice.unit.testData.BudgetOfferPackageTestData;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
public class BudgetOfferPackageRepositoryTests {

	@Autowired
	private BudgetOfferPackageRepository repository;

	@Autowired
	private EntityManager entityManager;

	@Test
	void findAllActiveBudgetOfferPackages_whenMixedAvailableAndNonAvailable_shouldReturnOnlyAvailablePackages() {
		// Arrange
		var packages = BudgetOfferPackageTestData.getBudgetOfferPackages(100);
		for (var pkg : packages) {
			entityManager.persist(pkg);
		}
		entityManager.flush();

		// Act
		List<BudgetOfferPackage> availablePackages = repository.findAllActiveBudgetOfferPackages();

		// Assert
		var availablePackageCount = repository.findAll().stream().filter(BudgetOfferPackage::getIsAvailable).count();
		Assertions.assertTrue(availablePackageCount > 0);
		Assertions.assertTrue(availablePackages.stream().allMatch(BudgetOfferPackage::getIsAvailable));
		Assertions.assertEquals(availablePackageCount, availablePackages.size());
	}

	@Test
	void findAllActiveBudgetOfferPackages_whenNoPackageInDb_shouldReturnEmptyList() {
		// Act
		List<BudgetOfferPackage> availablePackages = repository.findAllActiveBudgetOfferPackages();

		// Assert
		Assertions.assertTrue(availablePackages.isEmpty());
		Assertions.assertTrue(repository.findAllActiveBudgetOfferPackages().isEmpty());
	}

	@Test
	void findAllActiveBudgetOfferPackages_whenAllPackagesAvailable_shouldReturnAllPackages() {
		// Arrange
		var packages = BudgetOfferPackageTestData.getBudgetOfferPackages(100);
		for (var pkg : packages) {
			pkg.setIsAvailable(true);
			entityManager.persist(pkg);
		}
		entityManager.flush();

		// Act
		List<BudgetOfferPackage> availablePackages = repository.findAllActiveBudgetOfferPackages();

		// Assert
		Assertions.assertTrue(availablePackages.stream().allMatch(BudgetOfferPackage::getIsAvailable));
		Assertions.assertEquals(packages.size(), availablePackages.size());
	}

	@Test
	void findAllActiveBudgetOfferPackages_whenNoPackagesAvailable_shouldReturnEmptyList() {
		// Arrange
		var packages = BudgetOfferPackageTestData.getBudgetOfferPackages(100);
		for (var pkg : packages) {
			pkg.setIsAvailable(false);
			entityManager.persist(pkg);
		}
		entityManager.flush();

		// Act
		List<BudgetOfferPackage> availablePackages = repository.findAllActiveBudgetOfferPackages();

		// Assert
		Assertions.assertTrue(availablePackages.isEmpty());
	}

}
