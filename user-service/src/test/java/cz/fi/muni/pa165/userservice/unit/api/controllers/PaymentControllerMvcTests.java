package cz.fi.muni.pa165.userservice.unit.api.controllers;

import cz.fi.muni.pa165.dto.userService.PaymentUpdateCreateDto;
import cz.fi.muni.pa165.dto.userService.PaymentViewDto;
import cz.fi.muni.pa165.service.userService.api.PaymentController;
import cz.fi.muni.pa165.userservice.api.exception.BlankValueException;
import cz.fi.muni.pa165.userservice.business.facades.PaymentFacade;
import cz.fi.muni.pa165.userservice.security.SecurityConfig;
import cz.fi.muni.pa165.userservice.unit.testData.PaymentTestData;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@Import(SecurityConfig.class)
class PaymentControllerMvcTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PaymentFacade paymentFacade;

	@Autowired
	private ObjectMapper objectMapper;

	private PaymentViewDto paymentViewDto;

	private PaymentUpdateCreateDto paymentUpdateCreateDto;

	private UUID paymentId;

	@BeforeEach
	void setUp() {
		paymentId = UUID.randomUUID();
		paymentViewDto = PaymentTestData.getPaymentViewDto();
		paymentViewDto.setGuid(paymentId);
		paymentUpdateCreateDto = PaymentTestData.getPaymentUpdateCreateDto();
	}

	@Test
	void getPaymentById_whenPaymentNotFound_returnsNotFound() throws Exception {
		Mockito.when(paymentFacade.getPaymentById(paymentId)).thenThrow(EntityNotFoundException.class);

		doThrow(new EntityNotFoundException("Payment not found")).when(paymentFacade).getPaymentById(paymentId);

		mockMvc.perform(get("/v1/payment/{id}", paymentId))
			.andExpect(status().isNotFound())
			.andExpect(content().string("\"Payment not found\""));

		Mockito.verify(paymentFacade, Mockito.times(1)).getPaymentById(paymentId);
	}

	@Test
	void getPaymentById_whenInvalidUUID_returnsBadRequest() throws Exception {
		String invalidUuid = "not-a-uuid";

		mockMvc.perform(get("/v1/payment/{id}", invalidUuid)).andExpect(status().isBadRequest());

		Mockito.verify(paymentFacade, Mockito.never()).getPaymentById(any());
	}

	@Test
	void getPaymentById_whenValidRequest_returnsPayment() throws Exception {
		Mockito.when(paymentFacade.getPaymentById(paymentId)).thenReturn(paymentViewDto);
		Mockito.when(paymentFacade.getPaymentById(paymentId)).thenReturn(paymentViewDto);

		mockMvc.perform(get("/v1/payment/{id}", paymentId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(paymentId.toString()))
			.andExpect(jsonPath("$.paid").value(paymentViewDto.getPaid()))
			.andExpect(jsonPath("$.createdAt").value(paymentViewDto.getCreatedAt().toString()))

			.andExpect(jsonPath("$.user.guid").value(paymentViewDto.getUser().getGuid().toString()))
			.andExpect(jsonPath("$.user.mail").value(paymentViewDto.getUser().getMail()))
			.andExpect(jsonPath("$.user.username").value(paymentViewDto.getUser().getUsername()))
			.andExpect(jsonPath("$.user.surname").value(paymentViewDto.getUser().getSurname()))
			.andExpect(jsonPath("$.user.isActive").value(paymentViewDto.getUser().getIsActive()))

			.andExpect(jsonPath("$.budgetOfferPackage.guid")
				.value(paymentViewDto.getBudgetOfferPackage().getGuid().toString()))
			.andExpect(jsonPath("$.budgetOfferPackage.budgetIncrease")
				.value(paymentViewDto.getBudgetOfferPackage().getBudgetIncrease()))
			.andExpect(jsonPath("$.budgetOfferPackage.priceDollars")
				.value(paymentViewDto.getBudgetOfferPackage().getPriceDollars()))
			.andExpect(jsonPath("$.budgetOfferPackage.isAvailable")
				.value(paymentViewDto.getBudgetOfferPackage().getIsAvailable()));

		Mockito.verify(paymentFacade, Mockito.times(1)).getPaymentById(paymentId);
	}

	@Test
	void createPayment_whenValidRequest_returnsCreatedPayment() throws Exception {
		Mockito.when(paymentFacade.createPayment(any(PaymentUpdateCreateDto.class))).thenReturn(paymentViewDto);

		mockMvc
			.perform(post("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentUpdateCreateDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.guid").value(paymentId.toString()))
			.andExpect(jsonPath("$.paid").value(paymentViewDto.getPaid()))
			.andExpect(jsonPath("$.createdAt").value(paymentViewDto.getCreatedAt().toString()))

			.andExpect(jsonPath("$.user.guid").value(paymentViewDto.getUser().getGuid().toString()))
			.andExpect(jsonPath("$.user.mail").value(paymentViewDto.getUser().getMail()))
			.andExpect(jsonPath("$.user.username").value(paymentViewDto.getUser().getUsername()))
			.andExpect(jsonPath("$.user.surname").value(paymentViewDto.getUser().getSurname()))
			.andExpect(jsonPath("$.user.isActive").value(paymentViewDto.getUser().getIsActive()))

			.andExpect(jsonPath("$.budgetOfferPackage.guid")
				.value(paymentViewDto.getBudgetOfferPackage().getGuid().toString()))
			.andExpect(jsonPath("$.budgetOfferPackage.budgetIncrease")
				.value(paymentViewDto.getBudgetOfferPackage().getBudgetIncrease()))
			.andExpect(jsonPath("$.budgetOfferPackage.priceDollars")
				.value(paymentViewDto.getBudgetOfferPackage().getPriceDollars()))
			.andExpect(jsonPath("$.budgetOfferPackage.isAvailable")
				.value(paymentViewDto.getBudgetOfferPackage().getIsAvailable()));

		Mockito.verify(paymentFacade, Mockito.times(1)).createPayment(any(PaymentUpdateCreateDto.class));
	}

	@Test
	void createPayment_whenUserOrPackageDoesNotExists_returnsNotFound() throws Exception {
		Mockito.when(paymentFacade.createPayment(any(PaymentUpdateCreateDto.class)))
			.thenThrow(EntityNotFoundException.class);

		mockMvc
			.perform(post("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentUpdateCreateDto)))
			.andExpect(status().isNotFound());

		Mockito.verify(paymentFacade, Mockito.times(1)).createPayment(any(PaymentUpdateCreateDto.class));
	}

	@Test
	void createPayment_whenNotValidPaymentSent_returnsBadRequest() throws Exception {
		Mockito.when(paymentFacade.createPayment(any(PaymentUpdateCreateDto.class)))
			.thenThrow(BlankValueException.class);

		mockMvc
			.perform(post("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentUpdateCreateDto)))
			.andExpect(status().isBadRequest());

		Mockito.verify(paymentFacade, Mockito.times(1)).createPayment(any(PaymentUpdateCreateDto.class));
	}

	@Test
	void updatePayment_whenValidReques_returnsUpdatedPayment() throws Exception {
		Mockito.when(paymentFacade.updatePayment(any(PaymentUpdateCreateDto.class))).thenReturn(paymentViewDto);

		mockMvc
			.perform(put("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentUpdateCreateDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(paymentId.toString()))
			.andExpect(jsonPath("$.paid").value(paymentViewDto.getPaid()))
			.andExpect(jsonPath("$.createdAt").value(paymentViewDto.getCreatedAt().toString()))

			.andExpect(jsonPath("$.user.guid").value(paymentViewDto.getUser().getGuid().toString()))
			.andExpect(jsonPath("$.user.mail").value(paymentViewDto.getUser().getMail()))
			.andExpect(jsonPath("$.user.username").value(paymentViewDto.getUser().getUsername()))
			.andExpect(jsonPath("$.user.surname").value(paymentViewDto.getUser().getSurname()))
			.andExpect(jsonPath("$.user.isActive").value(paymentViewDto.getUser().getIsActive()))

			.andExpect(jsonPath("$.budgetOfferPackage.guid")
				.value(paymentViewDto.getBudgetOfferPackage().getGuid().toString()))
			.andExpect(jsonPath("$.budgetOfferPackage.budgetIncrease")
				.value(paymentViewDto.getBudgetOfferPackage().getBudgetIncrease()))
			.andExpect(jsonPath("$.budgetOfferPackage.priceDollars")
				.value(paymentViewDto.getBudgetOfferPackage().getPriceDollars()))
			.andExpect(jsonPath("$.budgetOfferPackage.isAvailable")
				.value(paymentViewDto.getBudgetOfferPackage().getIsAvailable()));

		Mockito.verify(paymentFacade, Mockito.times(1)).updatePayment(any(PaymentUpdateCreateDto.class));
	}

	@Test
	void updatePayment_whenUserOrPackageDoesNotExists_returnsNotFound() throws Exception {
		Mockito.when(paymentFacade.updatePayment(any(PaymentUpdateCreateDto.class)))
			.thenThrow(EntityNotFoundException.class);

		mockMvc
			.perform(put("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentUpdateCreateDto)))
			.andExpect(status().isNotFound());

		Mockito.verify(paymentFacade, Mockito.times(1)).updatePayment(any(PaymentUpdateCreateDto.class));
	}

	@Test
	void updatePayment_whenNotValidPaymentSent_returnsBadRequest() throws Exception {
		Mockito.when(paymentFacade.updatePayment(any(PaymentUpdateCreateDto.class)))
			.thenThrow(BlankValueException.class);

		mockMvc
			.perform(put("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentUpdateCreateDto)))
			.andExpect(status().isBadRequest());

		Mockito.verify(paymentFacade, Mockito.times(1)).updatePayment(any(PaymentUpdateCreateDto.class));
	}

	@Test
	void getAllPayments_whenValidRequest_returnsPaymentList() throws Exception {
		Mockito.when(paymentFacade.getAllPayments()).thenReturn(List.of(paymentViewDto));

		mockMvc.perform(get("/v1/payment/"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1))
			.andExpect(jsonPath("$[0].guid").value(paymentId.toString()))
			.andExpect(jsonPath("$[0].paid").value(paymentViewDto.getPaid()))
			.andExpect(jsonPath("$[0].createdAt").value(paymentViewDto.getCreatedAt().toString()))
			.andExpect(jsonPath("$[0].user.guid").value(paymentViewDto.getUser().getGuid().toString()))
			.andExpect(jsonPath("$[0].user.mail").value(paymentViewDto.getUser().getMail()))
			.andExpect(jsonPath("$[0].user.username").value(paymentViewDto.getUser().getUsername()))
			.andExpect(jsonPath("$[0].user.surname").value(paymentViewDto.getUser().getSurname()))
			.andExpect(jsonPath("$[0].user.isActive").value(paymentViewDto.getUser().getIsActive()))
			.andExpect(jsonPath("$[0].budgetOfferPackage.guid")
				.value(paymentViewDto.getBudgetOfferPackage().getGuid().toString()))
			.andExpect(jsonPath("$[0].budgetOfferPackage.budgetIncrease")
				.value(paymentViewDto.getBudgetOfferPackage().getBudgetIncrease()))
			.andExpect(jsonPath("$[0].budgetOfferPackage.priceDollars")
				.value(paymentViewDto.getBudgetOfferPackage().getPriceDollars()))
			.andExpect(jsonPath("$[0].budgetOfferPackage.isAvailable")
				.value(paymentViewDto.getBudgetOfferPackage().getIsAvailable()));
	}

}
