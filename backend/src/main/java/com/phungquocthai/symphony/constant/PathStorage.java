package com.phungquocthai.symphony.constant;

import lombok.Getter;

@Getter
public enum PathStorage {
    AVATAR("/images/avatars/"),
    MUSIC_IMG("/images/music/"),
    LRC("/lrc/"),
    LYRIC("/lyric/"),
    MUSIC_NORMAL("/music/normal/"),
    MUSIC_VIP("/music/vip/");

    private final String path;

    PathStorage(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}