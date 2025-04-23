package cz.fi.muni.pa165.userservice.integration.api.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.userService.PaymentUpdateCreateDto;
import cz.fi.muni.pa165.dto.userService.PaymentViewDto;
import cz.fi.muni.pa165.messaging.BudgetChangeMessage;
import cz.fi.muni.pa165.userservice.business.mappers.PaymentMapper;
import cz.fi.muni.pa165.userservice.business.messages.BudgetUpdateMessageResolver;
import cz.fi.muni.pa165.userservice.business.services.PaymentService;
import cz.fi.muni.pa165.userservice.persistence.entities.Payment;
import cz.fi.muni.pa165.userservice.persistence.repositories.BudgetOfferPackageRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.PaymentRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PaymentControllerIT extends BaseControllerIT<PaymentRepository, Payment> {

	@MockitoBean
	private JmsTemplate jmsTemplate;

	private final ArgumentCaptor<BudgetChangeMessage> messageCaptor = ArgumentCaptor
		.forClass(BudgetChangeMessage.class);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BudgetOfferPackageRepository budgetOfferPackageRepository;

	@Autowired
	private PaymentMapper paymentMapper;

	@MockitoSpyBean
	PaymentService paymentService;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private BudgetUpdateMessageResolver budgetUpdateMessageResolver;

	private final PaymentRepository paymentRepository;

	@Autowired
	public PaymentControllerIT(PaymentRepository paymentRepository) {
		super(paymentRepository);
		this.paymentRepository = paymentRepository;
	}

	@Test
	void getPaymentById_whenPaymentNotFound_returnsNotFound() throws Exception {
		var nonExistingId = getNonExistingId();

		mockMvc.perform(get("/v1/payment/{id}", nonExistingId))
			.andExpect(status().isNotFound())
			.andExpect(content().string("\"Payment with id %s was not found\"".formatted(nonExistingId)));

		Mockito.verify(paymentService, Mockito.times(1)).getPaymentById(Mockito.eq(nonExistingId));
	}

	@Test
	void getPaymentById_whenInvalidUUID_returnsBadRequest() throws Exception {
		String invalidUuid = "not-a-uuid";

		mockMvc.perform(get("/v1/payment/{id}", invalidUuid)).andExpect(status().isBadRequest());
	}

	@Test
	void getPaymentById_whenValidRequest_returnsPayment() throws Exception {
		var exisingPayment = getExistingEntity();

		ResultActions result = mockMvc.perform(get("/v1/payment/{id}", exisingPayment.getGuid()))
			.andExpect(status().isOk());

		assertPaymentEquals(result, exisingPayment);
	}

	@Test
	void createPayment_whenValidRequestPaymentNotPaid_returnsCreatedPayment() throws Exception {
		var allPayments = getExistingEntities();
		var user = getExistingEntity(userRepository);
		var budgetPackage = getExistingEntity(budgetOfferPackageRepository);
		var paymentDto = getValidPaymentUpdateCreateDto(user.getGuid(), budgetPackage.getGuid());
		paymentDto.setPaid(false);

		ResultActions result = mockMvc
			.perform(post("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.guid").isNotEmpty())
			.andExpect(jsonPath("$.paid").value(paymentDto.getPaid()))
			.andExpect(jsonPath("$.user.guid").value(user.getGuid().toString()))
			.andExpect(jsonPath("$.budgetOfferPackage.guid").value(budgetPackage.getGuid().toString()));

		PaymentViewDto viewDto = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(),
				PaymentViewDto.class);

		Assertions.assertEquals(allPayments.size() + 1, getExistingEntities().size());
		Assertions.assertTrue(paymentRepository.existsById(viewDto.getGuid()));
		Mockito.verify(jmsTemplate, Mockito.times(0))
			.convertAndSend(Mockito.anyString(), Mockito.any(BudgetChangeMessage.class));
	}

	@Test
	void createPayment_whenValidRequestPaymentPaid_returnsCreatedPayment() throws Exception {
		var allPayments = getExistingEntities();
		var user = getExistingEntity(userRepository);
		var budgetPackage = getExistingEntity(budgetOfferPackageRepository);
		var paymentDto = getValidPaymentUpdateCreateDto(user.getGuid(), budgetPackage.getGuid());
		paymentDto.setPaid(true);

		ResultActions result = mockMvc
			.perform(post("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.guid").isNotEmpty())
			.andExpect(jsonPath("$.paid").value(paymentDto.getPaid()))
			.andExpect(jsonPath("$.user.guid").value(user.getGuid().toString()))
			.andExpect(jsonPath("$.budgetOfferPackage.guid").value(budgetPackage.getGuid().toString()));

		PaymentViewDto viewDto = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(),
				PaymentViewDto.class);

		Assertions.assertEquals(allPayments.size() + 1, getExistingEntities().size());
		Assertions.assertTrue(paymentRepository.existsById(viewDto.getGuid()));
		Mockito.verify(jmsTemplate, Mockito.times(1))
			.convertAndSend(Mockito.anyString(), Mockito.any(BudgetChangeMessage.class));
	}

	@Test
	void createPayment_whenUserOrPackageDoesNotExists_returnsNotFound() throws Exception {
		var allPayments = getExistingEntities();
		var userId = getExistingEntity(userRepository).getGuid();
		var budgetPackageNonExistingId = getNonExistingEntityId(budgetOfferPackageRepository);
		var paymentDto = getValidPaymentUpdateCreateDto(userId, budgetPackageNonExistingId);

		mockMvc
			.perform(post("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentDto)))
			.andExpect(status().isNotFound());

		Assertions.assertEquals(allPayments.size(), getExistingEntities().size());
		Mockito.verify(paymentService, Mockito.times(1)).createPayment(Mockito.any());
	}

	@Test
	void createPayment_whenNotValidPaymentSent_returnsBadRequest() throws Exception {
		var allPayments = getExistingEntities();
		var budgetPackage = getExistingEntity(budgetOfferPackageRepository);
		var paymentDto = getValidPaymentUpdateCreateDto(null, budgetPackage.getGuid());

		mockMvc
			.perform(post("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentDto)))
			.andExpect(status().isBadRequest());

		Assertions.assertEquals(allPayments.size(), getExistingEntities().size());
	}

	@Test
	void updatePayment_whenValidRequestWithoutPaidChange_returnsUpdatedPayment() throws Exception {
		var existingEntity = getExistingEntity();
		entityManager.detach(existingEntity);
		existingEntity.setPaid(existingEntity.getPaid());
		existingEntity.setCreatedAt(LocalDateTime.of(1892, 1, 3, 1, 1, 1, 1));
		existingEntity.setUser(userRepository.findAll()
			.stream()
			.filter(u -> !u.getGuid().equals(existingEntity.getUser().getGuid()))
			.findFirst()
			.orElseThrow());
		existingEntity.setBudgetOfferPackage(budgetOfferPackageRepository.findAll()
			.stream()
			.filter(p -> !p.getGuid().equals(existingEntity.getBudgetOfferPackage().getGuid()))
			.findFirst()
			.orElseThrow());

		PaymentUpdateCreateDto updateDto = paymentMapper.paymentToPaymentUpdateCreateDto(existingEntity);

		ResultActions result = mockMvc
			.perform(put("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk());

		assertPaymentEquals(result, existingEntity);
		var updatedEntity = paymentRepository.findById(existingEntity.getGuid()).orElseThrow();
		Assertions.assertEquals(existingEntity.getGuid(), updatedEntity.getGuid());
		Assertions.assertEquals(existingEntity.getPaid(), updatedEntity.getPaid());
		Assertions.assertEquals(existingEntity.getCreatedAt(), updatedEntity.getCreatedAt());
		Assertions.assertEquals(existingEntity.getUser().getGuid(), updatedEntity.getUser().getGuid());
		Assertions.assertEquals(existingEntity.getBudgetOfferPackage().getGuid(),
				updatedEntity.getBudgetOfferPackage().getGuid());
		Mockito.verify(jmsTemplate, Mockito.times(0))
			.convertAndSend(Mockito.anyString(), Mockito.any(BudgetChangeMessage.class));
	}

	@Test
	void updatePayment_whenValidRequestWithPaidChangingToFalse_returnsUpdatedPayment() throws Exception {
		var existingEntity = getFirstFilteredEntity(Payment::getPaid);
		entityManager.detach(existingEntity);
		existingEntity.setPaid(!existingEntity.getPaid());
		existingEntity.setCreatedAt(LocalDateTime.of(1892, 1, 3, 1, 1, 1, 1));
		existingEntity.setUser(userRepository.findAll()
			.stream()
			.filter(u -> !u.getGuid().equals(existingEntity.getUser().getGuid()))
			.findFirst()
			.orElseThrow());
		existingEntity.setBudgetOfferPackage(budgetOfferPackageRepository.findAll()
			.stream()
			.filter(p -> !p.getGuid().equals(existingEntity.getBudgetOfferPackage().getGuid()))
			.findFirst()
			.orElseThrow());

		PaymentUpdateCreateDto updateDto = paymentMapper.paymentToPaymentUpdateCreateDto(existingEntity);

		ResultActions result = mockMvc
			.perform(put("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk());

		assertPaymentEquals(result, existingEntity);
		var updatedEntity = paymentRepository.findById(existingEntity.getGuid()).orElseThrow();
		Assertions.assertEquals(existingEntity.getGuid(), updatedEntity.getGuid());
		Assertions.assertEquals(existingEntity.getPaid(), updatedEntity.getPaid());
		Assertions.assertEquals(existingEntity.getCreatedAt(), updatedEntity.getCreatedAt());
		Assertions.assertEquals(existingEntity.getUser().getGuid(), updatedEntity.getUser().getGuid());
		Assertions.assertEquals(existingEntity.getBudgetOfferPackage().getGuid(),
				updatedEntity.getBudgetOfferPackage().getGuid());
		Mockito.verify(jmsTemplate, Mockito.times(1)).convertAndSend(Mockito.anyString(), messageCaptor.capture());
		Assertions.assertEquals(-existingEntity.getBudgetOfferPackage().getBudgetIncrease(),
				messageCaptor.getValue().getAmount());
	}

	@Test
	void updatePayment_whenValidRequestWithPaidChangingToTrue_returnsUpdatedPayment() throws Exception {
		var existingEntity = getFirstFilteredEntity(p -> !p.getPaid());
		entityManager.detach(existingEntity);
		existingEntity.setPaid(!existingEntity.getPaid());
		existingEntity.setCreatedAt(LocalDateTime.of(1892, 1, 3, 1, 1, 1, 1));
		existingEntity.setUser(userRepository.findAll()
			.stream()
			.filter(u -> !u.getGuid().equals(existingEntity.getUser().getGuid()))
			.findFirst()
			.orElseThrow());
		existingEntity.setBudgetOfferPackage(budgetOfferPackageRepository.findAll()
			.stream()
			.filter(p -> !p.getGuid().equals(existingEntity.getBudgetOfferPackage().getGuid()))
			.findFirst()
			.orElseThrow());

		PaymentUpdateCreateDto updateDto = paymentMapper.paymentToPaymentUpdateCreateDto(existingEntity);

		ResultActions result = mockMvc
			.perform(put("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk());

		assertPaymentEquals(result, existingEntity);
		var updatedEntity = paymentRepository.findById(existingEntity.getGuid()).orElseThrow();
		Assertions.assertEquals(existingEntity.getGuid(), updatedEntity.getGuid());
		Assertions.assertEquals(existingEntity.getPaid(), updatedEntity.getPaid());
		Assertions.assertEquals(existingEntity.getCreatedAt(), updatedEntity.getCreatedAt());
		Assertions.assertEquals(existingEntity.getUser().getGuid(), updatedEntity.getUser().getGuid());
		Assertions.assertEquals(existingEntity.getBudgetOfferPackage().getGuid(),
				updatedEntity.getBudgetOfferPackage().getGuid());
		Mockito.verify(jmsTemplate, Mockito.times(1)).convertAndSend(Mockito.anyString(), messageCaptor.capture());
		Assertions.assertEquals(existingEntity.getBudgetOfferPackage().getBudgetIncrease(),
				messageCaptor.getValue().getAmount());
	}

	@Test
	void updatePayment_whenUserOrPackageDoesNotExists_returnsNotFound() throws Exception {
		var userId = getNonExistingEntityId(userRepository);
		var payment = getExistingEntity();
		var paymentDto = paymentMapper.paymentToPaymentUpdateCreateDto(payment);
		paymentDto.setUserId(userId);

		mockMvc
			.perform(put("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentDto)))
			.andExpect(status().isNotFound());

		Mockito.verify(paymentService, Mockito.times(1)).updatePayment(Mockito.any());
	}

	@Test
	void updatePayment_whenNotValidPaymentSent_returnsBadRequest() throws Exception {
		var payment = getExistingEntity();
		var paymentDto = paymentMapper.paymentToPaymentUpdateCreateDto(payment);
		paymentDto.setBudgetOfferPackageId(null);

		mockMvc
			.perform(put("/v1/payment/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentDto)))
			.andExpect(status().isBadRequest());

		Mockito.verify(paymentService, Mockito.times(1)).updatePayment(Mockito.any());
	}

	@Test
	void getAllPayments_whenValidRequest_returnsPaymentList() throws Exception {
		var allPayments = getExistingEntities();

		ResultActions result = mockMvc.perform(get("/v1/payment/"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(allPayments.size()));

		assertAllPaymentsEquals(result, allPayments);
	}

	private void assertAllPaymentsEquals(ResultActions result, List<Payment> payments) throws Exception {
		Assertions.assertFalse(payments.isEmpty());
		List<Map<String, Object>> jsonPayments = objectMapper
			.readValue(result.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
			});

		for (Map<String, Object> jsonPayment : jsonPayments) {
			UUID paymentId = UUID.fromString(jsonPayment.get("guid").toString());
			Payment expectedPayment = payments.stream()
				.filter(p -> p.getGuid().equals(paymentId))
				.findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Payment with guid " + paymentId + " not found!"));

			Assertions.assertEquals(expectedPayment.getPaid(), jsonPayment.get("paid"));
			Assertions.assertEquals(expectedPayment.getCreatedAt().truncatedTo(ChronoUnit.SECONDS),
					LocalDateTime.parse(jsonPayment.get("createdAt").toString()).truncatedTo(ChronoUnit.SECONDS));

			Map<String, Object> user = (Map<String, Object>) jsonPayment.get("user");
			Assertions.assertEquals(expectedPayment.getUser().getGuid().toString(), user.get("guid"));
			Assertions.assertEquals(expectedPayment.getUser().getName(), user.get("name"));
			Assertions.assertEquals(expectedPayment.getUser().getSurname(), user.get("surname"));
			Assertions.assertEquals(expectedPayment.getUser().getUsername(), user.get("username"));
			Assertions.assertEquals(expectedPayment.getUser().getMail(), user.get("mail"));
			Assertions.assertEquals(expectedPayment.getUser().getIsActive(), user.get("isActive"));

			Map<String, Object> budgetOfferPackage = (Map<String, Object>) jsonPayment.get("budgetOfferPackage");
			Assertions.assertEquals(expectedPayment.getBudgetOfferPackage().getGuid().toString(),
					budgetOfferPackage.get("guid"));
			Assertions.assertEquals(expectedPayment.getBudgetOfferPackage().getPriceDollars(),
					budgetOfferPackage.get("priceDollars"));
			Assertions.assertEquals(expectedPayment.getBudgetOfferPackage().getBudgetIncrease(),
					budgetOfferPackage.get("budgetIncrease"));
			Assertions.assertEquals(expectedPayment.getBudgetOfferPackage().getDescription(),
					budgetOfferPackage.get("description"));
			Assertions.assertEquals(expectedPayment.getBudgetOfferPackage().getIsAvailable(),
					budgetOfferPackage.get("isAvailable"));
		}
	}

	private void assertPaymentEquals(ResultActions result, Payment payment) throws Exception {
		result.andExpect(jsonPath("$.guid").value(payment.getGuid().toString()))
			.andExpect(jsonPath("$.paid").value(payment.getPaid()))
			.andExpect(res -> compareCreatedAt(res, "createdAt", payment.getCreatedAt()))
			.andExpect(jsonPath("$.user.guid").value(payment.getUser().getGuid().toString()))
			.andExpect(jsonPath("$.user.mail").value(payment.getUser().getMail()))
			.andExpect(jsonPath("$.user.username").value(payment.getUser().getUsername()))
			.andExpect(jsonPath("$.user.surname").value(payment.getUser().getSurname()))
			.andExpect(jsonPath("$.user.isActive").value(payment.getUser().getIsActive()))

			.andExpect(
					jsonPath("$.budgetOfferPackage.guid").value(payment.getBudgetOfferPackage().getGuid().toString()))
			.andExpect(jsonPath("$.budgetOfferPackage.budgetIncrease")
				.value(payment.getBudgetOfferPackage().getBudgetIncrease()))
			.andExpect(jsonPath("$.budgetOfferPackage.priceDollars")
				.value(payment.getBudgetOfferPackage().getPriceDollars()))
			.andExpect(jsonPath("$.budgetOfferPackage.isAvailable")
				.value(payment.getBudgetOfferPackage().getIsAvailable()));
	}

	private PaymentUpdateCreateDto getValidPaymentUpdateCreateDto(UUID userId, UUID budgetPackageId) {
		return PaymentUpdateCreateDto.builder()
			.guid(null)
			.createdAt(LocalDateTime.now())
			.paid(true)
			.userId(userId)
			.budgetOfferPackageId(budgetPackageId)
			.build();
	}

}
