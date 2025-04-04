package cz.fi.muni.pa165.dto.worldlistservice.championship.create;

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
public class ChampionshipCreateDto {

	@Size(max = 200, message = "Name cannot be longer than 200 characters")
	@Size(min = 4, message = "Name has to be longer than 3 characters")
	@NotNull(message = "Name cannot be null")
	String name;

	@NotNull(message = "Championship has to have region assigned")
	UUID championshipRegionId;

	@NotNull(message = "Championship cannot have empty team list")
	Set<UUID> championshipTeamsIds;

}
