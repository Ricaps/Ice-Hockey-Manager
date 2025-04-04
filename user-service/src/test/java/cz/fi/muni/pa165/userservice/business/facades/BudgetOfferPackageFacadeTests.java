package cz.fi.muni.pa165.userservice.business.facades;

import cz.fi.muni.pa165.dto.userService.BudgetOfferPackageDto;
import cz.fi.muni.pa165.userservice.business.mappers.BudgetPackageOfferMapper;
import cz.fi.muni.pa165.userservice.business.services.BudgetOfferPackageService;
import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BudgetOfferPackageFacadeTests {

	@Mock
	private BudgetOfferPackageService budgetOfferPackageService;

	@Mock
	private BudgetPackageOfferMapper mapper;

	@InjectMocks
	private BudgetOfferPackageFacade facade;

	private UUID packageId;

	private BudgetOfferPackage entity;

	private BudgetOfferPackageDto dto;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		packageId = UUID.randomUUID();
		entity = new BudgetOfferPackage();
		dto = new BudgetOfferPackageDto();
	}

	@Test
	void getBudgetPackageOfferById_whenExistingId_returnsMappedDto() {
		// Arrange
		Mockito.when(budgetOfferPackageService.getBudgetOfferPackageById(packageId)).thenReturn(entity);
		Mockito.when(mapper.budgetOfferPackagetoBudgetOfferPackageDto(entity)).thenReturn(dto);

		// Act
		BudgetOfferPackageDto result = facade.getBudgetPackageOfferById(packageId);

		// Assert
		assertEquals(dto, result);
		Mockito.verify(budgetOfferPackageService).getBudgetOfferPackageById(packageId);
		Mockito.verify(mapper).budgetOfferPackagetoBudgetOfferPackageDto(entity);
	}

	@Test
	void create_whenValidDto_createsAndReturnsMappedDto() {
		// Arrange
		Mockito.when(mapper.budgetOfferPackageDtoToBudgetOfferPackage(dto)).thenReturn(entity);
		Mockito.when(budgetOfferPackageService.createBudgetOfferPackage(entity)).thenReturn(entity);
		Mockito.when(mapper.budgetOfferPackagetoBudgetOfferPackageDto(entity)).thenReturn(dto);

		// Act
		BudgetOfferPackageDto result = facade.create(dto);

		// Assert
		assertEquals(dto, result);
		Mockito.verify(mapper).budgetOfferPackageDtoToBudgetOfferPackage(dto);
		Mockito.verify(budgetOfferPackageService).createBudgetOfferPackage(entity);
		Mockito.verify(mapper).budgetOfferPackagetoBudgetOfferPackageDto(entity);
	}

	@Test
	void deactivateBudgetOfferPackage_whenExistingId_deactivatesAndReturnsMappedDto() {
		// Arrange
		Mockito.when(budgetOfferPackageService.deactivateBudgetOfferPackage(packageId)).thenReturn(entity);
		Mockito.when(mapper.budgetOfferPackagetoBudgetOfferPackageDto(entity)).thenReturn(dto);

		// Act
		BudgetOfferPackageDto result = facade.deactivateBudgetOfferPackage(packageId);

		// Assert
		assertEquals(dto, result);
		Mockito.verify(budgetOfferPackageService).deactivateBudgetOfferPackage(packageId);
		Mockito.verify(mapper).budgetOfferPackagetoBudgetOfferPackageDto(entity);
	}

	@Test
	void activateBudgetOfferPackage_whenExistingId_activatesAndReturnsMappedDto() {
		// Arrange
		Mockito.when(budgetOfferPackageService.activateBudgetOfferPackage(packageId)).thenReturn(entity);
		Mockito.when(mapper.budgetOfferPackagetoBudgetOfferPackageDto(entity)).thenReturn(dto);

		// Act
		BudgetOfferPackageDto result = facade.activateBudgetOfferPackage(packageId);

		// Assert
		assertEquals(dto, result);
		Mockito.verify(budgetOfferPackageService).activateBudgetOfferPackage(packageId);
		Mockito.verify(mapper).budgetOfferPackagetoBudgetOfferPackageDto(entity);
	}

	@Test
	void getAllBudgetOfferPackages_whenExistingPackages_returnsMappedDtoList() {
		// Arrange
		List<BudgetOfferPackage> entities = List.of(entity);
		List<BudgetOfferPackageDto> dtos = List.of(dto);

		Mockito.when(budgetOfferPackageService.getAllBudgetOfferPackages()).thenReturn(entities);
		Mockito.when(mapper.budgetOfferPackagetoBudgetOfferPackageDto(entity)).thenReturn(dto);

		// Act
		List<BudgetOfferPackageDto> result = facade.getAllBudgetOfferPackages();

		// Assert
		assertEquals(dtos, result);
		Mockito.verify(budgetOfferPackageService).getAllBudgetOfferPackages();
		Mockito.verify(mapper, Mockito.times(entities.size())).budgetOfferPackagetoBudgetOfferPackageDto(Mockito.any());
	}

	@Test
	void getAllAvailableBudgetOfferPackages_whenExistingPackages_returnsMappedDtoList() {
		// Arrange
		List<BudgetOfferPackage> entities = List.of(entity);
		List<BudgetOfferPackageDto> dtos = List.of(dto);

		Mockito.when(budgetOfferPackageService.getAllAvailableBudgetOfferPackages()).thenReturn(entities);
		Mockito.when(mapper.budgetOfferPackagetoBudgetOfferPackageDto(entity)).thenReturn(dto);

		// Act
		List<BudgetOfferPackageDto> result = facade.getAllAvailableBudgetOfferPackages();

		// Assert
		assertEquals(dtos, result);
		Mockito.verify(budgetOfferPackageService).getAllAvailableBudgetOfferPackages();
		Mockito.verify(mapper, Mockito.times(entities.size())).budgetOfferPackagetoBudgetOfferPackageDto(Mockito.any());
	}

}
