package com.phungquocthai.symphony.constant;

import lombok.Getter;

@Getter
public enum Role {
    USER(0, "Người dùng thông thường"),
    SINGER(1, "Ca sĩ"),
    ADMIN(2, "Quản trị");

    private int value;
    private String description;

    Role(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static Role fromValue(int value) {
        for (Role role : values()) {
            if (role.value == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy quyền với giá trị: " + value);
    }
    
}
