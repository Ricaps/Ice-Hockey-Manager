package cz.fi.muni.pa165.userservice.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "user_has_role")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserHasRole implements Identifiable {

	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID guid;

	@ManyToOne
	@NotNull
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@NotNull
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

}
