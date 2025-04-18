package cz.fi.muni.pa165.dto.gameService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionTeamDto {

	private UUID teamUid;

}
