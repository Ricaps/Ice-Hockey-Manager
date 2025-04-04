package cz.fi.muni.pa165.worldlistservice.business.services;

import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.ChampionshipService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.ChampionshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChampionshipServiceImpl extends BaseService<ChampionshipEntity> implements ChampionshipService {

	@Autowired
	protected ChampionshipServiceImpl(ChampionshipRepository repository) {
		super(repository);
	}

	@Override
	public String getEntityName() {
		return "Championship";
	}

	@Override
	public boolean isEntityUsed(UUID id) throws NotFoundException {
		var entity = repository.findById(id).orElseThrow(() -> new NotFoundException(this.getEntityName(), id));

		return !entity.getChampionshipTeams().isEmpty();
	}

}
