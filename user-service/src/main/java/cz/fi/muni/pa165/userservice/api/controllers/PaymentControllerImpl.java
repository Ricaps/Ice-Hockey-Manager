package cz.fi.muni.pa165.userservice.api.controllers;

import cz.fi.muni.pa165.dto.userService.PaymentUpdateCreateDto;
import cz.fi.muni.pa165.dto.userService.PaymentViewDto;
import cz.fi.muni.pa165.service.userService.api.api.PaymentController;
import cz.fi.muni.pa165.userservice.business.facades.PaymentFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/payment")
@Tag(name = "Payment API", description = "Operations related to payments.")
public class PaymentControllerImpl implements PaymentController {

	private final PaymentFacade paymentFacade;

	@Autowired
	public PaymentControllerImpl(PaymentFacade paymentFacade) {
		this.paymentFacade = paymentFacade;
	}

	@Override
	@Operation(description = "Returns desired payment by its id.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Payment view with desired details.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = PaymentViewDto.class))),
					@ApiResponse(responseCode = "404", description = "Desired payment does not exist",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))),
					@ApiResponse(responseCode = "400", description = "Path parameter does not have a form of UUID",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))) },
			parameters = @Parameter(name = "id", description = "UUID of desired payment", required = true))
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PaymentViewDto getPaymentById(@PathVariable UUID id) {
		return paymentFacade.getPaymentById(id);
	}

	@Override
	@Operation(description = "Creates a new Payment with given properties",
			responses = {
					@ApiResponse(responseCode = "201", description = "User View with newly created payment data",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = PaymentViewDto.class))),
					@ApiResponse(responseCode = "400", description = "Validation of input data failed",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))),
					@ApiResponse(responseCode = "404", description = "Desired user or budget package does not exist",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))),
					@ApiResponse(responseCode = "409", description = "Entity already exists",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))), },
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Data for payment creation", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = PaymentUpdateCreateDto.class))))
	@PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public PaymentViewDto createPayment(@RequestBody @Valid PaymentUpdateCreateDto paymentDto) {
		return paymentFacade.createPayment(paymentDto);
	}

	@Override
	@Operation(description = "Updates payment by it's ID", responses = {
			@ApiResponse(responseCode = "200",
					description = "Payment was successfully updated, returning updated model",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = PaymentViewDto.class))),
			@ApiResponse(responseCode = "404", description = "Desired payment does not exist",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "Request body does not meet validation requirements",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = String.class))) },
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated payment values",
					required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = PaymentUpdateCreateDto.class))))
	@PutMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public PaymentViewDto updatePayment(@RequestBody PaymentUpdateCreateDto paymentDto) {
		return paymentFacade.updatePayment(paymentDto);
	}

	@Override
	@Operation(description = "Returns all payments.",
			responses = { @ApiResponse(responseCode = "200", description = "List of all payments.",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							array = @ArraySchema(schema = @Schema(implementation = PaymentViewDto.class)))) })
	@GetMapping(path = "/all-payments", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PaymentViewDto> getAllPayments() {
		return paymentFacade.getAllPayments();
	}

}
