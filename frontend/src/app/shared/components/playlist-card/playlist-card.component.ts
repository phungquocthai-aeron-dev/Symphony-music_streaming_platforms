import { Component, Input, OnInit } from '@angular/core';
import { PlaylistDTO } from '../../models/Playlist.dto';
import { SongDTO } from '../../models/Song.dto';
import { SongService } from '../../../core/services/song.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-playlist-card',
  templateUrl: './playlist-card.component.html',
  styleUrls: ['./playlist-card.component.css'],
  imports: [NgIf]
})
export class PlaylistCardComponent implements OnInit {
  @Input() playlist!: PlaylistDTO;
  songs: SongDTO[] = [];
  songImages: string[] = [];

  constructor(
    private songService: SongService
  ) {}

  ngOnInit(): void {
    if (this.playlist?.playlistId) {
      this.songService.getSongsByPlaylistId(this.playlist.playlistId).subscribe({
        next: (data) => {
          this.songs = data.result || [];
  
          // Lấy tối đa 4 bài hát cuối
          const latestSongs = this.songs.slice(-4);
  
          this.songImages = latestSongs.map(song => song.song_img)
        }
      });
    }
  }
}
