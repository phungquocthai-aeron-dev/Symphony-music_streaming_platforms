import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { PlaylistDTO } from '../../models/Playlist.dto';
import { SongDTO } from '../../models/Song.dto';
import { SongService } from '../../../core/services/song.service';
import { NgIf } from '@angular/common';
import { PlaylistService } from '../../../core/services/playlist.service';
import { FormsModule } from '@angular/forms';
import { DataShareService } from '../../../core/services/dataShare.service';

@Component({
  selector: 'app-playlist-card',
  templateUrl: './playlist-card.component.html',
  styleUrls: ['./playlist-card.component.css'],
  standalone: true,
  imports: [NgIf, FormsModule]
})
export class PlaylistCardComponent implements OnInit {
  @Input() playlist!: PlaylistDTO;

  @Output() playlistDetail = new EventEmitter<SongDTO[]>();
  @Output() playlistSelect = new EventEmitter<PlaylistDTO>();
  @Output() deleted = new EventEmitter<number>();
  @Output() renamed = new EventEmitter<{ playlistId: number; newName: string }>();

  songs: SongDTO[] = [];
  songImages: string[] = [];
  newPlaylistName: string = '';

  @ViewChild('deleteButtonRef',  { static: false }) closeFormDelete!: ElementRef;
  @ViewChild('renameButtonRef',  { static: false }) closeFormUpdate!: ElementRef;

  constructor(
    private songService: SongService,
    private playlistService: PlaylistService,
    private dataShareService: DataShareService
  ) {}

  ngOnInit(): void {
    if (this.playlist?.playlistId) {
      this.songService.getSongsByPlaylistId(this.playlist.playlistId).subscribe({
        next: (data) => {
          this.songs = data.result || [];
          const latestSongs = this.songs.slice(-4);
          this.songImages = latestSongs.map(song => song.song_img);
        }
      });
    }
  }

  selectPlaylist() {
    this.playlistDetail.emit(this.songs);
    this.playlistSelect.emit(this.playlist);
    this.dataShareService.changeCurrentPlaylist(this.playlist);
  }

  confirmDelete(): void {
    this.playlistService.deletePlaylist(this.playlist.playlistId).subscribe({
      next: () => {
        this.deleted.emit(this.playlist.playlistId);
        this.closeFormDelete.nativeElement.click();
      },
      error: err => console.error('Lỗi khi xóa playlist:', err)
    });
  }

  confirmRename(): void {
    const newName = this.newPlaylistName?.trim();
    if (!newName || newName === this.playlist.playlistName) return;

    this.playlistService.updatePlaylist(this.playlist.playlistId, newName).subscribe({
      next: () => {
        this.renamed.emit({ playlistId: this.playlist.playlistId, newName });
        this.closeFormUpdate.nativeElement.click();
      },
      error: err => console.error('Lỗi khi đổi tên playlist:', err)
    });
  }
}
