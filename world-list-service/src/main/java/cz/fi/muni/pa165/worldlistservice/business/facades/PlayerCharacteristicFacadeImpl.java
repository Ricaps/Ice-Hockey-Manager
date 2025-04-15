package cz.fi.muni.pa165.worldlistservice.business.facades;

import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.PlayerCharacteristicDto;
import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.create.PlayerCharacteristicCreateDto;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.PlayerCharacteristicFacade;
import cz.fi.muni.pa165.worldlistservice.business.mappers.PlayerCharacteristicMapper;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.PlayerCharacteristicService;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.PlayerService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerCharacteristicEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerEntity;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static cz.fi.muni.pa165.worldlistservice.business.validations.ValidationHelper.requireNonNull;

@Service
public class PlayerCharacteristicFacadeImpl extends
		BaseFacade<PlayerCharacteristicDto, PlayerCharacteristicDto, PlayerCharacteristicCreateDto, PlayerCharacteristicDto, PlayerCharacteristicEntity>
		implements PlayerCharacteristicFacade {

	private final PlayerService playerService;

	@Autowired
	public PlayerCharacteristicFacadeImpl(PlayerCharacteristicService service,
			@Qualifier("playerCharacteristicMapperImpl") PlayerCharacteristicMapper mapper,
			PlayerService playerService) {
		super(service, mapper, LoggerFactory.getLogger(PlayerCharacteristicFacadeImpl.class));
		this.playerService = playerService;
	}

	@Override
	public PlayerCharacteristicDto create(PlayerCharacteristicCreateDto playerCharacteristic) {
		requireNonNull(playerCharacteristic, service.getEntityName() + " entity cannot be null.");

		logger.info("Creating PlayerCharacteristic with type {} and value {}", playerCharacteristic.getType(),
				playerCharacteristic.getValue());

		PlayerCharacteristicEntity entity = mapper.toEntityFromCreateModel(playerCharacteristic);

		PlayerCharacteristicEntity savedEntity = service.create(entity);

		if (savedEntity.getPlayer() != null) {
			updatePlayerWithAssignedCharacteristic(savedEntity.getPlayer());
		}

		logger.info("Created PlayerCharacteristic with type {} and value {}", playerCharacteristic.getType(),
				playerCharacteristic.getValue());

		return mapper.toDetailModel(savedEntity);
	}

	@Override
	public PlayerCharacteristicDto update(PlayerCharacteristicDto playerCharacteristic) {
		requireNonNull(playerCharacteristic, service.getEntityName() + " entity cannot be null.");

		logger.info("Updating PlayerCharacteristic with type {} and value {}", playerCharacteristic.getType(),
				playerCharacteristic.getValue());

		PlayerCharacteristicEntity entity = mapper.toEntityFromUpdateModel(playerCharacteristic);

		PlayerCharacteristicEntity updatedEntity = service.update(entity);

		if (updatedEntity.getPlayer() != null) {
			updatePlayerWithAssignedCharacteristic(updatedEntity.getPlayer());
		}

		logger.info("Updated PlayerCharacteristic with type {} and value {}", playerCharacteristic.getType(),
				playerCharacteristic.getValue());

		return mapper.toDetailModel(updatedEntity);
	}

	private void updatePlayerWithAssignedCharacteristic(PlayerEntity playerEntity) {
		var updatedPlayer = playerService.updateRating(playerEntity);

		playerService.update(updatedPlayer);
	}

}
