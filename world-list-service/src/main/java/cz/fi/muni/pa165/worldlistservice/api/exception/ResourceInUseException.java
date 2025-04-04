package cz.fi.muni.pa165.worldlistservice.api.exception;

import java.util.UUID;

public class ResourceInUseException extends RuntimeException {

	public ResourceInUseException(String entityName, UUID entityId) {
		super(entityName + " with ID " + entityId
				+ " is being used by another resource. Delete the resources of entity " + entityName + " first.");
	}

}
