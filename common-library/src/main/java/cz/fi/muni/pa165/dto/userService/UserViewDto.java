package cz.fi.muni.pa165.dto.userService;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserViewDto {

	private UUID guid;

	private UUID teamId;

	@NotBlank
	private String username;

	@Email
	@NotBlank
	private String mail;

	private Boolean isActive;

	private LocalDateTime deletedAt;

	@NotBlank
	private String name;

	@NotBlank
	private String surname;

	private LocalDate birthDate;

	private List<RoleViewDto> roles;

	private List<UserPaymentDto> payments;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof UserViewDto that))
			return false;

		return Objects.equals(guid, that.guid) && Objects.equals(username, that.username)
				&& Objects.equals(mail, that.mail) && Objects.equals(isActive, that.isActive)
				&& Objects.equals(deletedAt, that.deletedAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(guid, username, mail, isActive, deletedAt);
	}

}
