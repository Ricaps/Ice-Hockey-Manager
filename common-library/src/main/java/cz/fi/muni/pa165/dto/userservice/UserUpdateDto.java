package cz.fi.muni.pa165.dto.userservice;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

	private UUID guid;

	@NotBlank
	private String username;

	@NotBlank
	private String name;

	@NotBlank
	private String surname;

	private LocalDate birthDate;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof UserUpdateDto that))
			return false;

		return Objects.equals(guid, that.guid) && Objects.equals(username, that.username)
				&& Objects.equals(name, that.name) && Objects.equals(surname, that.surname)
				&& Objects.equals(birthDate, that.birthDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(guid, username, name, surname, birthDate);
	}

}
