import { Component, OnDestroy, OnInit } from '@angular/core';
import { HomeDTO } from '../../shared/models/Home.dto';
import { SongDTO } from '../../shared/models/Song.dto';
import { TopSongDTO } from '../../shared/models/TopSong.dto';
import { ListeningStatsDTO } from '../../shared/models/ListeningStats.dto';
import { HomeService } from '../../core/services/home.service';
import { ResponseData } from '../../shared/models/ResponseData';
import { Subject, takeUntil } from 'rxjs';
import { NgFor } from '@angular/common';
import { CardComponent } from '../../shared/components/card/card.component';
import { NewCardComponent } from '../../shared/components/new-card/new-card.component';
import { DataShareService } from '../../core/services/dataShare.service';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  imports: [NgFor, CardComponent, NewCardComponent ]
})

export class HomeComponent implements OnInit, OnDestroy {
  data!: HomeDTO;
  loading = false;
  error: string | null = null;
  
  private destroy$ = new Subject<void>();

  hotHitSongs: SongDTO[] = [];
  vpopSongs: SongDTO[] = [];
  usUkSongs: SongDTO[] = [];
  remixSongs: SongDTO[] = [];
  lyricalSongs: SongDTO[] = [];
  newSongs: SongDTO[] = [];
  recentlyListenSongs: SongDTO[] = [];
  recommendedSongs: SongDTO[] = [];
  topSongs: TopSongDTO[] = [];
  topTrendingStats: ListeningStatsDTO[] = [];

  constructor(
    private homeService: HomeService,
    private dataShareService: DataShareService) {}

  ngOnInit(): void {
    this.fetchData();
    this.dataShareService.changeLeftSideInfo("Home");
    this.dataShareService.changeTitle("Trang chủ");
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  fetchData(): void {
    this.loading = true;
    this.error = null;
    
    this.homeService.getData()
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (response: ResponseData<HomeDTO>) => {
          if (response.code === 1000) {
            this.data = response.result as HomeDTO;
            this.mapHomeData(this.data);
          } else {
            this.error = (response.message) ? response.message : "";
          }
          this.loading = false;
        },
        error: (error) => {
          this.error = 'Failed to fetch home data';
          this.loading = false;
          console.error('Error loading home data:', error);
        }
      });

    }

    private mapHomeData(data: HomeDTO): void {
      this.hotHitSongs = data.hotHitSong;
      this.vpopSongs = data.vpopSongs;
      this.usUkSongs = data.usUkSongs;
      this.remixSongs = data.remixSongs;
      this.lyricalSongs = data.lyricalSongs;
      this.newSongs = data.newSongs;
      this.recentlyListenSongs = data.recentlyListen;
      this.recommendedSongs = data.recommend;
      this.topSongs = data.topSongs;
      this.topTrendingStats = data.topTrendingStats;
  }
}
