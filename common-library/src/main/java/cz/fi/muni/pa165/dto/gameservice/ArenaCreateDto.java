package cz.fi.muni.pa165.dto.gameservice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArenaCreateDto {

	@Size(max = 3)
	@NotNull
	@Schema(example = "CZE", description = "Country Code where the Arena is located")
	private String countryCode;

	@Size(max = 255)
	@NotNull
	@Schema(example = "Prague", description = "City name where the arena is located")
	private String cityName;

	@Size(max = 255)
	@NotNull
	@Schema(example = "O2 Arena", description = "Name of the arena")
	private String arenaName;

}
