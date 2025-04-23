package cz.fi.muni.pa165.gameservice.testdata;

import cz.fi.muni.pa165.dto.gameservice.AssignTeamDto;
import cz.fi.muni.pa165.dto.gameservice.CompetitionTeamDto;
import cz.fi.muni.pa165.gameservice.persistence.entities.CompetitionHasTeam;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TeamsTestData {

	public static Set<CompetitionTeamDto> getTeamsDto() {
		return Set.of(
				CompetitionTeamDto.builder().teamUid(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).build(),
				CompetitionTeamDto.builder().teamUid(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479")).build(),
				CompetitionTeamDto.builder().teamUid(UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8")).build(),
				CompetitionTeamDto.builder().teamUid(UUID.fromString("3d81d98c-4444-4a48-9a5f-2b4c53c27a39")).build(),
				CompetitionTeamDto.builder().teamUid(UUID.fromString("b5509a10-1783-4906-9302-34b69ef4cf94")).build(),
				CompetitionTeamDto.builder().teamUid(UUID.fromString("a033dbbe-c26d-47c7-8eab-0f4b5f58a10b")).build(),
				CompetitionTeamDto.builder().teamUid(UUID.fromString("7c9e6679-7425-40de-944b-e07fc1f90ae7")).build(),
				CompetitionTeamDto.builder().teamUid(UUID.fromString("02a0b1c1-16a1-466e-9f6b-4e7a5e2b78d6")).build(),
				CompetitionTeamDto.builder().teamUid(UUID.fromString("e63d38f7-85a5-4e41-9025-093276d5f28d")).build(),
				CompetitionTeamDto.builder().teamUid(UUID.fromString("7d8f1b0c-872b-4511-b3db-c9b33f8dd9a7")).build());
	}

	public static Set<CompetitionHasTeam> getTeamEntities() {
		return Set.of(
				CompetitionHasTeam.builder().teamUid(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).build(),
				CompetitionHasTeam.builder().teamUid(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479")).build(),
				CompetitionHasTeam.builder().teamUid(UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8")).build(),
				CompetitionHasTeam.builder().teamUid(UUID.fromString("3d81d98c-4444-4a48-9a5f-2b4c53c27a39")).build(),
				CompetitionHasTeam.builder().teamUid(UUID.fromString("b5509a10-1783-4906-9302-34b69ef4cf94")).build(),
				CompetitionHasTeam.builder().teamUid(UUID.fromString("a033dbbe-c26d-47c7-8eab-0f4b5f58a10b")).build(),
				CompetitionHasTeam.builder().teamUid(UUID.fromString("7c9e6679-7425-40de-944b-e07fc1f90ae7")).build(),
				CompetitionHasTeam.builder().teamUid(UUID.fromString("02a0b1c1-16a1-466e-9f6b-4e7a5e2b78d6")).build(),
				CompetitionHasTeam.builder().teamUid(UUID.fromString("e63d38f7-85a5-4e41-9025-093276d5f28d")).build(),
				CompetitionHasTeam.builder().teamUid(UUID.fromString("7d8f1b0c-872b-4511-b3db-c9b33f8dd9a7")).build());
	}

	public static AssignTeamDto getAssignTeamDto(UUID uuid) {
		return AssignTeamDto.builder().assignTeam(uuid).build();
	}

	public static List<Pair<UUID, UUID>> getPairs() {
		var teams = getTeamEntities().stream().toList();
		var team1 = teams.getFirst().getTeamUid();
		var team2 = teams.get(1).getTeamUid();
		var team3 = teams.get(2).getTeamUid();
		var team4 = teams.get(3).getTeamUid();

		return List.of(Pair.of(team1, team2), Pair.of(team1, team3), Pair.of(team1, team3), Pair.of(team2, team3),
				Pair.of(team2, team4), Pair.of(team3, team4));
	}

}
