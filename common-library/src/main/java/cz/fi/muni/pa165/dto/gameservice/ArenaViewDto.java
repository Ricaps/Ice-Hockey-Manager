package cz.fi.muni.pa165.dto.gameservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArenaViewDto {

	private UUID guid;

	private String countryCode;

	private String cityName;

	private String arenaName;

}
