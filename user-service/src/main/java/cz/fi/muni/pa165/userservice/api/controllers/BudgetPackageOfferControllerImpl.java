package cz.fi.muni.pa165.userservice.api.controllers;

import cz.fi.muni.pa165.dto.userService.BudgetOfferPackageDto;
import cz.fi.muni.pa165.service.userService.api.BudgetOfferPackageController;
import cz.fi.muni.pa165.userservice.business.facades.BudgetOfferPackageFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/v1/budget-package-offer")
@Tag(name = "Budget package offer API", description = "Operations related to budget packages.")
public class BudgetPackageOfferControllerImpl implements BudgetOfferPackageController {

	private final BudgetOfferPackageFacade budgetOfferPackageFacade;

	@Autowired
	public BudgetPackageOfferControllerImpl(BudgetOfferPackageFacade budgetOfferPackageFacade) {
		this.budgetOfferPackageFacade = budgetOfferPackageFacade;
	}

	@Override
	@Operation(description = "Returns desired budget package by its id.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Budget package with desired details.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = BudgetOfferPackageDto.class))),
					@ApiResponse(responseCode = "404", description = "Desired package does not exist",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))),
					@ApiResponse(responseCode = "400", description = "Path parameter does not have a form of UUID",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))) },
			parameters = @Parameter(name = "id", description = "UUID of desired package", required = true))
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public BudgetOfferPackageDto getBudgetOfferPackageById(@PathVariable UUID id) {
		return budgetOfferPackageFacade.getBudgetPackageOfferById(id);
	}

	@Override
	@Operation(description = "Creates a package with given properties",
			responses = {
					@ApiResponse(responseCode = "201", description = "Package with newly created package data",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = BudgetOfferPackageDto.class))),
					@ApiResponse(responseCode = "400", description = "Validation of input data failed",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))),
					@ApiResponse(responseCode = "409", description = "Entity already exists",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))), },
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Data for pcakage creation", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = BudgetOfferPackageDto.class))))
	@PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public BudgetOfferPackageDto createBudgetOfferPackage(
			@RequestBody @Valid BudgetOfferPackageDto budgetOfferPackageDto) {
		return budgetOfferPackageFacade.create(budgetOfferPackageDto);
	}

	@Override
	@Operation(description = "Returns deactivated package by it's id.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Budget package was deactivated.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = BudgetOfferPackageDto.class))),
					@ApiResponse(responseCode = "404", description = "Desired package does not exist",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))),
					@ApiResponse(responseCode = "400", description = "Path parameter does not have a form of UUID",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))) },
			parameters = @Parameter(name = "id", description = "UUID of desired package", required = true))
	@PutMapping(path = "/deactivate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public BudgetOfferPackageDto deactivateBudgetOfferPackage(@PathVariable UUID id) {
		return budgetOfferPackageFacade.deactivateBudgetOfferPackage(id);
	}

	@Override
	@Operation(description = "Returns activated package by it's id.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Budget package was activated.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = BudgetOfferPackageDto.class))),
					@ApiResponse(responseCode = "404", description = "Desired package does not exist",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))),
					@ApiResponse(responseCode = "400", description = "Path parameter does not have a form of UUID",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))) },
			parameters = @Parameter(name = "id", description = "UUID of desired package", required = true))
	@PutMapping(path = "/activate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public BudgetOfferPackageDto activateBudgetOfferPackage(@PathVariable UUID id) {
		return budgetOfferPackageFacade.activateBudgetOfferPackage(id);
	}

	@Override
	@Operation(description = "Returns all budget packages.",
			responses = { @ApiResponse(responseCode = "200", description = "List of all packages.",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							array = @ArraySchema(schema = @Schema(implementation = BudgetOfferPackageDto.class)))) })
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<BudgetOfferPackageDto> getAllBudgetOfferPackages() {
		return budgetOfferPackageFacade.getAllBudgetOfferPackages();
	}

	@Override
	@Operation(description = "Returns all available budget packages.",
			responses = { @ApiResponse(responseCode = "200", description = "List of all available packages.",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							array = @ArraySchema(schema = @Schema(implementation = BudgetOfferPackageDto.class)))) })
	@GetMapping(path = "/available-packages", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<BudgetOfferPackageDto> getAllAvailableBudgetOfferPackages() {
		return budgetOfferPackageFacade.getAllAvailableBudgetOfferPackages();
	}

}
