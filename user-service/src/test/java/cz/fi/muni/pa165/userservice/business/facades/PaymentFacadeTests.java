package cz.fi.muni.pa165.userservice.business.facades;

import cz.fi.muni.pa165.dto.userService.PaymentUpdateCreateDto;
import cz.fi.muni.pa165.dto.userService.PaymentViewDto;
import cz.fi.muni.pa165.userservice.business.mappers.PaymentMapper;
import cz.fi.muni.pa165.userservice.business.services.PaymentService;
import cz.fi.muni.pa165.userservice.persistence.entities.Payment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PaymentFacadeTests {

	@Mock
	private PaymentService paymentService;

	@Mock
	private PaymentMapper paymentMapper;

	@InjectMocks
	private PaymentFacade paymentFacade;

	@Test
	public void getPaymentById_whenValid_shouldGetCorrectPayment() {
		// Arrange
		UUID paymentId = UUID.randomUUID();
		Payment payment = new Payment();
		payment.setGuid(paymentId);
		PaymentViewDto paymentViewDto = new PaymentViewDto();
		Mockito.when(paymentService.getPaymentById(paymentId)).thenReturn(payment);
		Mockito.when(paymentMapper.paymentToPaymentUserViewDto(payment)).thenReturn(paymentViewDto);

		// Act
		PaymentViewDto result = paymentFacade.getPaymentById(paymentId);

		// Assert
		assertEquals(paymentViewDto, result);
		Mockito.verify(paymentService, Mockito.times(1)).getPaymentById(paymentId);
		Mockito.verify(paymentMapper, Mockito.times(1)).paymentToPaymentUserViewDto(payment);
	}

	@Test
	public void createPayment_whenValid_shouldCreatePayment() {
		// Arrange
		PaymentUpdateCreateDto paymentDto = new PaymentUpdateCreateDto();
		paymentDto.setCreatedAt(LocalDateTime.now());
		Payment payment = new Payment();
		PaymentViewDto paymentViewDto = new PaymentViewDto();
		Mockito.when(paymentMapper.paymentUpdateCreateDtoToPayment(paymentDto)).thenReturn(payment);
		Mockito.when(paymentService.createPayment(payment)).thenReturn(payment);
		Mockito.when(paymentMapper.paymentToPaymentUserViewDto(payment)).thenReturn(paymentViewDto);

		// Act
		PaymentViewDto result = paymentFacade.createPayment(paymentDto);

		// Assert
		assertEquals(paymentViewDto, result);
		Mockito.verify(paymentMapper, Mockito.times(1)).paymentUpdateCreateDtoToPayment(paymentDto);
		Mockito.verify(paymentService, Mockito.times(1)).createPayment(payment);
		Mockito.verify(paymentMapper, Mockito.times(1)).paymentToPaymentUserViewDto(payment);
	}

	@Test
	public void updatePayment_whenValid_shouldUpdatePayment() {
		// Arrange
		PaymentUpdateCreateDto paymentDto = new PaymentUpdateCreateDto();
		Payment payment = new Payment();
		PaymentViewDto paymentViewDto = new PaymentViewDto();
		Mockito.when(paymentMapper.paymentUpdateCreateDtoToPayment(paymentDto)).thenReturn(payment);
		Mockito.when(paymentService.updatePayment(payment)).thenReturn(payment);
		Mockito.when(paymentMapper.paymentToPaymentUserViewDto(payment)).thenReturn(paymentViewDto);

		// Act
		PaymentViewDto result = paymentFacade.updatePayment(paymentDto);

		// Assert
		assertEquals(paymentViewDto, result);
		Mockito.verify(paymentMapper, Mockito.times(1)).paymentUpdateCreateDtoToPayment(paymentDto);
		Mockito.verify(paymentService, Mockito.times(1)).updatePayment(payment);
		Mockito.verify(paymentMapper, Mockito.times(1)).paymentToPaymentUserViewDto(payment);
	}

	@Test
	public void getAllPayments_whenValid_shouldGetAllPayments() {
		// Arrange
		Payment payment = new Payment();
		PaymentViewDto paymentViewDto = new PaymentViewDto();
		Mockito.when(paymentService.getAllPayments()).thenReturn(Collections.singletonList(payment));
		Mockito.when(paymentMapper.paymentToPaymentUserViewDto(payment)).thenReturn(paymentViewDto);

		// Act
		List<PaymentViewDto> result = paymentFacade.getAllPayments();

		// Assert
		assertEquals(Collections.singletonList(paymentViewDto), result);
		Mockito.verify(paymentService, Mockito.times(1)).getAllPayments();
		Mockito.verify(paymentMapper, Mockito.times(1)).paymentToPaymentUserViewDto(payment);
	}

}
