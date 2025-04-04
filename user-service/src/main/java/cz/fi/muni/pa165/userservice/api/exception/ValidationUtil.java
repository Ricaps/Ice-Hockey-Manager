package cz.fi.muni.pa165.userservice.api.exception;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.validation.ValidationException;

public class ValidationUtil {

	public static void requireNotNull(Object object, String message) {
		if (object == null) {
			throw new BlankValueException(message);
		}
	}

	public static void requireNull(Object object, String message) {
		if (object != null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void requireNotBlankString(String string, String message) {
		if (string == null || string.isEmpty() || string.trim().isEmpty()) {
			throw new BlankValueException(message);
		}
	}

	public static void requireValidEmailAddress(String emailAddress, String message) {
		try {
			new InternetAddress(emailAddress);
		}
		catch (AddressException e) {
			throw new ValidationException(message, e);
		}
	}

}
