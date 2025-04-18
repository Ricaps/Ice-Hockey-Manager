package cz.fi.muni.pa165.gameservice.utils;

import jakarta.annotation.Nullable;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckExceptionMatcher implements ResultMatcher {

	private Class<? extends Exception> exceptionClass;

	private String message;

	public CheckExceptionMatcher isInstanceOf(Class<? extends Exception> exceptionClass) {
		this.exceptionClass = exceptionClass;

		return this;
	}

	public CheckExceptionMatcher hasMessage(String message, @Nullable Object... args) {
		this.message = message.formatted(args);

		return this;
	}

	@Override
	public void match(MvcResult result) throws Exception {
		var resolvedException = result.getResolvedException();

		if (exceptionClass != null) {
			assertThat(resolvedException).isInstanceOf(exceptionClass);
		}

		if (message != null) {
			assertThat(resolvedException).hasMessage(message);
		}
	}

}
