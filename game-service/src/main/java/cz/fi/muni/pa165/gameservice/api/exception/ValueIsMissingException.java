package cz.fi.muni.pa165.gameservice.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValueIsMissingException extends RuntimeException {

	public ValueIsMissingException(String message) {
		super(message);
	}

}
