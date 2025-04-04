package cz.fi.muni.pa165.dto.worldlistservice.championship.detail;

import cz.fi.muni.pa165.dto.worldlistservice.BaseDto;
import cz.fi.muni.pa165.dto.worldlistservice.championshippregion.ChampionshipRegionDto;
import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
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
public class ChampionshipDetailDto extends BaseDto implements Identifiable {

	UUID id;

	@Size(max = 200, message = "Name cannot be longer than 200 characters")
	@Size(min = 4, message = "Name has to be longer than 3 characters")
	@NotNull(message = "Name cannot be null")
	String name;

	@NotNull(message = "Championship region must be assigned")
	ChampionshipRegionDto championshipRegion;

	@NotNull(message = "Championship must have teams assigned")
	Set<ChampionshipTeamDto> championshipTeams;

}
