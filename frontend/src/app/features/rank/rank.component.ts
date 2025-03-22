import { Component, OnInit } from '@angular/core';
import { ListeningStatsDTO } from '../../shared/models/ListeningStats.dto';
import { TopSongDTO } from '../../shared/models/TopSong.dto';
import { SongService } from '../../core/services/song.service';
import { DataShareService } from '../../core/services/dataShare.service';
import { ResponseData } from '../../shared/models/ResponseData';
import { RankingDTO } from '../../shared/models/Ranking.dto';
import { NgFor, NgIf } from '@angular/common';
import { RankCardComponent } from '../../shared/components/rank-card/rank-card.component';

@Component({
  selector: 'app-rank',
  imports: [NgFor, NgIf, RankCardComponent],
  templateUrl: './rank.component.html',
  styleUrl: './rank.component.css'
})
export class RankComponent implements OnInit {
  top3: ListeningStatsDTO[][] = [];
  topSongs: TopSongDTO[] = [];

  constructor(
    private songService: SongService,
    private dataShareService: DataShareService
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
      },
      error: (error) => {
        console.error(error);
      }
    });
  }
}
