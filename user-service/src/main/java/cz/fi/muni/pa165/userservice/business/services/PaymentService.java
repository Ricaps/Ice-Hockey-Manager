package cz.fi.muni.pa165.userservice.business.services;

import cz.fi.muni.pa165.userservice.api.exception.ValidationUtil;
import cz.fi.muni.pa165.userservice.business.messages.BudgetUpdateMessageResolver;
import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import cz.fi.muni.pa165.userservice.persistence.entities.Payment;
import cz.fi.muni.pa165.userservice.persistence.repositories.BudgetOfferPackageRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.PaymentRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService extends EntityServiceBase<Payment> {

	private final PaymentRepository paymentRepository;

	private final UserRepository userRepository;

	private final BudgetOfferPackageRepository budgetOfferPackageRepository;

	private final BudgetUpdateMessageResolver budgetIncreaseMessageResolver;

	@Autowired
	public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository,
			BudgetOfferPackageRepository budgetOfferPackageRepository,
			BudgetUpdateMessageResolver budgetIncreaseMessageResolver) {
		this.paymentRepository = paymentRepository;
		this.userRepository = userRepository;
		this.budgetOfferPackageRepository = budgetOfferPackageRepository;
		this.budgetIncreaseMessageResolver = budgetIncreaseMessageResolver;
	}

	@Transactional
	public Payment getPaymentById(UUID paymentId) {
		ValidationUtil.requireNotNull(paymentId, "You must provide a payment id, provided id is null");
		final var payment = paymentRepository.findById(paymentId);

		return payment
			.orElseThrow(() -> new EntityNotFoundException("Payment with id " + paymentId + " was not found"));
	}

	@Transactional
	public Payment createPayment(@Valid Payment payment) {
		validatePayment(payment);
		ValidationUtil.requireNull(payment.getGuid(), "ID must be null when creating a new payment!");

		payment = paymentRepository.save(payment);
		if (payment.getPaid()) {
			sendIncreaseBudgetMessage(payment);
		}

		return payment;
	}

	@Transactional
	public Payment updatePayment(@Valid Payment payment) {
		validatePayment(payment);
		ValidationUtil.requireNotNull(payment, "Payment is required! Provided payment is null.");
		boolean wasPaid = paymentRepository.findById(payment.getGuid()).get().getPaid();
		payment = paymentRepository.save(payment);

		if (wasPaid != payment.getPaid()) {
			sendIncreaseBudgetMessage(payment);
		}

		return payment;
	}

	@Transactional
	public List<Payment> getAllPayments() {
		return paymentRepository.findAll();
	}

	private void validatePayment(Payment payment) {
		ValidationUtil.requireNotNull(payment, "Payment is required!");
		ValidationUtil.requireNotNull(payment.getUser(), "Payment user is required!");
		ValidationUtil.requireNotNull(payment.getUser().getGuid(), "Payment user guid is required!");
		ValidationUtil.requireNotNull(payment.getBudgetOfferPackage(), "Payment budget offer package is required!");
		ValidationUtil.requireNotNull(payment.getBudgetOfferPackage().getGuid(),
				"Payment budget offer package guid is required!");

		if (!userRepository.existsById(payment.getUser().getGuid())) {
			throw new EntityNotFoundException("User " + payment.getUser().getGuid() + " was not found");
		}

		if (!budgetOfferPackageRepository.existsById(payment.getBudgetOfferPackage().getGuid())) {
			throw new EntityNotFoundException(
					"Budget offer package " + payment.getBudgetOfferPackage().getGuid() + " was not found");
		}
	}

	private void sendIncreaseBudgetMessage(Payment payment) {
		BudgetOfferPackage pkg = budgetOfferPackageRepository.findById(payment.getBudgetOfferPackage().getGuid())
			.orElseThrow();
		int increase = pkg.getBudgetIncrease() * (payment.getPaid() ? 1 : -1);

		budgetIncreaseMessageResolver.sendBudgetChangeMessage(payment.getUser().getGuid(), increase);
	}

}
