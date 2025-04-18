package cz.fi.muni.pa165.gameservice.utils;

public final class Assertions {

	private Assertions() {
		super();
	}

	public static CheckExceptionMatcher exception() {
		return new CheckExceptionMatcher();
	}

}
