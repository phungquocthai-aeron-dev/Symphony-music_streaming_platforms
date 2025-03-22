import { Component, OnDestroy, OnInit } from '@angular/core';
import { SongDTO } from '../../shared/models/Song.dto';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { SongService } from '../../core/services/song.service';
import { ResponseData } from '../../shared/models/ResponseData';
import { DatePipe, DecimalPipe, NgFor, NgIf } from '@angular/common';
import { CardComponent } from '../../shared/components/card/card.component';
import { HttpClient } from '@angular/common/http';
import { DataShareService } from '../../core/services/dataShare.service';
import { Subscription } from 'rxjs';
import { response } from 'express';
import { AuthService } from '../../core/services/auth.service';


@Component({
  selector: 'app-song',
  imports: [NgIf, NgFor, RouterModule, CardComponent, DatePipe, DecimalPipe],
  templateUrl: './song.component.html',
  styleUrl: './song.component.css'
})
export class SongComponent implements OnInit, OnDestroy {
  song!: SongDTO;
  songId!: number | string | null;
  songRecommend: SongDTO[] = [];
  lyricContent: string = '';
  lyricLines: string[] = [];
  isLoadingLyric: boolean = false;
  isLyricExpanded: boolean = false;

  private paramSubscription!: Subscription;


  constructor(
    private route: ActivatedRoute,
    private songService: SongService,
    private http: HttpClient,
    private eventSource: DataShareService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.paramSubscription = this.route.paramMap.subscribe(params => {
      this.songId = params.get('id');
      if(this.songId) {
        this.loadSongData(this.songId);
      }
    });
  }

  loadSongData(songId: string | number) {
    this.songService.getSongById(songId).subscribe({
      next: (response: ResponseData<SongDTO>) => {
        this.song = response.result;
        console.log("AAAA")
        console.log(this.song.favorite)
        // Tải file lời bài hát nếu có
        if (this.song.lyric) {
          const lyricPath = 'http://localhost:8080/symphony' + this.song.lyric;
          this.loadLyricFile(lyricPath);
        }
        
        const categoryIds: number[] = this.song.categories.map(category => category.category_id);
        
        this.songService.getRecommendedSongs(categoryIds).subscribe({
          next: (response: ResponseData<SongDTO[]>) => {
            if (response.code === 1000) {
              this.songRecommend = response.result;
            }
          },
          error: (error) => {
            console.log(error)
          }
        });
      },
      error: (error) => {
        console.log(error)
      }
    });
  }


  loadLyricFile(lyricPath: string) {
    this.isLoadingLyric = true;
    this.http.get(lyricPath, { responseType: 'text' })
      .subscribe({
        next: (data) => {
          this.lyricContent = data;
          // Tách nội dung thành các dòng
          this.lyricLines = data.split('\n').filter(line => line.trim() !== '')
          this.isLoadingLyric = false;
        },
        error: (error) => {
          console.error('Không thể tải file lời bài hát:', error);
          this.lyricContent = 'Không thể tải lời bài hát.';
          this.lyricLines = [];
          this.isLoadingLyric = false;
        }
      });
  }

  toggleLyricExpansion() {
    this.isLyricExpanded = !this.isLyricExpanded;
  }

  getExpandButtonText(): string {
    return this.isLyricExpanded ? 'Ẩn bớt' : '...Xem thêm';
  }

  toggleSendSong() {
    this.eventSource.changeData(this.song)
  }

  toggleFavorite() {
    if(!this.authService.isLoggedIn()) {
      alert("Vui lòng đăng nhập để thực hiện chức năng này!")
    }
    else {
      this.songService.favoriteSong(this.song.song_id).subscribe({
        next: (response) => {
          this.song.favorite = !this.song.favorite;
        }, 
        error: (error) => {
          console.error(error)
        }
      })
    }
  }

  ngOnDestroy() {
    // Hủy subscription khi component bị hủy để tránh rò rỉ bộ nhớ
    if (this.paramSubscription) {
      this.paramSubscription.unsubscribe();
    }
  }
}