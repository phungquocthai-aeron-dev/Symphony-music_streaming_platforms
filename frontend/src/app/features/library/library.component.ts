import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { PlaylistDTO } from '../../shared/models/Playlist.dto';
import { PlaylistCardComponent } from '../../shared/components/playlist-card/playlist-card.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PlaylistService } from '../../core/services/playlist.service';
import { AuthService } from '../../core/services/auth.service';
import { DataShareService } from '../../core/services/dataShare.service';
import { SongDTO } from '../../shared/models/Song.dto';
import { RowCardComponent } from '../../shared/components/row-card/row-card.component';


@Component({
  selector: 'app-library',
  templateUrl: './library.component.html',
  styleUrl: './library.component.css',
  standalone: true,
  imports: [CommonModule, FormsModule, PlaylistCardComponent, RowCardComponent]
})
export class LibraryComponent implements OnInit {
  @ViewChild('closeButtonRef',  { static: false }) closeFormCreate!: ElementRef;
  
  playlists: PlaylistDTO[] = [];
  newPlaylistName: string = '';
  userId: number;
  songs: SongDTO[] = [];
  quantity = 0;
  playlist!: PlaylistDTO;

  constructor(
    private playlistService: PlaylistService,
    private authService: AuthService,
    private dataShareService: DataShareService
  ) {
    this.userId = this.authService.getUserInfo().userId;
  }

  ngOnInit(): void {
    this.dataShareService.changeLeftSideInfo("Library");
    this.dataShareService.changeTitle("Thư viện");
    this.loadPlaylists();
  }

  loadPlaylists(): void {
    this.playlistService.getPlaylistByUserId(this.userId).subscribe(response => {
      this.playlists = response.result || [];
    });
  }

  addPlaylist(): void {
    const trimmedName = this.newPlaylistName.trim();
    if (!trimmedName) return;

    this.playlistService.createPlaylist(this.userId, trimmedName).subscribe(response => {
      this.playlists.push(response.result);
      this.newPlaylistName = '';
      this.closeFormCreate.nativeElement.click();
    });
  }

  playlistSelect(playlist: PlaylistDTO) {
    this.playlist = playlist;
  }

  showSongs(songs: SongDTO[]) {
    this.songs = songs;
    this.quantity = songs.length;
  }

  removePlaylist(playlistId: number): void {
    this.playlists = this.playlists.filter(p => p.playlistId !== playlistId);
  }
  
  updatePlaylistName(event: { playlistId: number; newName: string }): void {
    const playlist = this.playlists.find(p => p.playlistId === event.playlistId);
    if (playlist) {
      playlist.playlistName = event.newName;
    }
  }  
  
}


