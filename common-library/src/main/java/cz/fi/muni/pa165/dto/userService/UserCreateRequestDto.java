package cz.fi.muni.pa165.dto.userService;

import jakarta.validation.constraints.NotBlank;
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
public class UserCreateRequestDto {

	@Size(min = 3, max = 50)
	@NotBlank
	private String username;

	@Size(min = 2, max = 50)
	@NotBlank
	private String name;

	@Size(min = 2, max = 50)
	@NotBlank
	private String surname;

	private LocalDate birthDate;

}
