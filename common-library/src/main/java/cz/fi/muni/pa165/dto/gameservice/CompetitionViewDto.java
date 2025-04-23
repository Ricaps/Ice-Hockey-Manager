package cz.fi.muni.pa165.dto.gameservice;

import lombok.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompetitionViewDto {

	private UUID guid;

	private String name;

	private LocalDate startAt;

	private LocalDate endAt;

	private Set<CompetitionTeamDto> teams;

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CompetitionViewDto that))
			return false;
		return Objects.equals(guid, that.guid) && Objects.equals(name, that.name)
				&& Objects.equals(startAt, that.startAt) && Objects.equals(endAt, that.endAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(guid, name, startAt, endAt);
	}

}
