package cz.fi.muni.pa165.dto.worldlistservice.team.create;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateDto {

	@Size(max = 100, message = "Name cannot be longer than 100 characters")
	@Size(min = 7, message = "Name has to be longer than 6 characters")
	@NotNull
	String name;

	@NotNull(message = "Team has to be in championship")
	UUID championshipId;

	Set<UUID> teamPlayersIds;

}
