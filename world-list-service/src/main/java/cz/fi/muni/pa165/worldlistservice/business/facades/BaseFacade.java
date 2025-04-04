package cz.fi.muni.pa165.worldlistservice.business.facades;

import cz.fi.muni.pa165.dto.worldlistservice.BaseDto;
import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import cz.fi.muni.pa165.worldlistservice.api.exception.ResourceInUseException;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.GenericFacade;
import cz.fi.muni.pa165.worldlistservice.business.mappers.GenericMapper;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.GenericService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.BaseEntity;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public abstract class BaseFacade<TDetailModel extends BaseDto & Identifiable, TListModel extends BaseDto & Identifiable, TCreateModel, TUpdateModel extends BaseDto & Identifiable, TEntity extends BaseEntity & Identifiable>
		implements GenericFacade<TDetailModel, TListModel, TCreateModel, TUpdateModel> {

	protected final Logger logger;

	protected final GenericService<TEntity> service;

	protected final GenericMapper<TDetailModel, TListModel, TCreateModel, TUpdateModel, TEntity> mapper;

	protected BaseFacade(GenericService<TEntity> service,
			GenericMapper<TDetailModel, TListModel, TCreateModel, TUpdateModel, TEntity> mapper, Logger logger) {
		this.service = service;
		this.mapper = mapper;
		this.logger = logger;
	}

	@Transactional
	@Override
	public TDetailModel create(TCreateModel model) {
		logger.info("Creating {}", service.getEntityName());

		TEntity entity = mapper.toEntityFromCreateModel(model);
		TEntity savedEntity = service.create(entity);

		logger.info("Created {}", service.getEntityName());

		return mapper.toDetailModel(savedEntity);
	}

	@Transactional
	@Override
	public Optional<TDetailModel> findById(UUID id) {
		logger.info("Finding {} with id {}", service.getEntityName(), id);

		return service.findById(id).map(mapper::toDetailModel);
	}

	@Transactional
	@Override
	public Page<TListModel> findAll(Pageable pageable) {
		logger.info("Finding {} with pageable {}", service.getEntityName(), pageable);

		Page<TEntity> entityPage = service.findAll(pageable);

		logger.info("Found {} entities with pageable {}", service.getEntityName(), pageable);
		return mapper.toPageModel(entityPage);
	}

	@Transactional
	@Override
	public TDetailModel update(TUpdateModel model) {
		logger.info("Updating {} with id {}", service.getEntityName(), model != null ? model.getId() : "null");

		TEntity entity = mapper.toEntityFromUpdateModel(model);
		TEntity updatedEntity = service.update(entity);

		logger.info("Updated {} with id {}", service.getEntityName(), model != null ? model.getId() : "null");

		return mapper.toDetailModel(updatedEntity);
	}

	@Transactional
	@Override
	public void delete(UUID id) {
		logger.info("Deleting {} with id {}", service.getEntityName(), id);

		if (service.isEntityUsed(id)) {
			throw new ResourceInUseException(service.getEntityName(), id);
		}

		service.delete(id);

		logger.info("Deleted {} with id {}", service.getEntityName(), id);
	}

}
