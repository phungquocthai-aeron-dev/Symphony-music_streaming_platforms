import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SongDTO } from '../../models/Song.dto';
import { NgFor, NgIf } from '@angular/common';
import { SongService } from '../../../core/services/song.service';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { TimeFormatPipe } from '../../pipes/time-format.pipe';
import { TopSongDTO } from '../../models/TopSong.dto';
import { PlaylistService } from '../../../core/services/playlist.service';
import { PlaylistDTO } from '../../models/Playlist.dto';
import { DataShareService } from '../../../core/services/dataShare.service';


@Component({
  selector: 'app-row-card',
  imports: [NgIf, NgFor, RouterModule, TimeFormatPipe],
  templateUrl: './row-card.component.html',
  styleUrl: './row-card.component.css'
})
export class RowCardComponent {
  @Input() song!: SongDTO | TopSongDTO;
  @Input() isLibrary = false;
  @Input() isOwner: boolean = false;
  @Output() isEdit = new EventEmitter<boolean>();
  @Output() isDelete = new EventEmitter<boolean>();
  @Output() songSelected = new EventEmitter<SongDTO | TopSongDTO>();
  @Output() openModalEdit = new EventEmitter<void>();


  @Input()userPlaylists: PlaylistDTO[] = [];

  selectedSong: SongDTO | TopSongDTO | null = null;

  constructor(
    private songService: SongService,
    private authService: AuthService,
    private eventSource: DataShareService,
    private playlistService: PlaylistService
  ){}

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

  favoriteSong(): boolean {
    return this.song.favorite;
  }

  onSelectSong() {
    this.songSelected.emit(this.song); 
  }

  onEdit() {
    this.isEdit.emit(true);
    this.openModalEdit.emit();

  }

  onDelete() {
    this.isDelete.emit(true);
  }

  toggleSendSong() {
    this.eventSource.changeData(this.song)
  }

loadUserPlaylists() {
  const userId = this.authService.getUserInfo().userId;
  if (!userId) {
    alert('Vui lòng đăng nhập!');
    return;
  }
}

onDeleteSongInPlaylist(song: SongDTO | TopSongDTO) {
  this.eventSource.currentPlaylist.subscribe(data => {
    if(data) {
      this.playlistService.removeSongFromPlaylist(data.playlistId, song.song_id).subscribe({
        next: (data) => {

        },
        error: (error) => {
          console.error(error);
        }
      })
    }
  })
}

openAddToPlaylist(song: SongDTO | TopSongDTO) {
  this.eventSource.changeSongPlaylist(song);
  this.loadUserPlaylists();
}

addSongToPlaylist(playlistId: number) {
  let song: any = null;

  const subscription = this.eventSource.currentSongToPlaylist.subscribe(data => {
    if (data) {
      song = data;
      this.playlistService.addSongToPlaylist(playlistId, song.song_id).subscribe({
        next: (data) => {
          alert(data.result);
          subscription.unsubscribe();
        },
        error: (err) => {
          alert('Thêm vào playlist thất bại!');
          subscription.unsubscribe(); 
        }
      });
    } else {
      subscription.unsubscribe();
    }
  });  
  this.eventSource.changeSongPlaylist(null);


}


}
