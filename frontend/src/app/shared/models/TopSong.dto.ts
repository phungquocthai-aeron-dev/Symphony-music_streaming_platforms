import { SingerDTO } from "./Singer.dto";

export interface TopSongDTO {
    song_id: number;
    songName: string;
    song_img: string;
    total_listens: number;
    path: string;
    lyric: string;
    lrc: string;
    duration: number;
    releaseDate: Date;
    author: string;
    isVip: boolean;
    total_listens_per_hour?: number;
    categoryIds: number[];
    singers: SingerDTO[];
    favorite: boolean;
    active: boolean;
}