package cz.fi.muni.pa165.dto.gameService;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CompetitionTeamDto {

	private UUID teamUid;

}
