package cz.fi.muni.pa165.dto.teamService;

import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * @author Jan Martinek
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing a team")
public class FictiveTeamDTO {

	@Schema(description = "UUID of the team", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
	private UUID guid;

	@Schema(description = "Name of the team", example = "Avengers")
	private String name;

	@Schema(description = "UUID of the team's owner", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
	private UUID ownerId;

	@Schema(description = "List of player UUIDs")
	private List<UUID> playerIds;

	@Schema(description = "Primary characteristic type of the team", example = "STRENGTH")
	private TeamCharacteristicType characteristicType;

	@Schema(description = "Budget system ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
	private UUID budgetSystemId;

	@Schema(description = "Budget amount", example = "1000000.00")
	private Double budgetAmount;

}
