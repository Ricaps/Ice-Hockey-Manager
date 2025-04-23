package cz.fi.muni.pa165.dto.gameservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchViewDto {

	private UUID guid;

	private ArenaViewDto arena;

	private OffsetDateTime startAt;

	private OffsetDateTime endAt;

	private UUID homeTeamUid;

	private UUID awayTeamUid;

	private String matchType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ResultViewDto result;

}
