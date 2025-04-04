package cz.fi.muni.pa165.gameservice.business.mappers;

import cz.fi.muni.pa165.dto.gameService.AssignTeamDto;
import cz.fi.muni.pa165.dto.gameService.CompetitionCreateDto;
import cz.fi.muni.pa165.dto.gameService.CompetitionViewDto;
import cz.fi.muni.pa165.gameservice.persistence.entities.Competition;
import cz.fi.muni.pa165.gameservice.persistence.entities.CompetitionHasTeam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CompetitionMapper {

	CompetitionMapper INSTANCE = Mappers.getMapper(CompetitionMapper.class);

	CompetitionViewDto competitionToCompetitionViewDto(Competition competition);

	Competition competitionCreateDtoToCompetition(CompetitionCreateDto createDto);

	Competition competitionCreateDtoToCompetition(CompetitionCreateDto createDto, UUID guid);

	@Mapping(source = "assignTeamDto.assignTeam", target = "teamUid")
	CompetitionHasTeam competitionTeamsDtoToCompetitionHasTeam(AssignTeamDto assignTeamDto, Competition competition);

}
