package cz.fi.muni.pa165.gameservice.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "arena")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Arena {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID guid;

	@Column(name = "country_code", nullable = false)
	@Size(max = 3)
	private String countryCode;

	@Column(name = "city_name", nullable = false)
	@Size(max = 255)
	private String cityName;

	@Column(name = "arena_name", nullable = false)
	@Size(max = 255)
	private String arenaName;

}
