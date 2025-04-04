package cz.fi.muni.pa165.worldlistservice.business.facades.interfaces;

import cz.fi.muni.pa165.dto.worldlistservice.championship.create.ChampionshipCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.detail.ChampionshipDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.list.ChampionshipListDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.update.ChampionshipUpdateDto;

public interface ChampionshipFacade extends
		GenericFacade<ChampionshipDetailDto, ChampionshipListDto, ChampionshipCreateDto, ChampionshipUpdateDto> {

}
