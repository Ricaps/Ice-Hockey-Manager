package cz.fi.muni.pa165.worldlistservice.persistence.entities;

import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@MappedSuperclass
public abstract class BaseEntity implements Identifiable {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	UUID id;

}
