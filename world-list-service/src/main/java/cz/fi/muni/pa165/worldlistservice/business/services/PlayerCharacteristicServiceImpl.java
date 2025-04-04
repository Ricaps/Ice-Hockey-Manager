package cz.fi.muni.pa165.worldlistservice.business.services;

import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.PlayerCharacteristicService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerCharacteristicEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.PlayerCharacteristicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlayerCharacteristicServiceImpl extends BaseService<PlayerCharacteristicEntity>
		implements PlayerCharacteristicService {

	@Autowired
	public PlayerCharacteristicServiceImpl(PlayerCharacteristicRepository repository) {
		super(repository);
	}

	@Override
	public String getEntityName() {
		return "PlayerCharacteristic";
	}

	@Override
	public boolean isEntityUsed(UUID id) throws NotFoundException {
		return false;
	}

}
