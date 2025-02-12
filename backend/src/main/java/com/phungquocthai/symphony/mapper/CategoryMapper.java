package com.phungquocthai.symphony.mapper;

import org.mapstruct.Mapper;

import com.phungquocthai.symphony.dto.CategoryDTO;
import com.phungquocthai.symphony.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper extends MapperInterface<Category, CategoryDTO> {

}
