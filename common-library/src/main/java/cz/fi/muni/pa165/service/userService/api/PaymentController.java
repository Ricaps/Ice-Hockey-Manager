package cz.fi.muni.pa165.service.userService.api;

import cz.fi.muni.pa165.dto.userService.PaymentUpdateCreateDto;
import cz.fi.muni.pa165.dto.userService.PaymentViewDto;

import java.util.List;
import java.util.UUID;

public interface PaymentController {

	PaymentViewDto getPaymentById(UUID paymentId);

	PaymentViewDto createPayment(PaymentUpdateCreateDto paymentDto);

	PaymentViewDto updatePayment(PaymentUpdateCreateDto paymentDto);

	List<PaymentViewDto> getAllPayments();

}
