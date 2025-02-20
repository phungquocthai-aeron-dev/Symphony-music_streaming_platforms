package com.phungquocthai.symphony.dto;

import java.time.LocalDate;
import java.util.Set;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class SongCreateDTO {

	@NotNull(message = "Vui lòng nhập tên bài hát")
    private String songName;
    
    @NotNull(message = "Vui lòng chọn lượt nghe")
    @Min(value = 0, message = "Lượt nghe phải lớn hơn hoặc bằng 0")
    @Builder.Default
    private Integer total_listens = 0;
    
    @NotNull(message = "Vui lòng chọn thời lượng bài hát")
    private Integer duration;
    
    @NotNull(message = "Vui lòng chọn ngày phát hành")
    private LocalDate releaseDate;
    
    @NotNull(message = "Vui lòng chọn tên tác giả")
    private String author;
    
    @NotNull(message = "Vui lòng chọn đặc quyền bài hát")
    private Boolean isVip;
    
    @NotEmpty(message = "Vui lòng chọn thể loại cho bài hát")
    private Set<Integer> categoryIds;
    
    @NotEmpty(message = "Vui lòng chọn ca sĩ thể hiện")
    private Set<Integer> singersId;

}
