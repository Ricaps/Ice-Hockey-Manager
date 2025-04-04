package cz.fi.muni.pa165.dto.worldlistservice.player.detail;

import cz.fi.muni.pa165.dto.worldlistservice.BaseDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.detail.ChampionshipTeamDto;
import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerTeamDto extends BaseDto implements Identifiable {

	UUID id;

	@Size(max = 100, message = "Name cannot be longer than 100 characters")
	@Size(min = 7, message = "Name has to be longer than 6 characters")
	@NotNull(message = "Name cannot be null")
	String name;

	@NotNull(message = "Championship team cannot be null")
	ChampionshipTeamDto championship;

}
