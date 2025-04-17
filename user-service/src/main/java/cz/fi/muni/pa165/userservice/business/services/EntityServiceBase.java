package cz.fi.muni.pa165.userservice.business.services;

import cz.fi.muni.pa165.userservice.persistence.entities.Identifiable;

import java.util.Optional;
import java.util.UUID;

public abstract class EntityServiceBase<E extends Identifiable> {

	protected Boolean areEntitiesDuplicated(E entityToStore, Optional<E> existingEntity) {
		boolean sameEntityExists = existingEntity.isPresent();
		if (!sameEntityExists)
			return false;

		boolean entityIsNew = entityToStore.getGuid() == null;
		if (entityIsNew)
			return true;

		UUID existingEntityId = existingEntity.map(Identifiable::getGuid).orElse(null);

		return !entityToStore.getGuid().equals(existingEntityId);
	}

}
