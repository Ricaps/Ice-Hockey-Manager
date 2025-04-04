package cz.fi.muni.pa165.dto.worldlistservice.championship.list;

import cz.fi.muni.pa165.dto.worldlistservice.BaseDto;
import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder
public class ChampionshipListDto extends BaseDto implements Identifiable {

	UUID id;

	String name;

}
