package cz.fi.muni.pa165.dto.worldlistservice.player.detail;

import cz.fi.muni.pa165.dto.worldlistservice.BaseDto;
import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.PlayerCharacteristicDto;
import jakarta.validation.constraints.Min;
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
public class PlayerDetailDto extends BaseDto implements Identifiable {

	UUID id;

	@Size(max = 100, message = "First name cannot be longer than 100 characters")
	@Size(min = 3, message = "First name has to be longer than 2 characters")
	@NotNull(message = "First name cannot be null")
	String firstName;

	@Size(max = 100, message = "Last name cannot be longer than 100 characters")
	@Size(min = 3, message = "Last name has to be longer than 2 characters")
	@NotNull(message = "Last name cannot be null")
	String lastName;

	@Min(value = 1, message = "Overall rating has to be greater than 0")
	int overallRating;

	@Min(value = 0, message = "Market value cannot be negative")
	long marketValue;

	@NotNull(message = "Team cannot be null")
	PlayerTeamDto team;

	@NotNull(message = "Player characteristics cannot be null")
	Set<PlayerCharacteristicDto> playerCharacteristics;

}
