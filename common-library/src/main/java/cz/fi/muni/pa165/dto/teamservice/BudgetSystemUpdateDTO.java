package cz.fi.muni.pa165.dto.teamservice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.Setter;

import java.util.UUID;

/**
 * @author Jan Martinek
 */
@Data
@Schema(description = "DTO for updating a budget system")
public class BudgetSystemUpdateDTO {

	@Setter
	@Schema(description = "ID of the budget system", required = true)
	@NotNull
	private UUID guid;

	@Schema(description = "Budget amount", example = "1500000.00", required = true)
	@NotNull
	@PositiveOrZero
	private Double amount;

}
