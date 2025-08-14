import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { SongDTO } from '../../models/Song.dto';
import { AuthService } from '../../../core/services/auth.service';
import { NgFor, NgIf } from '@angular/common';
import { SideItemComponent } from '../side-item/side-item.component';
import { PlaylistDTO } from '../../models/Playlist.dto';
import { PlaylistService } from '../../../core/services/playlist.service';

@Component({
  selector: 'app-right-side',
  imports: [NgFor, NgIf, SideItemComponent],
  templateUrl: './right-side.component.html',
  styleUrl: './right-side.component.css'
})
export class RightSideComponent implements OnInit, OnChanges {

  @Input() currentSong!: SongDTO;
  @Input() playlistSongs: SongDTO[] = [];
  @Input() recentSongs: SongDTO[] = [];
  @Input() isTurnOn: boolean = false;
  @Output() optionPlaylist = new EventEmitter<boolean>();
  @Input()playlists: PlaylistDTO[] = [];
  activeSongId: number | null = null;
  isOptionPlaylist = true;
  

  constructor(
    private authService: AuthService
  ) {}

  playSong(songId: number): void {
    this.activeSongId = songId;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['currentSong']) {
      if(this.currentSong) this.activeSongId = this.currentSong.song_id;
      
    }
  }

  ngOnInit(): void {
    if(this.currentSong) this.activeSongId = this.currentSong.song_id;
    // this.paramSubscription = this.dataShareService.currentData.subscribe(data => {
    //   if(data) this.currentSong = data;
    // });
  }

  toggleFavorite(songId: number): void {
    const song = this.playlistSongs.find((s) => s.song_id === songId);
    if (song) {
      song.favorite = !song.favorite;
    }
  }

  toggleOption() {
    this.isOptionPlaylist = !this.isOptionPlaylist;
    this.optionPlaylist.emit(this.isOptionPlaylist);
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  requiredLogin(): boolean {
    if(this.authService.isLoggedIn()) return true;
    else {
      alert("Vui lòng đăng nhập để trải nghiệm tính năng này!")
      return false;
    }
  }
}
