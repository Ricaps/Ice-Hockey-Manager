package cz.fi.muni.pa165.teamservice.business.mappers;

import cz.fi.muni.pa165.dto.teamService.BudgetSystemCreateDTO;
import cz.fi.muni.pa165.dto.teamService.BudgetSystemDTO;
import cz.fi.muni.pa165.dto.teamService.BudgetSystemUpdateDTO;
import cz.fi.muni.pa165.teamservice.persistence.entities.BudgetSystem;
import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

/**
 * @author Jan Martinek
 */
@Mapper(componentModel = "spring")
public interface BudgetSystemMapper {

	@Mapping(source = "guid", target = "guid")
	@Mapping(source = "team.guid", target = "teamId")
	BudgetSystemDTO toDto(BudgetSystem budgetSystem);

	@Mapping(target = "guid", ignore = true)
	@Mapping(source = "teamId", target = "team", qualifiedByName = "mapTeamIdToTeam")
	BudgetSystem toEntity(BudgetSystemCreateDTO createDTO);

	@Mapping(source = "guid", target = "guid")
	BudgetSystem toEntity(BudgetSystemUpdateDTO updateDTO);

	@Named("mapTeamIdToTeam")
	default FictiveTeam mapTeamIdToTeam(UUID teamId) {
		if (teamId == null) {
			return null;
		}
		FictiveTeam team = new FictiveTeam();
		team.setGuid(teamId);
		return team;
	}

}
