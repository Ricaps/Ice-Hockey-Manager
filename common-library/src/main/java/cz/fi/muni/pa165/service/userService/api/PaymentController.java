package cz.fi.muni.pa165.service.userservice.api;

import cz.fi.muni.pa165.dto.userservice.PaymentUpdateCreateDto;
import cz.fi.muni.pa165.dto.userservice.PaymentViewDto;

import java.util.List;
import java.util.UUID;

public interface PaymentController {

	PaymentViewDto getPaymentById(UUID paymentId);

	PaymentViewDto createPayment(PaymentUpdateCreateDto paymentDto);

	PaymentViewDto updatePayment(PaymentUpdateCreateDto paymentDto);

	List<PaymentViewDto> getAllPayments();

}
