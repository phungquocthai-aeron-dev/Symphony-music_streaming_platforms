package com.phungquocthai.symphony.constant;

import lombok.Getter;

@Getter
public enum CategorySong {
	LYRICAL("LYRICAL"),
    VPOP("VPOP"),
	REMIX("REMIX"),
	USUK("USUK"),
	RAP("RAP"),
	EDM("EDM"),
	BALLAD("BALLAD"),
	KPOP("KPOP");

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
