package cz.fi.muni.pa165.userservice.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "`user`")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class User implements Identifiable {

	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID guid;

	@Column(name = "username", unique = true, nullable = false)
	@Size(min = 3, max = 255)
	@NotNull
	private String username;

	@Column(name = "mail", unique = true, nullable = false)
	@NotNull
	private String mail;

	@Column(name = "name", nullable = false)
	@NotNull
	private String name;

	@Column(name = "surname", nullable = false)
	@NotNull
	private String surname;

	@Column(name = "birth_date")
	private LocalDate birthDate;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Column(name = "is_active", nullable = false)
	@NotNull
	private Boolean isActive = true;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	private Set<UserHasRole> roles = new HashSet<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	private Set<Payment> payments = new HashSet<>();

}
