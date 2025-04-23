package cz.fi.muni.pa165.gameservice.business.mappers;

import cz.fi.muni.pa165.dto.gameservice.AssignTeamDto;
import cz.fi.muni.pa165.dto.gameservice.CompetitionCreateDto;
import cz.fi.muni.pa165.dto.gameservice.CompetitionViewDto;
import cz.fi.muni.pa165.gameservice.persistence.entities.Competition;
import cz.fi.muni.pa165.gameservice.persistence.entities.CompetitionHasTeam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompetitionMapper {

	CompetitionViewDto competitionToCompetitionViewDto(Competition competition);

	Competition competitionCreateDtoToCompetition(CompetitionCreateDto createDto);

	Competition competitionCreateDtoToCompetition(CompetitionCreateDto createDto, UUID guid);

	@Mapping(source = "assignTeamDto.assignTeam", target = "teamUid")
	CompetitionHasTeam competitionTeamsDtoToCompetitionHasTeam(AssignTeamDto assignTeamDto, Competition competition);

}
