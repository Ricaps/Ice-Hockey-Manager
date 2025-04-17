package cz.fi.muni.pa165.userservice.unit.business.mappers;

import cz.fi.muni.pa165.dto.userService.BudgetOfferPackageDto;
import cz.fi.muni.pa165.userservice.business.mappers.BudgetPackageOfferMapper;
import cz.fi.muni.pa165.userservice.business.mappers.BudgetPackageOfferMapperImpl;
import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import cz.fi.muni.pa165.userservice.unit.testData.BudgetOfferPackageTestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BudgetOfferPackageMapperTests {

	private final BudgetPackageOfferMapper mapper = new BudgetPackageOfferMapperImpl();

	@Test
	void mapBudgetOfferPackageToBudgetOfferPackageDto() {
		// Arrange
		BudgetOfferPackage budgetOfferPackage = BudgetOfferPackageTestData.getBudgetOfferPackage();

		// Act
		BudgetOfferPackageDto budgetOfferPackageDto = mapper
			.budgetOfferPackagetoBudgetOfferPackageDto(budgetOfferPackage);

		// Assert
		equalsBudgetOfferPackageAndBudgetOfferPackageDto(budgetOfferPackage, budgetOfferPackageDto);
	}

	@Test
	void mapBudgetOfferPackageDtoToBudgetOfferPackage() {
		// Arrange
		BudgetOfferPackageDto budgetOfferPackageDto = BudgetOfferPackageTestData.getBudgetOfferPackageDto();

		// Act
		BudgetOfferPackage budgetOfferPackage = mapper.budgetOfferPackageDtoToBudgetOfferPackage(budgetOfferPackageDto);

		// Assert
		equalsBudgetOfferPackageAndBudgetOfferPackageDto(budgetOfferPackage, budgetOfferPackageDto);
	}

	private void equalsBudgetOfferPackageAndBudgetOfferPackageDto(BudgetOfferPackage budgetOfferPackage,
			BudgetOfferPackageDto budgetOfferPackageDto) {
		assertEquals(budgetOfferPackage.getGuid(), budgetOfferPackageDto.getGuid());
		assertEquals(budgetOfferPackage.getPriceDollars(), budgetOfferPackageDto.getPriceDollars());
		assertEquals(budgetOfferPackage.getBudgetIncrease(), budgetOfferPackageDto.getBudgetIncrease());
		assertEquals(budgetOfferPackage.getDescription(), budgetOfferPackageDto.getDescription());
		assertEquals(budgetOfferPackage.getIsAvailable(), budgetOfferPackageDto.getIsAvailable());
	}

}
