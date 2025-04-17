package cz.fi.muni.pa165.userservice.unit.business.mappers;

import cz.fi.muni.pa165.dto.userService.PaymentUpdateCreateDto;
import cz.fi.muni.pa165.dto.userService.PaymentViewDto;
import cz.fi.muni.pa165.userservice.business.mappers.PaymentMapper;
import cz.fi.muni.pa165.userservice.business.mappers.PaymentMapperImpl;
import cz.fi.muni.pa165.userservice.persistence.entities.Payment;
import cz.fi.muni.pa165.userservice.unit.testData.PaymentTestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentMapperTests {

	PaymentMapper mapper = new PaymentMapperImpl();

	@Test
	void mapPaymentToPaymentViewDto() {
		// Arrange
		Payment payment = PaymentTestData.getPayment();

		// Act
		PaymentViewDto paymentViewDto = mapper.paymentToPaymentUserViewDto(payment);

		// Assert
		assertEqualsPaymentAndPaymentViewDto(payment, paymentViewDto);
	}

	@Test
	void mapPaymentUpdateCreateDtoToPayment() {
		// Arrange
		PaymentUpdateCreateDto paymentUpdateCreateDto = PaymentTestData.getPaymentUpdateCreateDto();

		// Act
		Payment payment = mapper.paymentUpdateCreateDtoToPayment(paymentUpdateCreateDto);

		// Assert
		assertEquals(paymentUpdateCreateDto.getGuid(), payment.getGuid());
		assertEquals(paymentUpdateCreateDto.getCreatedAt(), payment.getCreatedAt());
		assertEquals(paymentUpdateCreateDto.getPaid(), payment.getPaid());
		assertEquals(paymentUpdateCreateDto.getUserId(), payment.getUser().getGuid());
		assertEquals(paymentUpdateCreateDto.getBudgetOfferPackageId(), payment.getBudgetOfferPackage().getGuid());
	}

	@Test
	void mapPaymentToPaymentUpdateCreateDto() {
		// Arrange
		Payment payment = PaymentTestData.getPayment();

		// Act
		PaymentUpdateCreateDto paymentUpdateCreateDto = mapper.paymentToPaymentUpdateCreateDto(payment);

		// Assert
		assertEquals(paymentUpdateCreateDto.getGuid(), payment.getGuid());
		assertEquals(paymentUpdateCreateDto.getCreatedAt(), payment.getCreatedAt());
		assertEquals(paymentUpdateCreateDto.getPaid(), payment.getPaid());
		assertEquals(paymentUpdateCreateDto.getUserId(), payment.getUser().getGuid());
		assertEquals(paymentUpdateCreateDto.getBudgetOfferPackageId(), payment.getBudgetOfferPackage().getGuid());
	}

	private void assertEqualsPaymentAndPaymentViewDto(Payment payment, PaymentViewDto paymentViewDto) {
		assertEquals(payment.getGuid(), paymentViewDto.getGuid());
		assertEquals(payment.getPaid(), paymentViewDto.getPaid());
		assertEquals(payment.getCreatedAt(), paymentViewDto.getCreatedAt());

		assertEquals(payment.getUser().getGuid(), paymentViewDto.getUser().getGuid());
		assertEquals(payment.getUser().getUsername(), paymentViewDto.getUser().getUsername());
		assertEquals(payment.getUser().getMail(), paymentViewDto.getUser().getMail());
		assertEquals(payment.getUser().getSurname(), paymentViewDto.getUser().getSurname());
		assertEquals(payment.getUser().getIsActive(), paymentViewDto.getUser().getIsActive());

		assertEquals(payment.getBudgetOfferPackage().getGuid(), paymentViewDto.getBudgetOfferPackage().getGuid());
		assertEquals(payment.getBudgetOfferPackage().getBudgetIncrease(),
				paymentViewDto.getBudgetOfferPackage().getBudgetIncrease());
		assertEquals(payment.getBudgetOfferPackage().getDescription(),
				paymentViewDto.getBudgetOfferPackage().getDescription());
		assertEquals(payment.getBudgetOfferPackage().getPriceDollars(),
				paymentViewDto.getBudgetOfferPackage().getPriceDollars());
	}

}
