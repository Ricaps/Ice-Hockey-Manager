package cz.fi.muni.pa165.gameservice.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "result")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "match")
public class Result {

	@Id
	@Column(name = "match_uid")
	private UUID matchGuid;

	@OneToOne
	@MapsId("match_uid")
	@JoinColumn(name = "match_uid", foreignKey = @ForeignKey(name = "result_match_fk"))
	private Match match;

	@Column(name = "winner_team")
	private UUID winnerTeam;

	@Column(name = "score_home_team")
	private int scoreHomeTeam;

	@Column(name = "score_away_team")
	private int scoreAwayTeam;

}
