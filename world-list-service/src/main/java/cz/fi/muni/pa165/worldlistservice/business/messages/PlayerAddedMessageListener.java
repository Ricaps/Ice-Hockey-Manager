package cz.fi.muni.pa165.worldlistservice.business.messages;

import cz.fi.muni.pa165.dto.worldlistservice.player.update.PlayerUpdateDto;
import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.PlayerCharacteristicDto;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.PlayerFacade;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PlayerAddedMessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerAddedMessageListener.class);

	private final PlayerFacade playerFacade;

	@Autowired
	PlayerAddedMessageListener(PlayerFacade playerFacade) {
		this.playerFacade = playerFacade;
	}

	@JmsListener(destination = "team.player.added", containerFactory = "queueListenerFactory")
	public void onPlayerAdded(@Nonnull UUID playerUuid) {
		LOGGER.info("Received player UUID from topic: {}", playerUuid);

		var playerOption = playerFacade.findById(playerUuid);

		if (playerOption.isEmpty()) {
			return;
		}

		var player = playerOption.get();

		var playerUpdateDto = PlayerUpdateDto.builder()
			.id(playerUuid)
			.teamId(player.getTeam().getId())
			.firstName(player.getFirstName())
			.lastName(player.getLastName())
			.playerCharacteristicsIds(player.getPlayerCharacteristics()
				.stream()
				.map(PlayerCharacteristicDto::getId)
				.collect(Collectors.toSet()))
			.marketValue(player.getMarketValue())
			.used(true)
			.build();

		playerFacade.update(playerUpdateDto);

		LOGGER.info("Updated player {} - used in team", playerUuid);
	}

}
