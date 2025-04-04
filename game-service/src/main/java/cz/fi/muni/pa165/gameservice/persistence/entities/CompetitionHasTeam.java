package cz.fi.muni.pa165.gameservice.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/**
 * Fictive Many-to-Many relation
 */
@Entity
@Table(name = "competition_has_teams")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CompetitionHasTeam {

	@Id
	@NotNull
	@Column(name = "team_uid", nullable = false)
	private UUID teamUid;

	@ManyToOne
	@NotNull
	@JoinColumn(name = "competition_uid", foreignKey = @ForeignKey(name = "competition_teams_fk"), nullable = false)
	private Competition competition;

}
