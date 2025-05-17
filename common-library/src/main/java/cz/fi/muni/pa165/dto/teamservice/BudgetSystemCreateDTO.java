package cz.fi.muni.pa165.dto.teamservice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.UUID;

/**
 * @author Jan Martinek
 */
@Data
@Schema(description = "DTO for creating a budget system")
public class BudgetSystemCreateDTO {

	@Schema(description = "Budget amount", example = "1000000.00", required = true)
	@NotNull
	@PositiveOrZero
	private Double amount;

	@Schema(description = "ID of the associated team", required = true)
	@NotNull
	private UUID teamId;

}
