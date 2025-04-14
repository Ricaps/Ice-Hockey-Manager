package cz.fi.muni.pa165.worldlistservice.business.facades;

import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.ChampionshipRegionDto;
import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.create.ChampionshipRegionCreateDto;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.ChampionshipRegionFacade;
import cz.fi.muni.pa165.worldlistservice.business.mappers.ChampionshipRegionMapper;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.ChampionshipRegionService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipRegionEntity;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ChampionshipRegionFacadeImpl extends
		BaseFacade<ChampionshipRegionDto, ChampionshipRegionDto, ChampionshipRegionCreateDto, ChampionshipRegionDto, ChampionshipRegionEntity>
		implements ChampionshipRegionFacade {

	@Autowired
	public ChampionshipRegionFacadeImpl(ChampionshipRegionService service,
			@Qualifier("championshipRegionMapperImpl") ChampionshipRegionMapper mapper) {
		super(service, mapper, LoggerFactory.getLogger(ChampionshipRegionFacadeImpl.class));
	}

}
