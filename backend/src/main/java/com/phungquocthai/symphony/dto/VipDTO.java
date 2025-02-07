package com.phungquocthai.symphony.dto;

import java.math.BigDecimal;

import com.phungquocthai.symphony.entity.Vip;

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
public class VipDTO {
	
	@NotNull(message = "Id không được để trống")
    private Integer vipId;
    
    @NotNull(message = "Vui lòng chọn tên gói vip")
    private String vipTitle;
    
    @NotNull(message = "Vui lòng nhập thông tin mô tả gói vip")
    private String description;
    
    @NotNull(message = "Vui lòng chọn thời hạn gói vip")
    private int durationDays;
    
    @NotNull(message = "Vui lòng chọn giá trị gói vip")
    private BigDecimal price;
    
	public VipDTO(Vip vip) {
		this.vipId = vip.getVip_id();
		this.vipTitle = vip.getVip_title();
		this.description = vip.getDescription();
		this.durationDays = vip.getDuration_days();
		this.price = vip.getPrice();
	}
}
