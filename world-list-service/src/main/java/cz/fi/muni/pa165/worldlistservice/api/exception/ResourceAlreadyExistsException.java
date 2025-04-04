package cz.fi.muni.pa165.worldlistservice.api.exception;

import java.util.UUID;

public class ResourceAlreadyExistsException extends RuntimeException {

	public ResourceAlreadyExistsException(String entityName, UUID entityId) {
		super(entityName + " with ID " + entityId + " already exists and cannot be created.");
	}

}
