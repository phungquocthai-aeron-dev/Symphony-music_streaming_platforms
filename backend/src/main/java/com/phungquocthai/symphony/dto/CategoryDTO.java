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

    private Integer category_id;
    
    private String category_name;
    
    public CategoryDTO(Category category) {
    }
}
