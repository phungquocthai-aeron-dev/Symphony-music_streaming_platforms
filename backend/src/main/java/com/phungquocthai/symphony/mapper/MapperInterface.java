package com.phungquocthai.symphony.mapper;

import java.util.List;
import org.mapstruct.MappingTarget;

public interface MapperInterface<Entity, DTO> {
	Entity toEntity(DTO dto);
	DTO toDTO(Entity entity);
	List<Entity> toListEntity(List<DTO> entities);
	List<DTO> toListDTO(List<Entity> dtos);
	void updateEntity(@MappingTarget Entity entity, DTO dto);
}
