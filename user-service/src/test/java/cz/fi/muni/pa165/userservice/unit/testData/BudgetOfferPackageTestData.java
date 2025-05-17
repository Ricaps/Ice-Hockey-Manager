package cz.fi.muni.pa165.userservice.unit.testData;

import cz.fi.muni.pa165.dto.userservice.BudgetOfferPackageDto;
import cz.fi.muni.pa165.dto.userservice.PaymentBudgetPackageOfferDto;
import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

	public static List<BudgetOfferPackage> getBudgetOfferPackages(int count) {
		List<BudgetOfferPackage> budgetOfferPackages = new ArrayList<BudgetOfferPackage>();

		Random random = new Random(654321L);
		for (int i = 0; i < count; i++) {
			budgetOfferPackages.add(BudgetOfferPackage.builder()
				.guid(null)
				.isAvailable(random.nextBoolean())
				.priceDollars(random.nextInt(0, 1000))
				.budgetIncrease(random.nextInt(1, 100000000))
				.build());
		}

		return budgetOfferPackages;
	}

}
