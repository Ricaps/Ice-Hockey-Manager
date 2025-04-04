package cz.fi.muni.pa165.teamservice.business.mappers;

import cz.fi.muni.pa165.dto.teamService.BudgetSystemCreateDTO;
import cz.fi.muni.pa165.dto.teamService.BudgetSystemDTO;
import cz.fi.muni.pa165.dto.teamService.BudgetSystemUpdateDTO;
import cz.fi.muni.pa165.teamservice.persistence.entities.BudgetSystem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Jan Martinek
 */
@Mapper(componentModel = "spring")
public interface BudgetSystemMapper {

	@Mapping(source = "guid", target = "guid")
	BudgetSystemDTO toDto(BudgetSystem budgetSystem);

	@Mapping(target = "guid", ignore = true)
	BudgetSystem toEntity(BudgetSystemCreateDTO createDTO);

	BudgetSystem toEntity(BudgetSystemUpdateDTO updateDTO);

}
