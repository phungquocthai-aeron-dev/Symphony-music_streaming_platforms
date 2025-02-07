package com.phungquocthai.symphony.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

import com.phungquocthai.symphony.entity.Favorite;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteDTO {
 
    @NotNull(message = "Id không được để trống")
    private Integer favoriteId;
    
    @NotNull(message = "Vui lòng chọn thời gian thêm vào yêu thích")
    private LocalDate addAt;
    
    public FavoriteDTO(Favorite favorite) {
    	this.favoriteId = favorite.getFavorite_id();
    	this.addAt = favorite.getAdd_at();
    }
}