package cz.fi.muni.pa165.dto.teamService;

import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Schema(description = "DTO for creating a new team")
public class FictiveTeamCreateDTO {

	@Schema(description = "Name of the team", example = "Avengers", required = true)
	@NotBlank
	@Size(max = 255)
	private String name;

	@Schema(description = "List of player UUIDs", required = true)
	@NotNull
	private List<UUID> playerIds;

	@Schema(description = "Primary characteristic type of the team", example = "STRENGTH")
	private TeamCharacteristicType characteristicType;

}
