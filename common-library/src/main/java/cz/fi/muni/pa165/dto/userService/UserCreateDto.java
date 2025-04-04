package cz.fi.muni.pa165.dto.userService;

import cz.fi.muni.pa165.util.PasswordValidationUtil;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

	@Size(min = 3, max = 50)
	@NotBlank
	private String username;

	@Email
	@NotBlank
	private String mail;

	@NotBlank
	@Pattern(regexp = PasswordValidationUtil.passwordRegex, message = PasswordValidationUtil.requirementDescription)
	private String password;

	@Size(min = 2, max = 50)
	@NotBlank
	private String name;

	@Size(min = 2, max = 50)
	@NotBlank
	private String surname;

	private LocalDate birthDate;

}
