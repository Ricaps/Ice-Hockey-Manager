package cz.fi.muni.pa165.worldlistservice.business.services;

import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.PlayerService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlayerServiceImpl extends BaseService<PlayerEntity> implements PlayerService {

	@Autowired
	public PlayerServiceImpl(PlayerRepository repository) {
		super(repository);
	}

	@Override
	public String getEntityName() {
		return "Player";
	}

	@Override
	public boolean isEntityUsed(UUID id) throws NotFoundException {
		var entity = repository.findById(id).orElseThrow(() -> new NotFoundException(this.getEntityName(), id));

		return !entity.getPlayerCharacteristics().isEmpty();
	}

}
