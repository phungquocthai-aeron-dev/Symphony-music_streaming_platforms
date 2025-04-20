import { Component, OnInit } from '@angular/core';
import { SongDTO } from '../../shared/models/Song.dto';
import { SongService } from '../../core/services/song.service';
import { AuthService } from '../../core/services/auth.service';
import { NgFor, NgIf } from '@angular/common';
import { RowCardComponent } from '../../shared/components/row-card/row-card.component';
import { ResponseData } from '../../shared/models/ResponseData';
import { DataShareService } from '../../core/services/dataShare.service';
import { PlaylistService } from '../../core/services/playlist.service';
import { PlaylistDTO } from '../../shared/models/Playlist.dto';

@Component({
  selector: 'app-favorite',
  imports: [NgFor, NgIf, RowCardComponent],
  templateUrl: './favorite.component.html',
  styleUrl: './favorite.component.css'
})
export class FavoriteComponent implements OnInit {
  songs: SongDTO[] = [];
  playlists: PlaylistDTO[] = [];
  isLoggedIn = false;
  quantity = 0;

  constructor(
    private songService: SongService,
    private authService: AuthService,
    private dataShareService: DataShareService,
    private playlistService: PlaylistService
  ) {}

  ngOnInit(): void {
    this.dataShareService.changeLeftSideInfo("Favorite");
    this.dataShareService.changeTitle("Yêu thích");

    this.isLoggedIn = this.authService.isLoggedIn();
    if(this.isLoggedIn) this.loadData();
  }

  loadData() {
    this.songService.getFavoriteSongs().subscribe({
      next: (response: ResponseData<SongDTO[]>) => {
        this.songs = response.result;
        this.quantity = this.songs.length;
      },
      error: (error) => {
        console.error(error)
      }      
    })

    const user = this.authService.getUserInfo()
    if(user) {
      this.playlistService.getPlaylistByUserId(user.userId).subscribe({
        next: (res) => {
          this.playlists = res.result;
          console.log(this.playlists)
        },
        error: (err) => {
          console.error('Lỗi load playlist:', err);
        }
      });
    }
  }

}
