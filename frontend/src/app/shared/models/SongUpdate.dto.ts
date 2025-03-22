import { SingerDTO } from "./Singer.dto";

export interface SongUpdateDTO {
    songName: string;
    song_img: string;
    total_listens: number;
    duration: number;
    releaseDate: Date;
    author: string;
    isVip: boolean;
    categoriesId: number[];
    singers: SingerDTO[];
}