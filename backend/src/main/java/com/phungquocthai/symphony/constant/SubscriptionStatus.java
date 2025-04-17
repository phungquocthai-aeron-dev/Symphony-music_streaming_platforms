package com.phungquocthai.symphony.constant;

public enum SubscriptionStatus {
    PENDING("Chờ thanh toán"),
    ACTIVE("Đang hoạt động"),
    CANCELLED("Đã huỷ"),
    EXPIRED("Hết hạn"),
    FAILED("Thất bại");

    private final String displayName;

    SubscriptionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
