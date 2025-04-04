package cz.fi.muni.pa165.dto.userService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleViewDto {

	private UUID guid;

	@NotBlank
	private String name;

	@NotBlank
	private String description;

	@NotBlank
	@Size(max = 50)
	private String code;

}
