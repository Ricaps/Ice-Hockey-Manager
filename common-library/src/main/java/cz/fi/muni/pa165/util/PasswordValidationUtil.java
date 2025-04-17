package cz.fi.muni.pa165.util;

public class PasswordValidationUtil {

	public static final String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&-+/|]).{8,}$";

	public static final String requirementDescription = "Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 number "
			+ "and at lease one of those special characters: @$!%*?&-+/|";

	public static boolean isPasswordValid(String password) {
		return password.matches(passwordRegex);
	}

}
