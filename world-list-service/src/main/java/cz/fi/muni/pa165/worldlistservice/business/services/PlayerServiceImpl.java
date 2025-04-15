package cz.fi.muni.pa165.worldlistservice.business.services;

import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.PlayerService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerCharacteristicEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

import static cz.fi.muni.pa165.worldlistservice.business.validations.ValidationHelper.requireNonNull;

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

	@Override
	public PlayerEntity updateRating(UUID id) throws NotFoundException {
		var entity = repository.findById(id).orElseThrow(() -> new NotFoundException(this.getEntityName(), id));

		return updateRating(entity);
	}

	@Override
	public PlayerEntity updateRating(PlayerEntity player) {
		requireNonNull(player, "Player must not be null when updating rating");
		int[] playerCharacteristicValues = player.getPlayerCharacteristics()
			.stream()
			.mapToInt(PlayerCharacteristicEntity::getValue)
			.toArray();

		int average = playerCharacteristicValues.length == 0 ? 0
				: (int) Math.round(Arrays.stream(playerCharacteristicValues).average().orElse(0));

		player.setOverallRating(average);

		return player;
	}

}
