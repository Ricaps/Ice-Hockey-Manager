package cz.fi.muni.pa165.gameservice.business.services.seed;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SeedDataNotExist extends RuntimeException {

	public SeedDataNotExist(String message) {
		super(message);
	}

}
