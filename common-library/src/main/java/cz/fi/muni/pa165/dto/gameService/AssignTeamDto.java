package cz.fi.muni.pa165.dto.gameService;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignTeamDto {

	@NotNull
	private UUID assignTeam;

}
