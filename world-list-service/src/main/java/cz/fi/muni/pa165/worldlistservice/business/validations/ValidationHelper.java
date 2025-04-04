package cz.fi.muni.pa165.worldlistservice.business.validations;

import cz.fi.muni.pa165.worldlistservice.api.exception.ValueIsMissingException;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

public class ValidationHelper {

	/**
	 * Checks if the passed object is null. If so, the method throws
	 * {@link ValueIsMissingException}
	 * @param object object to be checked for null
	 * @param message message to be passed into the exception
	 * @throws ValueIsMissingException exception that invokes 400 Bad Request status code
	 */
	public static void requireNonNull(Object object, String message) throws ValueIsMissingException {
		if (object == null) {
			throw new ValueIsMissingException(message);
		}
	}

	/**
	 * Checks whether given collection is empty
	 * @param collection to be checked collection
	 * @param message message to be thrown with the exception
	 * @throws ValueIsMissingException exception that invokes 400 Bad Request status code
	 */
	public static void requireNotEmpty(@NotNull Collection<?> collection, String message)
			throws ValueIsMissingException {
		if (collection.isEmpty()) {
			throw new ValueIsMissingException(message);
		}
	}

}
