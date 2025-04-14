package cz.fi.muni.pa165.worldlistservice.business.services;

import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.TeamService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.TeamEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TeamServiceImpl extends BaseService<TeamEntity> implements TeamService {

	@Autowired
	public TeamServiceImpl(TeamRepository repository) {
		super(repository);
	}

	@Override
	public String getEntityName() {
		return "Team";
	}

	@Override
	public boolean isEntityUsed(UUID id) throws NotFoundException {
		var entity = repository.findById(id).orElseThrow(() -> new NotFoundException(this.getEntityName(), id));

		return !entity.getTeamPlayers().isEmpty();
	}

}
