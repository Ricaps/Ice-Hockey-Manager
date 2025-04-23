package cz.fi.muni.pa165.userservice.unit.business.service;

import cz.fi.muni.pa165.userservice.api.exception.BlankValueException;
import cz.fi.muni.pa165.userservice.business.messages.BudgetUpdateMessageResolver;
import cz.fi.muni.pa165.userservice.business.services.PaymentService;
import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import cz.fi.muni.pa165.userservice.persistence.entities.Payment;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import cz.fi.muni.pa165.userservice.persistence.repositories.BudgetOfferPackageRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.PaymentRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserRepository;
import cz.fi.muni.pa165.userservice.unit.testData.BudgetOfferPackageTestData;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTests {

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BudgetOfferPackageRepository budgetOfferPackageRepository;

	@Mock
	private BudgetUpdateMessageResolver budgetIncreaseMessageResolver;

	@InjectMocks
	private PaymentService paymentService;

	@Test
	void getPaymentById_whenIdIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> paymentService.getPaymentById(null));
		Mockito.verify(paymentRepository, Mockito.never()).findById(Mockito.any(UUID.class));
	}

	@Test
	void getPaymentById_whenPaymentIsNotFound_shouldThrowEntityNotFoundException() {
		// Arrange
		UUID paymentId = UUID.randomUUID();
		Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> paymentService.getPaymentById(paymentId));
	}

	@Test
	void getPaymentById_whenPaymentIsFound_shouldReturnPayment() {
		// Arrange
		UUID paymentId = UUID.randomUUID();
		Payment payment = getPayment();
		payment.setGuid(paymentId);
		Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

		// Act
		Payment result = paymentService.getPaymentById(paymentId);

		// Assert
		assertNotNull(result);
		assertEquals(paymentId, result.getGuid());
		Mockito.verify(paymentRepository, Mockito.times(1)).findById(paymentId);
	}

	@Test
	void createPayment_whenPaymentIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> paymentService.createPayment(null));
		Mockito.verify(paymentRepository, Mockito.never()).save(Mockito.any(Payment.class));
	}

	@Test
	void createPayment_whenPaymentHasGuid_shouldThrowIllegalArgumentException() {
		// Arrange
		Payment paymentToCreate = getPayment();
		Mockito.when(userRepository.existsById(paymentToCreate.getUser().getGuid())).thenReturn(true);
		Mockito.when(budgetOfferPackageRepository.existsById(paymentToCreate.getBudgetOfferPackage().getGuid()))
			.thenReturn(true);

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> paymentService.createPayment(paymentToCreate));
		Mockito.verify(paymentRepository, Mockito.never()).save(Mockito.any(Payment.class));
	}

	@Test
	void createPayment_whenUserIsNull_shouldThrowBlankValueException() {
		// Arrange
		Payment paymentToCreate = getPayment();
		paymentToCreate.setUser(null);

		// Act & Assert
		assertThrows(BlankValueException.class, () -> paymentService.createPayment(paymentToCreate));
		Mockito.verify(paymentRepository, Mockito.never()).save(Mockito.any(Payment.class));
	}

	@Test
	void createPayment_whenUserGuidIsNull_shouldThrowBlankValueException() {
		// Arrange
		Payment paymentToCreate = getPayment();
		paymentToCreate.getUser().setGuid(null);

		// Act & Assert
		assertThrows(BlankValueException.class, () -> paymentService.createPayment(paymentToCreate));
		Mockito.verify(paymentRepository, Mockito.never()).save(Mockito.any(Payment.class));
	}

	@Test
	void createPayment_whenBudgetOfferPackageIsNull_shouldThrowBlankValueException() {
		// Arrange
		Payment paymentToCreate = getPayment();
		paymentToCreate.setBudgetOfferPackage(null);

		// Act & Assert
		assertThrows(BlankValueException.class, () -> paymentService.createPayment(paymentToCreate));
		Mockito.verify(paymentRepository, Mockito.never()).save(Mockito.any(Payment.class));
	}

	@Test
	void createPayment_whenBudgetOfferPackageGuidIsNull_shouldThrowBlankValueException() {
		// Arrange
		Payment paymentToCreate = getPayment();
		paymentToCreate.getBudgetOfferPackage().setGuid(null);

		// Act & Assert
		assertThrows(BlankValueException.class, () -> paymentService.createPayment(paymentToCreate));
		Mockito.verify(paymentRepository, Mockito.never()).save(Mockito.any(Payment.class));
	}

	@Test
	void createPayment_whenValidPaymentIsGivenAndPaymentNotPaid_shouldSavePayment() {
		// Arrange
		Payment paymentToCreate = getPayment();
		paymentToCreate.setGuid(null);
		paymentToCreate.setPaid(false);
		Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(paymentToCreate);
		Mockito.when(userRepository.existsById(Mockito.any(UUID.class))).thenReturn(true);
		Mockito.when(budgetOfferPackageRepository.existsById(Mockito.any(UUID.class))).thenReturn(true);

		// Act
		Payment savedPayment = paymentService.createPayment(paymentToCreate);

		// Assert
		assertNotNull(savedPayment);
		Mockito.verify(paymentRepository, Mockito.times(1)).save(Mockito.any(Payment.class));
		Mockito.verify(budgetIncreaseMessageResolver, Mockito.times(0))
			.sendBudgetChangeMessage(Mockito.any(UUID.class), Mockito.anyInt());
		Mockito.verify(budgetOfferPackageRepository, Mockito.times(0)).findById(Mockito.any(UUID.class));
	}

	@Test
	void createPayment_whenValidPaymentIsGivenAndPaymentPaid_shouldSavePayment() {
		// Arrange
		Payment paymentToCreate = getPayment();
		paymentToCreate.setGuid(null);
		paymentToCreate.setPaid(true);
		Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(paymentToCreate);
		Mockito.when(userRepository.existsById(Mockito.any(UUID.class))).thenReturn(true);
		Mockito.when(budgetOfferPackageRepository.existsById(Mockito.any(UUID.class))).thenReturn(true);
		Mockito.when(budgetOfferPackageRepository.findById(Mockito.any(UUID.class)))
			.thenReturn(Optional.of(BudgetOfferPackageTestData.getBudgetOfferPackage()));

		// Act
		Payment savedPayment = paymentService.createPayment(paymentToCreate);

		// Assert
		assertNotNull(savedPayment);
		Mockito.verify(paymentRepository, Mockito.times(1)).save(Mockito.any(Payment.class));
		Mockito.verify(budgetIncreaseMessageResolver, Mockito.times(1))
			.sendBudgetChangeMessage(Mockito.any(UUID.class), Mockito.anyInt());
		Mockito.verify(budgetOfferPackageRepository, Mockito.times(1)).findById(Mockito.any(UUID.class));
	}

	@Test
	void updatePayment_whenPaymentIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> paymentService.updatePayment(null));
		Mockito.verify(paymentRepository, Mockito.never()).save(Mockito.any(Payment.class));
	}

	@Test
	void updatePayment_whenPaymentUserIsNull_shouldThrowBlankValueException() {
		// Arrange
		Payment paymentToUpdate = getPayment();
		paymentToUpdate.setUser(null);

		// Act & Assert
		assertThrows(BlankValueException.class, () -> paymentService.updatePayment(paymentToUpdate));
		Mockito.verify(paymentRepository, Mockito.never()).save(Mockito.any(Payment.class));
	}

	@Test
	void updatePayment_whenPaymentUserGuidIsNull_shouldThrowBlankValueException() {
		// Arrange
		Payment paymentToUpdate = getPayment();
		paymentToUpdate.getUser().setGuid(null);

		// Act & Assert
		assertThrows(BlankValueException.class, () -> paymentService.updatePayment(paymentToUpdate));
		Mockito.verify(paymentRepository, Mockito.never()).save(Mockito.any(Payment.class));
	}

	@Test
	void updatePayment_whenBudgetPackageOfferIsNull_shouldThrowBlankValueException() {
		// Arrange
		Payment paymentToUpdate = getPayment();
		paymentToUpdate.setBudgetOfferPackage(null);

		// Act & Assert
		assertThrows(BlankValueException.class, () -> paymentService.updatePayment(paymentToUpdate));
		Mockito.verify(paymentRepository, Mockito.never()).save(Mockito.any(Payment.class));
	}

	@Test
	void updatePayment_whenBudgetPackageOfferGuidIsNull_shouldThrowBlankValueException() {
		// Arrange
		Payment paymentToUpdate = getPayment();
		paymentToUpdate.getBudgetOfferPackage().setGuid(null);

		// Act & Assert
		assertThrows(BlankValueException.class, () -> paymentService.updatePayment(paymentToUpdate));
		Mockito.verify(paymentRepository, Mockito.never()).save(Mockito.any(Payment.class));
	}

	@Test
	void updatePayment_whenValidPaymentIsGivenWithNoPaidChange_shouldSavePayment() {
		// Arrange
		Payment paymentToUpdate = getPayment();
		paymentToUpdate.setGuid(UUID.randomUUID());
		Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(paymentToUpdate);
		Mockito.when(paymentRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(paymentToUpdate));
		Mockito.when(userRepository.existsById(Mockito.any(UUID.class))).thenReturn(true);
		Mockito.when(budgetOfferPackageRepository.existsById(Mockito.any(UUID.class))).thenReturn(true);

		// Act
		Payment updatedPayment = paymentService.updatePayment(paymentToUpdate);

		// Assert
		assertNotNull(updatedPayment);
		Mockito.verify(paymentRepository, Mockito.times(1)).save(Mockito.any(Payment.class));
		Mockito.verify(budgetOfferPackageRepository, Mockito.times(0)).findById(Mockito.any(UUID.class));
		Mockito.verify(budgetIncreaseMessageResolver, Mockito.times(0))
			.sendBudgetChangeMessage(Mockito.any(UUID.class), Mockito.anyInt());
	}

	@Test
	void updatePayment_whenValidPaymentIsGivenWithPaidChangingToTrue_shouldSavePayment() {
		// Arrange
		Payment paymentToUpdate = getPayment();
		paymentToUpdate.setGuid(UUID.randomUUID());
		paymentToUpdate.setPaid(false);
		Payment updatedPayment = getPayment();
		paymentToUpdate.setGuid(paymentToUpdate.getGuid());
		updatedPayment.setPaid(true);

		Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(updatedPayment);
		Mockito.when(paymentRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(paymentToUpdate));
		Mockito.when(userRepository.existsById(Mockito.any(UUID.class))).thenReturn(true);
		Mockito.when(budgetOfferPackageRepository.existsById(Mockito.any(UUID.class))).thenReturn(true);
		Mockito.when(budgetOfferPackageRepository.findById(Mockito.any(UUID.class)))
			.thenReturn(Optional.of(BudgetOfferPackageTestData.getBudgetOfferPackage()));

		// Act
		Payment receivedPayment = paymentService.updatePayment(paymentToUpdate);

		// Assert
		assertNotNull(receivedPayment);
		Mockito.verify(paymentRepository, Mockito.times(1)).save(Mockito.any(Payment.class));
		Mockito.verify(budgetOfferPackageRepository, Mockito.times(1)).findById(Mockito.any(UUID.class));
		Mockito.verify(budgetIncreaseMessageResolver, Mockito.times(1))
			.sendBudgetChangeMessage(Mockito.any(UUID.class), Mockito.anyInt());
	}

	@Test
	void updatePayment_whenValidPaymentIsGivenWithPaidChangingToFalse_shouldSavePayment() {
		// Arrange
		Payment paymentToUpdate = getPayment();
		paymentToUpdate.setGuid(UUID.randomUUID());
		paymentToUpdate.setPaid(true);
		Payment updatedPayment = getPayment();
		paymentToUpdate.setGuid(paymentToUpdate.getGuid());
		updatedPayment.setPaid(false);

		Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(updatedPayment);
		Mockito.when(paymentRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(paymentToUpdate));
		Mockito.when(userRepository.existsById(Mockito.any(UUID.class))).thenReturn(true);
		Mockito.when(budgetOfferPackageRepository.existsById(Mockito.any(UUID.class))).thenReturn(true);
		Mockito.when(budgetOfferPackageRepository.findById(Mockito.any(UUID.class)))
			.thenReturn(Optional.of(BudgetOfferPackageTestData.getBudgetOfferPackage()));

		// Act
		Payment receivedPayment = paymentService.updatePayment(paymentToUpdate);

		// Assert
		assertNotNull(receivedPayment);
		Mockito.verify(paymentRepository, Mockito.times(1)).save(Mockito.any(Payment.class));
		Mockito.verify(budgetOfferPackageRepository, Mockito.times(1)).findById(Mockito.any(UUID.class));
		Mockito.verify(budgetIncreaseMessageResolver, Mockito.times(1))
			.sendBudgetChangeMessage(Mockito.any(UUID.class), Mockito.anyInt());
	}

	@Test
	void getAllPayments_whenPaymentsExist_shouldReturnPayments() {
		// Arrange
		Mockito.when(paymentRepository.findAll()).thenReturn(List.of(new Payment()));

		// Act
		var payments = paymentService.getAllPayments();

		// Assert
		assertNotNull(payments);
		assertFalse(payments.isEmpty());
		Mockito.verify(paymentRepository, Mockito.times(1)).findAll();
	}

	private User getUser() {
		return User.builder()
			.isActive(true)
			.guid(UUID.randomUUID())
			.mail("adfdasf@sadfsf.bmn")
			.deletedAt(null)
			.passwordHash("sadfadfasd")
			.name("AAAAA")
			.surname("BBBBBB")
			.build();
	}

	private BudgetOfferPackage getBudgetOfferPackage() {
		return BudgetOfferPackage.builder()
			.guid(UUID.randomUUID())
			.isAvailable(true)
			.priceDollars(100)
			.budgetIncrease(50)
			.description("asdf")
			.build();
	}

	private Payment getPayment() {
		return Payment.builder()
			.guid(UUID.randomUUID())
			.user(getUser())
			.budgetOfferPackage(getBudgetOfferPackage())
			.paid(false)
			.createdAt(null)
			.build();
	}

}
