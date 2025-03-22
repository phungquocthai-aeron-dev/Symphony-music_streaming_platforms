import { ListeningStatsDTO } from "./ListeningStats.dto";
import { TopSongDTO } from "./TopSong.dto";

export interface RankingDTO {
    topSong: TopSongDTO[],
    top3: ListeningStatsDTO[][]
}