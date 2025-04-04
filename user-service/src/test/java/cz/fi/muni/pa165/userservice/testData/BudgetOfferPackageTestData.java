package cz.fi.muni.pa165.userservice.testData;

import cz.fi.muni.pa165.dto.userService.BudgetOfferPackageDto;
import cz.fi.muni.pa165.dto.userService.PaymentBudgetPackageOfferDto;
import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;

import java.util.UUID;

public class BudgetOfferPackageTestData {

	public static BudgetOfferPackage getBudgetOfferPackage() {
		return BudgetOfferPackage.builder()
			.guid(UUID.randomUUID())
			.budgetIncrease(100)
			.priceDollars(984)
			.description("adasd")
			.isAvailable(true)
			.build();
	}

	public static BudgetOfferPackageDto getBudgetOfferPackageDto() {
		return BudgetOfferPackageDto.builder()
			.guid(UUID.randomUUID())
			.budgetIncrease(100)
			.priceDollars(984)
			.description("adasd")
			.isAvailable(true)
			.build();
	}

	public static PaymentBudgetPackageOfferDto getPaymentBudgetPackageOfferDto() {
		return PaymentBudgetPackageOfferDto.builder()
			.guid(UUID.randomUUID())
			.budgetIncrease(516)
			.priceDollars(556)
			.description("sdapfj aspdija apsodj")
			.isAvailable(true)
			.build();
	}

}
