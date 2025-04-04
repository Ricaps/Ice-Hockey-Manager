package cz.fi.muni.pa165.gameservice.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "competition")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Competition {

	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID guid;

	@Column(name = "name", nullable = false)
	@Size(max = 255)
	@NotNull
	private String name;

	@Column(name = "start_at", nullable = false)
	@NotNull
	private LocalDate startAt;

	@Column(name = "end_at", nullable = false)
	@NotNull
	private LocalDate endAt;

	@OneToMany(mappedBy = "competition")
	private Set<CompetitionHasTeam> teams;

	@OneToMany(mappedBy = "competition")
	private Set<Match> matches;

}
