package cz.fi.muni.pa165.dto.gameservice;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchCreateDto {

	@NotNull
	private UUID arenaUid;

	@NotNull
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private OffsetDateTime startAt;

	@NotNull
	private UUID homeTeamUid;

	@NotNull
	private UUID awayTeamUid;

}
