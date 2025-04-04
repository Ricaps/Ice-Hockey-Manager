package cz.fi.muni.pa165.dto.teamService;

import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

/**
 * @author Jan Martinek
 */
@Data
@Schema(description = "Team characteristic response")
public class TeamCharacteristicDTO {

	@Schema(description = "Unique identifier of the characteristic", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
	private UUID guid;

	@Schema(description = "ID of the team this characteristic belongs to",
			example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
	private UUID teamId;

	@Schema(description = "Type of characteristic", example = "STRENGTH")
	private TeamCharacteristicType characteristicType;

	@Schema(description = "Numeric value of the characteristic", example = "85")
	private int characteristicValue;

}
