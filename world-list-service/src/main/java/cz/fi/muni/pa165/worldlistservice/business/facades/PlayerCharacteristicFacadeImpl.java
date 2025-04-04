package cz.fi.muni.pa165.worldlistservice.business.facades;

import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.PlayerCharacteristicDto;
import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.create.PlayerCharacteristicCreateDto;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.PlayerCharacteristicFacade;
import cz.fi.muni.pa165.worldlistservice.business.mappers.PlayerCharacteristicMapper;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.PlayerCharacteristicService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerCharacteristicEntity;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PlayerCharacteristicFacadeImpl extends
		BaseFacade<PlayerCharacteristicDto, PlayerCharacteristicDto, PlayerCharacteristicCreateDto, PlayerCharacteristicDto, PlayerCharacteristicEntity>
		implements PlayerCharacteristicFacade {

	@Autowired
	public PlayerCharacteristicFacadeImpl(PlayerCharacteristicService service,
			@Qualifier("playerCharacteristicMapperImpl") PlayerCharacteristicMapper mapper) {
		super(service, mapper, LoggerFactory.getLogger(PlayerCharacteristicFacadeImpl.class));
	}

}
