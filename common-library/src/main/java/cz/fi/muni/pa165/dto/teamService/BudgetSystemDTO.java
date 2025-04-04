package cz.fi.muni.pa165.dto.teamService;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

/**
 * @author Jan Martinek
 */
@Data
@Schema(description = "DTO representing a budget system")
public class BudgetSystemDTO {

	@Schema(description = "ID of the budget system")
	private UUID guid;

	@Schema(description = "Budget amount")
	private Double amount;

}
