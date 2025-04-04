package cz.fi.muni.pa165.worldlistservice.api.exception;

import java.util.UUID;

public class NotFoundException extends RuntimeException {

	public NotFoundException(String entityName, UUID entityId) {
		super(entityName + " with ID " + entityId + " not found.");
	}

}
