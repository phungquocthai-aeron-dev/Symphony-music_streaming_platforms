import { SingerDTO } from "./Singer.dto"
import { SongDTO } from "./Song.dto"

export interface SearchDTO {
    songs: SongDTO[],
    singers: SingerDTO[]
}