import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
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
import { AlbumDTO } from '../../models/Album.dto';
import { AlbumService } from '../../../core/services/album.service';


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
  @Output() notify = new EventEmitter<{ title: string, content: string, isSuccess: boolean }>();

  @Input()userPlaylists: PlaylistDTO[] = [];
  @Input()singerAlbums: AlbumDTO[] = [];

  selectedSong: SongDTO | TopSongDTO | null = null;

  constructor(
    private songService: SongService,
    private authService: AuthService,
    private eventSource: DataShareService,
    private playlistService: PlaylistService,
    private albumService: AlbumService
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
          this.notify.emit({
            title: 'Xóa bài hát khỏi playlist',
            content: 'Bài hát đã được xóa khỏi playlist!',
            isSuccess: true
          });
        },
        error: (error) => {
          this.notify.emit({
            title: 'Xóa bài hát khỏi playlist',
            content: 'Xóa bài hát khỏi playlist thất bại!',
            isSuccess: false
          });
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
          this.notify.emit({
            title: 'Thêm bài hát vào playlist',
            content: 'Bài hát đã được thêm vào playlist!',
            isSuccess: true
          });
          subscription.unsubscribe();
        },
        error: (err) => {
          this.notify.emit({
            title: 'Thêm bài hát vào playlist',
            content: 'Thêm bài hát vào playlist thất bại!',
            isSuccess: true
          });
          subscription.unsubscribe(); 
        }
      });
    } else {
      subscription.unsubscribe();
    }
  });  
  this.eventSource.changeSongPlaylist(null);


}

openAddToAlbum(song: SongDTO | TopSongDTO) {
  console.log(this.singerAlbums)
  this.eventSource.changeSongAlbum(song);
  this.loadUserPlaylists();
}

addSongToAlbum(albumId: number) {
  let song: any = null;

  const subscription = this.eventSource.currentSongToAlbum.subscribe(data => {
    if (data) {
      song = data;
      this.albumService.addSongToAlbum(albumId, song.song_id).subscribe({
        next: (data) => {
          this.notify.emit({
            title: 'Thêm bài hát vào album',
            content: 'Bài hát đã được thêm vào album!',
            isSuccess: true
          });
          subscription.unsubscribe();
        },
        error: (err) => {
          this.notify.emit({
            title: 'Thêm bài hát vào album',
            content: 'Thêm bài hát vào album thất bại!',
            isSuccess: true
          });
        }
      });
    }
  });  
  this.eventSource.changeSongPlaylist(null);

}

deleteSongFromAlbum() {
  let song: any = null;
  this.eventSource.currentAlbum.subscribe({
    next: data => {
      const album:AlbumDTO = data;
      console.log(data)
      if(data) {

        this.eventSource.currentSongToAlbum.subscribe(data => {
          if (data) {
            song = data;
            this.albumService.removeSongFromAlbum(album.albumId, song.song_id).subscribe({
              next: (data) => {
                this.notify.emit({
                  title: 'Xóa bài hát khỏi album',
                  content: 'Bài hát đã được xoá khỏi album!',
                  isSuccess: true
                });
              },
              error: (err) => {
                this.notify.emit({
                  title: 'Xóa bài hát khỏi album',
                  content: 'Xóa bài hát khỏi album thất bại!',
                  isSuccess: false
                });
              }
            });
          }
        });  
        this.eventSource.changeSongPlaylist(null);
      

      }
    }
  })
  
}

}
