package cz.fi.muni.pa165.worldlistservice.business.facades;

import cz.fi.muni.pa165.dto.worldlistservice.championship.create.ChampionshipCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.detail.ChampionshipDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.list.ChampionshipListDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.update.ChampionshipUpdateDto;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.ChampionshipFacade;
import cz.fi.muni.pa165.worldlistservice.business.mappers.ChampionshipMapper;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.ChampionshipRegionService;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.ChampionshipService;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.TeamService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipRegionEntity;
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
public class ChampionshipFacadeImpl extends
		BaseFacade<ChampionshipDetailDto, ChampionshipListDto, ChampionshipCreateDto, ChampionshipUpdateDto, ChampionshipEntity>
		implements ChampionshipFacade {

	private final ChampionshipRegionService championshipRegionService;

	private final TeamService teamService;

	@Autowired
	public ChampionshipFacadeImpl(ChampionshipService service, ChampionshipRegionService championshipRegionService,
			TeamService teamService, @Qualifier("championshipMapperImpl") ChampionshipMapper mapper) {
		super(service, mapper, LoggerFactory.getLogger(ChampionshipFacadeImpl.class));
		this.championshipRegionService = championshipRegionService;
		this.teamService = teamService;
	}

	@Transactional
	@Override
	public ChampionshipDetailDto create(ChampionshipCreateDto model) throws NotFoundException {
		requireNonNull(model, service.getEntityName() + " entity cannot be null.");

		logger.info("Creating Championship with name {}", model.getName());

		var championshipRegion = findChampionshipRegion(model.getChampionshipRegionId());
		var championshipTeams = findChampionshipTeams(model.getChampionshipTeamsIds());

		var championshipEntity = mapper.toEntityFromCreateModel(model);
		championshipEntity.setChampionshipRegion(championshipRegion);
		championshipEntity.setChampionshipTeams(championshipTeams);

		var createdEntity = service.create(championshipEntity);

		logger.info("Created Championship with id {}", createdEntity.getId());

		return mapper.toDetailModel(createdEntity);
	}

	@Transactional
	@Override
	public ChampionshipDetailDto update(ChampionshipUpdateDto model) throws NotFoundException {
		requireNonNull(model, service.getEntityName() + " entity cannot be null.");

		logger.info("Updating Championship with id {}", model.getId());

		var championshipRegion = findChampionshipRegion(model.getChampionshipRegionId());
		var championshipTeams = findChampionshipTeams(model.getChampionshipTeamsIds());

		var championshipEntity = mapper.toEntityFromUpdateModel(model);
		championshipEntity.setChampionshipRegion(championshipRegion);
		championshipEntity.setChampionshipTeams(championshipTeams);

		var updatedEntity = service.update(championshipEntity);

		logger.info("Updated Championship with id {}", updatedEntity.getId());

		return mapper.toDetailModel(updatedEntity);
	}

	private ChampionshipRegionEntity findChampionshipRegion(UUID regionId) throws NotFoundException {
		return championshipRegionService.findById(regionId)
			.orElseThrow(() -> new NotFoundException(championshipRegionService.getEntityName(), regionId));
	}

	private Set<TeamEntity> findChampionshipTeams(Set<UUID> teamIds) throws NotFoundException {
		return teamIds.stream()
			.map(teamId -> teamService.findById(teamId)
				.orElseThrow(() -> new NotFoundException(teamService.getEntityName(), teamId)))
			.collect(Collectors.toSet());
	}

}
