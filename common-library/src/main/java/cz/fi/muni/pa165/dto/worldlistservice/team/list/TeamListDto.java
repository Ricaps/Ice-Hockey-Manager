package cz.fi.muni.pa165.dto.worldlistservice.team.list;

import cz.fi.muni.pa165.dto.worldlistservice.BaseDto;
import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamListDto extends BaseDto implements Identifiable {

	UUID id;

	String name;

}
