package cz.fi.muni.pa165.worldlistservice.business.mappers;

import cz.fi.muni.pa165.dto.worldlistservice.BaseDto;
import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.BaseEntity;
import org.mapstruct.IterableMapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

public interface GenericMapper<TDetailModel extends BaseDto & Identifiable, TListModel extends BaseDto & Identifiable, TCreateModel, TUpdateModel extends BaseDto & Identifiable, TEntity extends BaseEntity & Identifiable> {

	@Named("toDetailModel")
	TDetailModel toDetailModel(TEntity entity);

	@Named("toListModel")
	TListModel toListModel(TEntity entity);

	TEntity toEntityFromCreateModel(TCreateModel createModel);

	TEntity toEntityFromUpdateModel(TUpdateModel updateModel);

	@IterableMapping(qualifiedByName = "toListModel")
	List<TListModel> toModelList(List<TEntity> entities);

	default Page<TListModel> toPageModel(Page<TEntity> page) {
		List<TListModel> models = toModelList(page.getContent());
		return new PageImpl<>(models, page.getPageable(), page.getTotalElements());
	}

}
