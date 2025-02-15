package com.phungquocthai.symphony.constant;

import lombok.Getter;

@Getter
public enum CategorySong {
	LYRICAL("nhactrutinh"),
    VPOP("v-pop"),
	REMIX("remix"),
	USUK("us-uk"),
	RAP("rap"),
	EDM("edm"),
	BALLAD("ballad"),
	KPOP("k-pop");

    private String value;

    CategorySong(String value) {
        this.value = value;
    }

    public static CategorySong fromValue(String value) {
        for (CategorySong categorySong : values()) {
            if (categorySong.value.equalsIgnoreCase(value)) {
                return categorySong;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy quyền với giá trị: " + value);
    }
}
