package cz.fi.muni.pa165.worldlistservice.business.facades.interfaces;

import cz.fi.muni.pa165.dto.worldlistservice.team.create.TeamCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.detail.TeamDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.list.TeamListDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.update.TeamUpdateDto;

public interface TeamFacade extends GenericFacade<TeamDetailDto, TeamListDto, TeamCreateDto, TeamUpdateDto> {

}
