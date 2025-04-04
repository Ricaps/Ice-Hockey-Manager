package cz.fi.muni.pa165.gameservice.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ActionForbidden extends RuntimeException {

	public ActionForbidden(String message) {
		super(message);
	}

}
