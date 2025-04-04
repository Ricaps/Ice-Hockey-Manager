package cz.fi.muni.pa165.gameservice.api.exception;

import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
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
	 * @see #validateConstraints(Object, String)
	 */
	public static void validateConstraints(Object object) throws ConstraintViolationException {
		validateConstraints(object, null);
	}

	/**
	 * Validates constraints defined by {@link jakarta.validation} annotations
	 * @param object object to be validated
	 * @param message message to be passed into the exception, might be null
	 * @throws ConstraintViolationException exception thrown by validator when object is
	 * invalid
	 */
	public static void validateConstraints(Object object, @Nullable String message)
			throws ConstraintViolationException {
		try (final var validator = Validation.buildDefaultValidatorFactory()) {
			var violations = validator.getValidator().validate(object);
			if (!violations.isEmpty()) {
				throw new ConstraintViolationException(message, violations);
			}
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
