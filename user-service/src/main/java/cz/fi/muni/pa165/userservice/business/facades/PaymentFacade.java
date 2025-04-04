package cz.fi.muni.pa165.userservice.business.facades;

import cz.fi.muni.pa165.dto.userService.PaymentUpdateCreateDto;
import cz.fi.muni.pa165.dto.userService.PaymentViewDto;
import cz.fi.muni.pa165.userservice.business.mappers.PaymentMapper;
import cz.fi.muni.pa165.userservice.business.services.PaymentService;
import cz.fi.muni.pa165.userservice.persistence.entities.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentFacade {

	private final PaymentService paymentService;

	private final PaymentMapper paymentMapper;

	@Autowired
	public PaymentFacade(PaymentService paymentService, PaymentMapper paymentMapper) {
		this.paymentService = paymentService;
		this.paymentMapper = paymentMapper;
	}

	public PaymentViewDto getPaymentById(UUID paymentId) {
		return paymentMapper.paymentToPaymentUserViewDto(paymentService.getPaymentById(paymentId));
	}

	public PaymentViewDto createPayment(PaymentUpdateCreateDto paymentDto) {
		paymentDto.setCreatedAt(LocalDateTime.now());
		final Payment payment = paymentMapper.paymentUpdateCreateDtoToPayment(paymentDto);
		return paymentMapper.paymentToPaymentUserViewDto(paymentService.createPayment(payment));
	}

	public PaymentViewDto updatePayment(PaymentUpdateCreateDto paymentDto) {
		final Payment payment = paymentMapper.paymentUpdateCreateDtoToPayment(paymentDto);
		return paymentMapper.paymentToPaymentUserViewDto(paymentService.updatePayment(payment));
	}

	public List<PaymentViewDto> getAllPayments() {
		final List<Payment> payments = paymentService.getAllPayments();
		return payments.stream().map(paymentMapper::paymentToPaymentUserViewDto).toList();
	}

}
