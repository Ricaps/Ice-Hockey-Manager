package cz.fi.muni.pa165.userservice.unit.business.service;

import cz.fi.muni.pa165.userservice.api.exception.BlankValueException;
import cz.fi.muni.pa165.userservice.business.services.BudgetOfferPackageService;
import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import cz.fi.muni.pa165.userservice.persistence.repositories.BudgetOfferPackageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class BudgetOfferPackageServiceTests {

	@Mock
	private BudgetOfferPackageRepository budgetOfferPackageRepository;

	@InjectMocks
	private BudgetOfferPackageService budgetOfferPackageService;

	@Test
	void getBudgetOfferPackageById_whenIdIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> budgetOfferPackageService.getBudgetOfferPackageById(null));
	}

	@Test
	void getBudgetOfferPackageById_whenPackageNotFound_shouldThrowEntityNotFoundException() {
		// Arrange
		Mockito.when(budgetOfferPackageRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class,
				() -> budgetOfferPackageService.getBudgetOfferPackageById(UUID.randomUUID()));
	}

	@Test
	void getBudgetOfferPackageById_whenPackageFound_shouldReturnPackage() {
		// Arrange
		UUID packageId = UUID.randomUUID();
		Mockito.when(budgetOfferPackageRepository.findById(Mockito.any(UUID.class)))
			.thenReturn(Optional.of(getBudgetOfferPackage(false)));

		// Act
		budgetOfferPackageService.getBudgetOfferPackageById(packageId);

		// Assert
		verify(budgetOfferPackageRepository, times(1)).findById(packageId);
	}

	@Test
	void createBudgetOfferPackage_whenPackageIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> budgetOfferPackageService.createBudgetOfferPackage(null));
		Mockito.verify(budgetOfferPackageRepository, Mockito.never()).save(Mockito.any(BudgetOfferPackage.class));
	}

	@Test
	void createBudgetOfferPackage_whenPackageIdIsNotNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> budgetOfferPackageService.createBudgetOfferPackage(null));
		Mockito.verify(budgetOfferPackageRepository, Mockito.never()).save(Mockito.any(BudgetOfferPackage.class));
	}

	@Test
	void createBudgetOfferPackage_whenValidPackageIsGiven_shouldSavePackage() {
		// Arrange
		BudgetOfferPackage packageToCreate = getBudgetOfferPackage(true);
		Mockito.when(budgetOfferPackageRepository.save(Mockito.any(BudgetOfferPackage.class)))
			.thenReturn(packageToCreate);

		// Act
		BudgetOfferPackage savedPackage = budgetOfferPackageService.createBudgetOfferPackage(packageToCreate);

		// Assert
		verify(budgetOfferPackageRepository, times(1)).save(Mockito.any(BudgetOfferPackage.class));
	}

	@Test
	void deactivateBudgetOfferPackage_whenPackageIdIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> budgetOfferPackageService.deactivateBudgetOfferPackage(null));
		Mockito.verify(budgetOfferPackageRepository, Mockito.never()).save(Mockito.any(BudgetOfferPackage.class));
	}

	@Test
	void deactivateBudgetOfferPackage_whenPackageNotFound_shouldThrowEntityNotFoundException() {
		// Arrange
		UUID packageId = UUID.randomUUID();
		Mockito.when(budgetOfferPackageRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class,
				() -> budgetOfferPackageService.deactivateBudgetOfferPackage(packageId));
		Mockito.verify(budgetOfferPackageRepository, Mockito.never()).save(Mockito.any(BudgetOfferPackage.class));
	}

	@Test
	void deactivateBudgetOfferPackage_whenValidPackageIdIsGiven_shouldDeactivatePackage() {
		// Arrange
		UUID packageId = UUID.randomUUID();
		BudgetOfferPackage budgetOfferPackage = getBudgetOfferPackage(false);
		Mockito.when(budgetOfferPackageRepository.findById(Mockito.any(UUID.class)))
			.thenReturn(Optional.of(budgetOfferPackage));
		Mockito.when(budgetOfferPackageRepository.save(Mockito.any(BudgetOfferPackage.class)))
			.thenReturn(budgetOfferPackage);

		// Act
		BudgetOfferPackage deactivatedPackage = budgetOfferPackageService.deactivateBudgetOfferPackage(packageId);

		// Assert
		verify(budgetOfferPackageRepository, times(1)).save(Mockito.any(BudgetOfferPackage.class));
		assertNotNull(deactivatedPackage);
		assertFalse(deactivatedPackage.getIsAvailable());
	}

	@Test
	void activateBudgetOfferPackage_whenPackageIdIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> budgetOfferPackageService.activateBudgetOfferPackage(null));
		Mockito.verify(budgetOfferPackageRepository, Mockito.never()).save(Mockito.any(BudgetOfferPackage.class));
	}

	@Test
	void activateBudgetOfferPackage_whenPackageNotFound_shouldThrowEntityNotFoundException() {
		// Arrange
		UUID packageId = UUID.randomUUID();
		Mockito.when(budgetOfferPackageRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class,
				() -> budgetOfferPackageService.activateBudgetOfferPackage(packageId));
		Mockito.verify(budgetOfferPackageRepository, Mockito.never()).save(Mockito.any(BudgetOfferPackage.class));
	}

	@Test
	void activateBudgetOfferPackage_whenValidPackageIdIsGiven_shouldActivatePackage() {
		// Arrange
		UUID packageId = UUID.randomUUID();
		BudgetOfferPackage budgetOfferPackage = getBudgetOfferPackage(false);
		Mockito.when(budgetOfferPackageRepository.findById(Mockito.any(UUID.class)))
			.thenReturn(Optional.of(budgetOfferPackage));
		Mockito.when(budgetOfferPackageRepository.save(Mockito.any(BudgetOfferPackage.class)))
			.thenReturn(budgetOfferPackage);

		// Act
		BudgetOfferPackage activatedPackage = budgetOfferPackageService.activateBudgetOfferPackage(packageId);

		// Assert
		verify(budgetOfferPackageRepository, times(1)).save(Mockito.any(BudgetOfferPackage.class));
		assertNotNull(activatedPackage);
		assertTrue(activatedPackage.getIsAvailable());
	}

	@Test
	void getAllBudgetOfferPackages_whenRepositoryReturns_shouldReturn() {
		// Arrange
		Mockito.when(budgetOfferPackageRepository.findAll()).thenReturn(List.of(getBudgetOfferPackage(true)));

		// Act
		Collection<BudgetOfferPackage> allPackages = budgetOfferPackageService.getAllBudgetOfferPackages();

		// Assert
		Mockito.verify(budgetOfferPackageRepository, Mockito.times(1)).findAll();
		assertNotNull(allPackages);
		assertFalse(allPackages.isEmpty());
	}

	@Test
	void getAllAvailableBudgetOfferPackages_whenRepositoryReturns_shouldReturn() {
		// Arrange
		Mockito.when(budgetOfferPackageRepository.findAllActiveBudgetOfferPackages())
			.thenReturn(List.of(getBudgetOfferPackage(true)));

		// Act
		Collection<BudgetOfferPackage> allPackages = budgetOfferPackageService.getAllAvailableBudgetOfferPackages();

		// Assert
		Mockito.verify(budgetOfferPackageRepository, Mockito.times(1)).findAllActiveBudgetOfferPackages();
		assertNotNull(allPackages);
		assertFalse(allPackages.isEmpty());
	}

	private BudgetOfferPackage getBudgetOfferPackage(boolean emptyGuid) {
		return BudgetOfferPackage.builder()
			.guid(emptyGuid ? null : UUID.randomUUID())
			.isAvailable(true)
			.description("This is a basic package")
			.priceDollars(200)
			.budgetIncrease(600)
			.build();
	}

}
