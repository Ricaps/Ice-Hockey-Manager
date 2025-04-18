package cz.fi.muni.pa165.gameservice.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "match")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(exclude = "competition")
public class Match {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID guid;

	@ManyToOne
	@JoinColumn(name = "competition_uid", foreignKey = @ForeignKey(name = "match_competition_fk"))
	private Competition competition;

	@ManyToOne
	@NotNull
	@JoinColumn(name = "arena_uid", foreignKey = @ForeignKey(name = "match_arena_fk"), nullable = false)
	private Arena arena;

	@Column(name = "start_at", nullable = false)
	private OffsetDateTime startAt;

	@Column(name = "end_at")
	private OffsetDateTime endAt;

	@Column(name = "home_team_uid", nullable = false)
	private UUID homeTeamUid;

	@Column(name = "away_team_uid", nullable = false)
	private UUID awayTeamUid;

	@Column(name = "match_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private MatchType matchType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "result_uid", foreignKey = @ForeignKey(name = "match_result_fk"))
	private Result result;

}
