package cz.fi.muni.pa165.dto.teamservice;

import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.UUID;

/**
 * @author Jan Martinek
 */
@Data
@Schema(description = "Team characteristic creation request")
public class TeamCharacteristicCreateDTO {

	@Schema(description = "ID of the team this characteristic belongs to",
			example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
	@NotNull(message = "Team ID cannot be null")
	private UUID teamId;

	@Schema(description = "Type of characteristic", example = "STRENGTH", required = true)
	@NotNull(message = "Characteristic type cannot be null")
	private TeamCharacteristicType characteristicType;

	@Schema(description = "Numeric value of the characteristic", example = "85", required = true)
	@NotNull(message = "Value cannot be null")
	@PositiveOrZero(message = "Value must be positive or zero")
	private Integer characteristicValue;

}
