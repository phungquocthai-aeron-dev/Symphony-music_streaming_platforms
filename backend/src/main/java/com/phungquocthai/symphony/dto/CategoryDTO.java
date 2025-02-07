package com.phungquocthai.symphony.dto;

import com.phungquocthai.symphony.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    private Integer categoryId;
    
    private String categoryName;
    
    public CategoryDTO(Category category) {
    	this.categoryId = category.getCategory_id();
    	this.categoryName = category.getCategory_name();
    }
}
