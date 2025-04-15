package cz.fi.muni.pa165.dto.worldlistservice.player.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
public class PlayerCreateDto {

	@Size(max = 100, message = "First name cannot be longer than 100 characters")
	@Size(min = 3, message = "First name has to be longer than 2 characters")
	@NotNull(message = "First name cannot be null")
	String firstName;

	@Size(max = 100, message = "Last name cannot be longer than 100 characters")
	@Size(min = 3, message = "Last name has to be longer than 2 characters")
	@NotNull(message = "Last name cannot be null")
	String lastName;

	@Min(value = 0, message = "Market value cannot be negative")
	long marketValue;

	UUID teamId;

	boolean used;

	@NotEmpty(message = "Player characteristics cannot be empty")
	@NotNull(message = "Player characteristics cannot be null")
	Set<UUID> playerCharacteristicsIds;

}
