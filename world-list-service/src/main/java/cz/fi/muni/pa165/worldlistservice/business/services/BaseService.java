package cz.fi.muni.pa165.worldlistservice.business.services;

import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ResourceAlreadyExistsException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.GenericService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.BaseEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

import static cz.fi.muni.pa165.worldlistservice.business.validations.ValidationHelper.requireNonNull;

public abstract class BaseService<E extends BaseEntity & Identifiable> implements GenericService<E> {

	protected final Repository<E> repository;

	protected BaseService(Repository<E> repository) {
		this.repository = repository;
	}

	@Override
	public E create(E entity) throws ValueIsMissingException {
		requireNonNull(entity, getEntityName() + " entity cannot be null.");

		if (entity.getId() != null && repository.existsById(entity.getId())) {
			throw new ResourceAlreadyExistsException(getEntityName(), entity.getId());
		}

		return repository.save(entity);
	}

	@Override
	public Optional<E> findById(UUID id) throws ValueIsMissingException {
		requireNonNull(id, getEntityName() + " ID cannot be null.");
		return repository.findById(id);
	}

	@Override
	public Page<E> findAll(Pageable pageable) throws ValueIsMissingException {
		requireNonNull(pageable, "Pageable for " + getEntityName() + " cannot be null.");
		return repository.findAll(pageable);
	}

	@Override
	public E update(E entity) throws ValueIsMissingException, NotFoundException {
		requireNonNull(entity, getEntityName() + " entity cannot be null.");

		if (!repository.existsById(entity.getId())) {
			throw new NotFoundException(getEntityName(), entity.getId());
		}
		return repository.save(entity);
	}

	@Override
	public void delete(UUID id) throws ValueIsMissingException, NotFoundException {
		requireNonNull(id, getEntityName() + " ID cannot be null.");

		if (!repository.existsById(id)) {
			throw new NotFoundException(getEntityName(), id);
		}
		repository.deleteById(id);
	}

	public abstract String getEntityName();

	public abstract boolean isEntityUsed(UUID id);

}