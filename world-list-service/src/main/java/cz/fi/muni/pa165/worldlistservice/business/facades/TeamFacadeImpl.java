package cz.fi.muni.pa165.worldlistservice.business.facades;

import cz.fi.muni.pa165.dto.worldlistservice.team.create.TeamCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.detail.TeamDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.list.TeamListDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.update.TeamUpdateDto;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.TeamFacade;
import cz.fi.muni.pa165.worldlistservice.business.mappers.TeamMapper;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.ChampionshipService;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.PlayerService;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.TeamService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipEntity;
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
public class TeamFacadeImpl extends BaseFacade<TeamDetailDto, TeamListDto, TeamCreateDto, TeamUpdateDto, TeamEntity>
		implements TeamFacade {

	private final ChampionshipService championshipService;

	private final PlayerService playerService;

	@Autowired
	public TeamFacadeImpl(TeamService service, ChampionshipService championshipService, PlayerService playerService,
			@Qualifier("teamMapperImpl") TeamMapper mapper) {
		super(service, mapper, LoggerFactory.getLogger(TeamFacadeImpl.class));
		this.championshipService = championshipService;
		this.playerService = playerService;
	}

	@Transactional
	@Override
	public TeamDetailDto create(TeamCreateDto model) throws NotFoundException {
		requireNonNull(model, service.getEntityName() + " entity cannot be null.");

		logger.info("Creating Team with name {}", model.getName());

		var teamChampionship = findTeamChampionship(model.getChampionshipId());
		var teamPlayers = findTeamPlayers(model.getTeamPlayersIds());

		var teamEntity = mapper.toEntityFromCreateModel(model);
		teamEntity.setChampionship(teamChampionship);
		teamEntity.setTeamPlayers(teamPlayers);

		var createdEntity = service.create(teamEntity);

		logger.info("Created Team with name {}", createdEntity.getName());

		return mapper.toDetailModel(createdEntity);
	}

	@Transactional
	@Override
	public TeamDetailDto update(TeamUpdateDto model) throws NotFoundException {
		requireNonNull(model, service.getEntityName() + " entity cannot be null.");

		logger.info("Updating Team with name {}", model.getName());

		var teamChampionship = findTeamChampionship(model.getChampionshipId());
		var teamPlayers = findTeamPlayers(model.getTeamPlayersIds());

		var teamEntity = mapper.toEntityFromUpdateModel(model);
		teamEntity.setChampionship(teamChampionship);
		teamEntity.setTeamPlayers(teamPlayers);

		var updatedEntity = service.update(teamEntity);

		logger.info("Updated Team with name {}", updatedEntity.getName());

		return mapper.toDetailModel(updatedEntity);
	}

	private ChampionshipEntity findTeamChampionship(UUID championshipId) throws NotFoundException {
		if (championshipId == null) {
			return null;
		}

		return championshipService.findById(championshipId)
			.orElseThrow(() -> new NotFoundException(championshipService.getEntityName(), championshipId));
	}

	private Set<PlayerEntity> findTeamPlayers(Set<UUID> playerIds) throws NotFoundException {
		return playerIds.stream()
			.map(playerId -> playerService.findById(playerId)
				.orElseThrow(() -> new NotFoundException(playerService.getEntityName(), playerId)))
			.collect(Collectors.toSet());
	}

}
