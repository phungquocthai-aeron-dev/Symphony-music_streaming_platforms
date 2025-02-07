package com.phungquocthai.symphony.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import com.phungquocthai.symphony.entity.Subscription;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDTO {

	@NotNull(message = "Id không được để trống")
    private Integer subscriptionId;
    
    @NotNull(message = "Vui lòng chọn người dùng đăng ký")
    private Integer userId;
    
    @NotNull(message = "Vui lòng nhập thông tin gói đăng ký")
    private Integer vipId;
    
    @NotNull(message = "Vui lòng chọn ngày hiệu lực")
    private LocalDate startDate;
    
    @NotNull(message = "Vui lòng chọn ngày hết hạn")
    private LocalDate endDate;
    
    @NotNull(message = "Vui lòng nhập trạng thái")
    private String status;
    
    @NotNull(message = "Vui lòng chọn mã giao dịch")
    private String paymentId;
    
    @NotNull(message = "Vui lòng chọn ngày giao dịch")
    private LocalDate createdAt;
    
    public SubscriptionDTO(Subscription subscription) {
    	this.subscriptionId = subscription.getSubscription_id();
    	this.startDate = subscription.getStart_date();
    	this.endDate = subscription.getEnd_date();
    	this.status = subscription.getStatus();
    	this.paymentId = subscription.getPayment_id();
    	this.createdAt = subscription.getCreated_at();
    	this.userId = (subscription.getUser().getUserId() != null) ? subscription.getUser().getUserId() : null;
    	this.vipId = (subscription.getVip().getVip_id() != null) ? subscription.getVip().getVip_id() : null;
    }
}
