package com.phungquocthai.symphony.constant;

import lombok.Getter;

@Getter
public enum VipPackage {
    VIP("vip"),
    STUDENT("student"),
	DIAMOND("diamond");

    private String value;

    VipPackage(String value) {
        this.value = value;
    }

    public static VipPackage fromValue(String value) {
        for (VipPackage vipPackage : values()) {
            if (vipPackage.value.equalsIgnoreCase(value)) {
                return vipPackage;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy quyền với giá trị: " + value);
    }
}
