package cz.fi.muni.pa165.worldlistservice.business.facades.interfaces;

import cz.fi.muni.pa165.dto.worldlistservice.player.create.PlayerCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.detail.PlayerDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.list.PlayerListDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.update.PlayerUpdateDto;

public interface PlayerFacade extends GenericFacade<PlayerDetailDto, PlayerListDto, PlayerCreateDto, PlayerUpdateDto> {

}
