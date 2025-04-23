package cz.fi.muni.pa165.gameservice.business.mappers;

import cz.fi.muni.pa165.dto.gameservice.MatchCreateDto;
import cz.fi.muni.pa165.dto.gameservice.MatchViewDto;
import cz.fi.muni.pa165.gameservice.persistence.entities.Arena;
import cz.fi.muni.pa165.gameservice.persistence.entities.Match;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MatchMapper {

	MatchViewDto matchEntityToMatchViewDto(Match match);

	@Named("EntityToViewIgnoreResults")
	default MatchViewDto matchEntityToMatchViewDtoIgnoreResult(Match match) {
		match.setResult(null);

		return matchEntityToMatchViewDto(match);
	}

	default List<MatchViewDto> listEntitiesToListViewsIgnoreResult(List<Match> matches) {
		matches.forEach(match -> match.setResult(null));

		return listEntitiesToListViews(matches);
	}

	List<MatchViewDto> listEntitiesToListViews(List<Match> matches);

	@Mapping(source = "arenaEntity", target = "arena")
	@Mapping(target = "guid", ignore = true)
	@Mapping(target = "matchType",
			expression = "java(cz.fi.muni.pa165.gameservice.persistence.entities.MatchType.FRIENDLY)")
	Match matchCreateDtoToEntity(MatchCreateDto matchCreateDto, Arena arenaEntity);

}
