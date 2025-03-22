package com.phungquocthai.symphony.constant;

import lombok.Getter;

@Getter
public enum Role {
    USER("USER", "Người dùng thông thường"),
    SINGER("SINGER", "Ca sĩ"),
    ADMIN("ADMIN", "Quản trị"),
	VIP("VIP", "Vip");

    private String value;
    private String description;

    Role(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static Role fromValue(String value) {
        for (Role role : values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy quyền với giá trị: " + value);
    }
    
}
