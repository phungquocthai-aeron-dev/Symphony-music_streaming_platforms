import { ListeningStatsDTO } from "./ListeningStats.dto";
import { SongDTO } from "./Song.dto";

export interface RankingDTO {
    topSong: SongDTO[],
    top3: ListeningStatsDTO[][]
}