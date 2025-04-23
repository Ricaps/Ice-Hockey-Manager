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
public class ResultViewDto {

	private UUID winnerTeam;

	private int scoreHomeTeam;

	private int scoreAwayTeam;

}
