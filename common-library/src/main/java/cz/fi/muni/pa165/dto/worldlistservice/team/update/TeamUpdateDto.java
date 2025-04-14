package cz.fi.muni.pa165.dto.worldlistservice.team.update;

import cz.fi.muni.pa165.dto.worldlistservice.BaseDto;
import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamUpdateDto extends BaseDto implements Identifiable {

	UUID id;

	@Size(max = 100, message = "Name cannot be longer than 100 characters")
	@Size(min = 7, message = "Name has to be longer than 6 characters")
	@NotNull
	String name;

	@NotNull(message = "Team has to be in championship")
	UUID championshipId;

	Set<UUID> teamPlayersIds = new HashSet<>();

}
