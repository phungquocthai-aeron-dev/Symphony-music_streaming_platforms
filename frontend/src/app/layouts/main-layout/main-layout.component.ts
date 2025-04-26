import { Component, OnInit } from '@angular/core';
import { HeaderComponent } from '../../shared/components/header/header.component';
import { FooterComponent } from '../../shared/components/footer/footer.component';
import { RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { SongDTO } from '../../shared/models/Song.dto';
import { SongService } from '../../core/services/song.service';
import { LeftSideComponent } from '../../shared/components/left-side/left-side.component';
import { RightSideComponent } from '../../shared/components/right-side/right-side.component';
import { AudioMenuComponent } from '../../shared/components/audio-menu/audio-menu.component';
import { UserDTO } from '../../shared/models/User.dto';
import { SingerDTO } from '../../shared/models/Singer.dto';
import { SingerService } from '../../core/services/singer.service';
import { DataShareService } from '../../core/services/dataShare.service';
import { Subscription } from 'rxjs';
import { ResponseData } from '../../shared/models/ResponseData';
import { LyricComponent } from "../../shared/components/lyric/lyric.component";

@Component({
  selector: 'app-main-layout',
  imports: [HeaderComponent, FooterComponent, RouterOutlet, LeftSideComponent, RightSideComponent, AudioMenuComponent, LyricComponent],
  templateUrl: './main-layout.component.html'
})

export class MainLayoutComponent implements OnInit {
  constructor(
    private authService: AuthService,
    private songService: SongService,
    private singerService: SingerService,
    private dataShareService: DataShareService
  ) {}

  currentSong!: SongDTO;
  recentSong!: SongDTO[];
  playlistSong!: SongDTO[];
  turnRightSide: boolean = false;
  turnLyric: boolean = false;
  user!: UserDTO;
  singer!: SingerDTO;
  private paramSubscription!: Subscription;
  isOptionPlaylist = true;
  isPlaying: boolean = false;
  songProgress: number = 0;

  ngOnInit(): void {
    this.paramSubscription = this.dataShareService.currentData.subscribe(data => {
      if(data) this.currentSong = data;
    });

    this.dataShareService.dataPlaylistSong.subscribe(data => {
      console.log(data)
      if(data) {
        if(data.length > 0) {
          this.playlistSong = [];
          this.playlistSong = data;
          this.currentSong = data[0];
          this.playlistSong = this.filteredPlaylist();
          this.playlistSong.unshift(this.currentSong);
          console.log(this.playlistSong)
        }
      }
    })

    this.songService.getCurrentSong().subscribe({
      next: (response) => {
        if (response.code === 1000) {
          this.currentSong = response.result;
          this.dataShareService.changeData(this.currentSong);

          console.log('Bài hát hiện tại:', this.currentSong);
        } else {
          console.warn('Không lấy được bài hát:', response.message);
        }
      },
      error: (err) => {
        this.authService.logout();
        console.error('Lỗi khi lấy bài hát:', err);
      }
    });

    this.songService.getNewSongs().subscribe({
      next: (response) => {
        if (response.code === 1000) {
          this.playlistSong = response.result;
          this.playlistSong = this.filteredPlaylist();
          if (this.currentSong) {
            this.playlistSong.unshift(this.currentSong);
          }
        } else {
          console.warn('Không lấy được bài hát:', response.message);
        }
      },
      error: (err) => {
        console.error('Lỗi khi lấy bài hát:', err);
      }
    })

    if(this.authService.isLoggedIn()) {
      this.songService.getRecentlyListenSongs().subscribe({
        next: (response: ResponseData<SongDTO[]>) => {
          if (response.code === 1000) {
            if(response.result) {
              this.recentSong = response.result;
              this.recentSong = this.filteredRecentSong();
            }

          if (this.currentSong) {
            this.recentSong.unshift(this.currentSong);
          }
          } else {
            console.warn('Không lấy được bài hát:', response.message);
          }
        },
        error: (error) => {
          console.error(error);
        }
      });

        this.authService.getUser().subscribe({
          next: (response) => {
            if (response.code === 1000) {
              this.user = response.result;
              console.log('Người dùng đang đăng nhập:', this.user);
              if(this.user.role === "SINGER") {
                this.singerService.getSingerByUserId(this.user.userId).subscribe({
                  next: (response) => {
                    if (response.code === 1000) {
                      this.singer = response.result;
                      this.dataShareService.changeSinger(this.singer);
                    } else {
                      console.warn('Không lấy được thông tin ca sĩ:', response.message);
                    }
                  },
                  error: (err) => {
                    console.error('Lỗi khi tìm ca sĩ:', err);
                  }
                });
              }
  
            } else {
              console.warn('Không lấy được thông tin người dùng:', response.message);
            }
          },
          error: (err) => {
            console.error('Lỗi khi tìm người dùng:', err);
          }
        })
      
    }

  }

  OptionPlaylist(value: boolean) {
    this.isOptionPlaylist = value;
  }

  OptionTurnRightSide(value: boolean): void {
    this.turnRightSide = value;
  }

  OptionTurnLyric(value: boolean): void {
    this.turnLyric = value;
  }

  OptionIsPlaying(value: boolean): void {
    this.isPlaying = value;
  }

  SongProgress(value: number): void {
    this.songProgress = value;
  }

  filteredPlaylist() {
    return this.playlistSong.filter(song => song.song_id !== this.currentSong?.song_id);
  }

  filteredRecentSong() {
    return this.recentSong.filter(song => song.song_id !== this.currentSong?.song_id);
  }

  ngOnDestroy() {
    // Hủy subscription khi component bị hủy để tránh rò rỉ bộ nhớ
    if (this.paramSubscription) {
      this.paramSubscription.unsubscribe();
    }
  }
  
}
