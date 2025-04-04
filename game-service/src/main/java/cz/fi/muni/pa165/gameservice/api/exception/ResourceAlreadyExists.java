package cz.fi.muni.pa165.gameservice.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExists extends RuntimeException {

	public ResourceAlreadyExists(String message) {
		super(message);
	}

}
