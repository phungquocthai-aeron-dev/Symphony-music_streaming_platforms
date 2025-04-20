import { Component, OnInit } from '@angular/core';
import { ListeningStatsDTO } from '../../shared/models/ListeningStats.dto';
import { TopSongDTO } from '../../shared/models/TopSong.dto';
import { SongService } from '../../core/services/song.service';
import { DataShareService } from '../../core/services/dataShare.service';
import { ResponseData } from '../../shared/models/ResponseData';
import { RankingDTO } from '../../shared/models/Ranking.dto';
import { NgFor, NgIf } from '@angular/common';
import { LineChartComponent } from '../../shared/components/line-chart/line-chart.component';
import { RankCardComponent } from '../../shared/components/rank-card/rank-card.component';
import { AuthService } from '../../core/services/auth.service';
import { PlaylistDTO } from '../../shared/models/Playlist.dto';
import { PlaylistService } from '../../core/services/playlist.service';

@Component({
  selector: 'app-rank',
  imports: [NgFor, NgIf, LineChartComponent, RankCardComponent],
  templateUrl: './rank.component.html',
  styleUrl: './rank.component.css'
})
export class RankComponent implements OnInit {
  top3: ListeningStatsDTO[][] = [];
  topSongs: TopSongDTO[] = [];
  playlists: PlaylistDTO[] = [];
  

  constructor(
    private songService: SongService,
    private authService: AuthService,
    private dataShareService: DataShareService,
    private playlistService: PlaylistService
  ) {}

  ngOnInit(): void {
    this.dataShareService.changeLeftSideInfo("Rank");
    this.dataShareService.changeTitle("Bảng xếp hạng");

    this.loadData();
  }

  loadData() {
    this.songService.getTopSong().subscribe({
      next: (response: ResponseData<RankingDTO>) => {
        this.top3 = response.result.top3;
        this.topSongs = response.result.topSong;

        console.log(this.top3)
        console.log(this.topSongs)
      },
      error: (error) => {
        console.error(error);
      }
    });

    const user = this.authService.getUserInfo()
    if(user) {
      this.playlistService.getPlaylistByUserId(user.userId).subscribe({
        next: (res) => {
          this.playlists = res.result;
        },
        error: (err) => {
          console.error('Lỗi load playlist:', err);
          alert('Tải danh sách playlist thất bại!');
        }
      });
    }
  }
}
