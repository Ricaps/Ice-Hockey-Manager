package cz.fi.muni.pa165.worldlistservice.business.services.interfaces;

import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerEntity;

import java.util.UUID;

public interface PlayerService extends GenericService<PlayerEntity> {

	PlayerEntity updateRating(UUID id) throws NotFoundException;

	PlayerEntity updateRating(PlayerEntity player);

}
