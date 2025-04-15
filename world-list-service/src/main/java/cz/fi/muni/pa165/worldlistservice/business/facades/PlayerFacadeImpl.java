package cz.fi.muni.pa165.worldlistservice.business.facades;

import cz.fi.muni.pa165.dto.worldlistservice.player.create.PlayerCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.detail.PlayerDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.list.PlayerListDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.update.PlayerUpdateDto;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.PlayerFacade;
import cz.fi.muni.pa165.worldlistservice.business.mappers.PlayerMapper;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.PlayerCharacteristicService;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.PlayerService;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.TeamService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerCharacteristicEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.TeamEntity;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static cz.fi.muni.pa165.worldlistservice.business.validations.ValidationHelper.requireNonNull;

@Service
public class PlayerFacadeImpl
		extends BaseFacade<PlayerDetailDto, PlayerListDto, PlayerCreateDto, PlayerUpdateDto, PlayerEntity>
		implements PlayerFacade {

	private final TeamService teamService;

	private final PlayerCharacteristicService playerCharacteristicService;

	@Autowired
	public PlayerFacadeImpl(PlayerService service, TeamService teamService,
			PlayerCharacteristicService playerCharacteristicService,
			@Qualifier("playerMapperImpl") PlayerMapper mapper) {
		super(service, mapper, LoggerFactory.getLogger(PlayerFacadeImpl.class));
		this.teamService = teamService;
		this.playerCharacteristicService = playerCharacteristicService;
	}

	@Transactional
	@Override
	public PlayerDetailDto create(PlayerCreateDto model) throws NotFoundException {
		requireNonNull(model, service.getEntityName() + " entity cannot be null.");

		logger.info("Creating Player with name {} {}", model.getFirstName(), model.getLastName());

		var playerTeam = findPlayerTeam(model.getTeamId());
		var playerCharacteristics = findPlayerCharacteristics(model.getPlayerCharacteristicsIds());

		var playerEntity = mapper.toEntityFromCreateModel(model);
		playerEntity.setPlayerCharacteristics(playerCharacteristics);
		playerEntity.setTeam(playerTeam);

		playerEntity = ((PlayerService) service).updateRating(playerEntity);

		var createdEntity = service.create(playerEntity);

		logger.info("Created Player with name {} {}", model.getFirstName(), model.getLastName());

		return mapper.toDetailModel(createdEntity);
	}

	@Transactional
	@Override
	public PlayerDetailDto update(PlayerUpdateDto model) throws NotFoundException {
		requireNonNull(model, service.getEntityName() + " entity cannot be null.");

		logger.info("Updating Player with name {} {}", model.getFirstName(), model.getLastName());

		var playerTeam = findPlayerTeam(model.getTeamId());
		var playerCharacteristics = findPlayerCharacteristics(model.getPlayerCharacteristicsIds());

		var playerEntity = mapper.toEntityFromUpdateModel(model);
		playerEntity.setTeam(playerTeam);
		playerEntity.setPlayerCharacteristics(playerCharacteristics);

		playerEntity = ((PlayerService) service).updateRating(playerEntity);

		var updatedEntity = service.update(playerEntity);

		logger.info("Updated Player with name {} {}", model.getFirstName(), model.getLastName());

		return mapper.toDetailModel(updatedEntity);
	}

	private TeamEntity findPlayerTeam(UUID teamId) throws NotFoundException {
		if (teamId == null) {
			return null;
		}

		return teamService.findById(teamId)
			.orElseThrow(() -> new NotFoundException(teamService.getEntityName(), teamId));
	}

	private Set<PlayerCharacteristicEntity> findPlayerCharacteristics(Set<UUID> characteristicIds)
			throws NotFoundException {
		return characteristicIds.stream()
			.map(characteristicId -> playerCharacteristicService.findById(characteristicId)
				.orElseThrow(
						() -> new NotFoundException(playerCharacteristicService.getEntityName(), characteristicId)))
			.collect(Collectors.toSet());
	}

}
