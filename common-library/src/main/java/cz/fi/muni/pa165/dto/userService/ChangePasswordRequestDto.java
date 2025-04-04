package cz.fi.muni.pa165.dto.userService;

import cz.fi.muni.pa165.util.PasswordValidationUtil;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {

	private String oldPassword;

	@Pattern(regexp = PasswordValidationUtil.passwordRegex, message = PasswordValidationUtil.requirementDescription)
	private String newPassword;

	private UUID userId;

}
