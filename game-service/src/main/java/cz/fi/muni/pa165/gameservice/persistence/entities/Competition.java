package cz.fi.muni.pa165.gameservice.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "competition")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = { "teams", "matches" })
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
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Builder.Default
	private Set<CompetitionHasTeam> teams = new HashSet<>();

	@OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
	@Builder.Default
	private Set<Match> matches = new HashSet<>();

}
