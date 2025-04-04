package cz.fi.muni.pa165.worldlistservice.business.facades.interfaces;

import cz.fi.muni.pa165.dto.worldlistservice.BaseDto;
import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface GenericFacade<TDetailModel extends BaseDto & Identifiable, TListModel extends BaseDto & Identifiable, TCreateModel, TUpdateModel extends BaseDto & Identifiable> {

	TDetailModel create(TCreateModel model);

	Optional<TDetailModel> findById(UUID id);

	Page<TListModel> findAll(Pageable pageable);

	TDetailModel update(TUpdateModel model);

	void delete(UUID id);

}