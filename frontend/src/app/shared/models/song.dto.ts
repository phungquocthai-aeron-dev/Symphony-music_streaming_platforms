import { CategoryDTO } from "./Category.dto";
import { SingerDTO } from "./Singer.dto";

export interface SongDTO {
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
    categories: CategoryDTO[];
    singers: SingerDTO[];
    favorite: boolean;
}