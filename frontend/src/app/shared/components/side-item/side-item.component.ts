import { Component, Input, EventEmitter, Output, ElementRef, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
import { SongDTO } from '../../models/Song.dto';
import { AuthService } from '../../../core/services/auth.service';
import { RouterModule } from '@angular/router';
import { NgFor, NgIf } from '@angular/common';
import { SongService } from '../../../core/services/song.service';
import { DataShareService } from '../../../core/services/dataShare.service';
import { PlaylistService } from '../../../core/services/playlist.service';
import { PlaylistDTO } from '../../models/Playlist.dto';

@Component({
  selector: 'app-side-item',
  templateUrl: './side-item.component.html',
  styleUrls: ['./side-item.component.css'],
  imports: [RouterModule, NgIf, NgFor]
})
export class SideItemComponent implements OnChanges {
  @Input() song!: SongDTO;
  @Input() isActive: boolean = false;
  @Output() playSongEvent = new EventEmitter<number>();
  @Output() toggleFavoriteEvent = new EventEmitter<number>();
  @ViewChild('item') itemElement!: ElementRef;
  @Output() notify = new EventEmitter<{ title: string, content: string, isSuccess: boolean }>();
  @Input()userPlaylists: PlaylistDTO[] = [];
  

  constructor(
    private authService: AuthService,
    private songService: SongService,
    private eventSource: DataShareService,
    private playlistService: PlaylistService
  ) {}

  playSong(): void {
    this.playSongEvent.emit(this.song.song_id);
    this.eventSource.changeData(this.song);
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

   ngOnChanges(changes: SimpleChanges) {
      if (changes['isActive']) {
        this.scrollToCenter();
      }
    }

  scrollToCenter() {
    if (this.itemElement && this.isActive) {
      this.itemElement.nativeElement.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      });
    }
  }

  haveData(): boolean {
    return this.song != null;
  }  

  favoriteSong(): boolean {
    return this.song.favorite;
  }

  isLoggedin(): boolean {
    return this.authService.isLoggedIn();
  }

  loadUserPlaylists() {
  const userId = this.authService.getUserInfo().userId;
  if (!userId) {
    alert('Vui lòng đăng nhập!');
    return;
  }
}

  openAddToPlaylist(song: SongDTO) {
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
}