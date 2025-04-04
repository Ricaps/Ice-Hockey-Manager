package cz.fi.muni.pa165.worldlistservice.business.services.interfaces;

import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface GenericService<E extends BaseEntity & Identifiable> {

	E create(E entity);

	Optional<E> findById(UUID id);

	Page<E> findAll(Pageable pageable);

	E update(E entity) throws NotFoundException;

	void delete(UUID id) throws NotFoundException;

	String getEntityName();

	boolean isEntityUsed(UUID id) throws NotFoundException;

}
