package cz.fi.muni.pa165.teamservice.api.controllers;

import cz.fi.muni.pa165.dto.teamservice.BudgetSystemCreateDTO;
import cz.fi.muni.pa165.dto.teamservice.BudgetSystemDTO;
import cz.fi.muni.pa165.dto.teamservice.BudgetSystemUpdateDTO;
import cz.fi.muni.pa165.teamservice.business.facades.BudgetSystemFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * @author Jan Martinek
 */
@RestController
@RequestMapping("/api/budget-systems")
@Tag(name = "Budget System API", description = "Management of team budget systems")
public class BudgetSystemController {

	private final BudgetSystemFacade budgetSystemFacade;

	@Autowired
	public BudgetSystemController(BudgetSystemFacade budgetSystemFacade) {
		this.budgetSystemFacade = budgetSystemFacade;
	}

	@Operation(description = "Create new budget system",
			responses = {
					@ApiResponse(responseCode = "201", description = "Budget system created",
							content = @Content(schema = @Schema(implementation = BudgetSystemDTO.class))),
					@ApiResponse(responseCode = "400", description = "Invalid input"),
					@ApiResponse(responseCode = "409", description = "Budget system already exists") })
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public BudgetSystemDTO createBudgetSystem(@RequestBody @Valid BudgetSystemCreateDTO createDTO) {
		return budgetSystemFacade.createBudgetSystem(createDTO);
	}

	@Operation(description = "Update budget system",
			responses = {
					@ApiResponse(responseCode = "200", description = "Budget system updated",
							content = @Content(schema = @Schema(implementation = BudgetSystemDTO.class))),
					@ApiResponse(responseCode = "400", description = "Invalid input"),
					@ApiResponse(responseCode = "404", description = "Budget system not found") })
	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public BudgetSystemDTO updateBudgetSystem(@PathVariable UUID id,
			@RequestBody @Valid BudgetSystemUpdateDTO updateDTO) {
		if (!id.equals(updateDTO.getGuid())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path must match ID in body");
		}
		return budgetSystemFacade.updateBudgetSystem(updateDTO);
	}

	@Operation(description = "Delete budget system",
			responses = { @ApiResponse(responseCode = "204", description = "Budget system deleted"),
					@ApiResponse(responseCode = "404", description = "Budget system not found") })
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteBudgetSystem(@PathVariable UUID id) {
		budgetSystemFacade.deleteBudgetSystem(id);
	}

	@Operation(description = "Get budget system by ID",
			responses = {
					@ApiResponse(responseCode = "200", description = "Budget system details",
							content = @Content(schema = @Schema(implementation = BudgetSystemDTO.class))),
					@ApiResponse(responseCode = "404", description = "Budget system not found") })
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public BudgetSystemDTO getBudgetSystem(@PathVariable UUID id) {
		return budgetSystemFacade.findById(id);
	}

}
