package cz.fi.muni.pa165.dto.worldlistservice;

import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public abstract class BaseDto implements Identifiable {

	UUID id;

}
