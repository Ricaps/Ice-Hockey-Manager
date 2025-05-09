package cz.fi.muni.pa165.dto.userService;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

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

	@Size(min = 2, max = 50)
	@NotBlank
	private String name;

	@Size(min = 2, max = 50)
	@NotBlank
	private String surname;

	private LocalDate birthDate;

	public static UserCreateDto createFromRequest(UserCreateRequestDto dto, String mail) {
		return UserCreateDto.builder()
			.username(dto.getUsername())
			.name(dto.getName())
			.surname(dto.getSurname())
			.birthDate(dto.getBirthDate())
			.mail(mail)
			.build();
	}

}
