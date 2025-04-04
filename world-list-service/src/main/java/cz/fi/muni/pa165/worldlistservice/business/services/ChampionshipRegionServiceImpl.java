package cz.fi.muni.pa165.worldlistservice.business.services;

import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.ChampionshipRegionService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipRegionEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.ChampionshipRegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChampionshipRegionServiceImpl extends BaseService<ChampionshipRegionEntity>
		implements ChampionshipRegionService {

	@Autowired
	protected ChampionshipRegionServiceImpl(ChampionshipRegionRepository repository) {
		super(repository);
	}

	@Override
	public String getEntityName() {
		return "ChampionshipRegion";
	}

	@Override
	public boolean isEntityUsed(UUID id) throws NotFoundException {
		var entity = repository.findById(id).orElseThrow(() -> new NotFoundException(this.getEntityName(), id));

		return !entity.getRegionChampionships().isEmpty();
	}

}
