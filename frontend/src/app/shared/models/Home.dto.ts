import { ListeningStatsDTO } from "./ListeningStats.dto";
import { SongDTO } from "./Song.dto"
import { TopSongDTO } from "./TopSong.dto";

export interface HomeDTO {
    hotHitSong: SongDTO[],
    vpopSongs: SongDTO[],
    usUkSongs: SongDTO[],
    remixSongs: SongDTO[],
    lyricalSongs: SongDTO[],
    newSongs: SongDTO[],
    recentlyListen: SongDTO[],
    recommend: SongDTO[],
    topSongs: TopSongDTO[];
    topTrendingStats: ListeningStatsDTO[];
}