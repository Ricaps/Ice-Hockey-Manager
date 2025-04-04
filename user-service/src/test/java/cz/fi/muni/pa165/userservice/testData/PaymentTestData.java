package cz.fi.muni.pa165.userservice.testData;

import cz.fi.muni.pa165.dto.userService.PaymentUpdateCreateDto;
import cz.fi.muni.pa165.dto.userService.PaymentViewDto;
import cz.fi.muni.pa165.dto.userService.UserPaymentDto;
import cz.fi.muni.pa165.userservice.persistence.entities.Payment;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentTestData {

	public static Payment getPayment() {
		return Payment.builder()
			.guid(UUID.randomUUID())
			.createdAt(LocalDateTime.now())
			.paid(false)
			.user(UserTestData.getUser())
			.budgetOfferPackage(BudgetOfferPackageTestData.getBudgetOfferPackage())
			.build();
	}

	public static PaymentUpdateCreateDto getPaymentUpdateCreateDto() {
		return PaymentUpdateCreateDto.builder()
			.guid(UUID.randomUUID())
			.createdAt(LocalDateTime.now())
			.paid(false)
			.userId(UserTestData.getUser().getGuid())
			.budgetOfferPackageId(BudgetOfferPackageTestData.getBudgetOfferPackage().getGuid())
			.build();
	}

	public static UserPaymentDto getUserPaymentDto() {
		return UserPaymentDto.builder()
			.guid(UUID.randomUUID())
			.budgetOfferPackage(BudgetOfferPackageTestData.getPaymentBudgetPackageOfferDto())
			.createdAt(LocalDateTime.now())
			.paid(true)
			.build();
	}

	public static PaymentViewDto getPaymentViewDto() {
		return PaymentViewDto.builder()
			.guid(UUID.randomUUID())
			.paid(true)
			.budgetOfferPackage(BudgetOfferPackageTestData.getPaymentBudgetPackageOfferDto())
			.user(UserTestData.getPaymentUserViewDto())
			.createdAt(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1))
			.build();
	}

}
