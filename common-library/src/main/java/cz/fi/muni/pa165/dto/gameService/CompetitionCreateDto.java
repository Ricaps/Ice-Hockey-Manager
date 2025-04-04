package cz.fi.muni.pa165.dto.gameService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
public class CompetitionCreateDto {

	@Size(min = 1, max = 255)
	private String name;

	@NotNull
	private LocalDate startAt;

	@NotNull
	private LocalDate endAt;

}
