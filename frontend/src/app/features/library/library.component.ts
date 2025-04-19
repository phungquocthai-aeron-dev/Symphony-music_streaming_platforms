import { Component, Input, OnInit } from '@angular/core';
import { PlaylistDTO } from '../../shared/models/Playlist.dto';
import { PlaylistCardComponent } from '../../shared/components/playlist-card/playlist-card.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PlaylistService } from '../../core/services/playlist.service';
import { AuthService } from '../../core/services/auth.service';
import { DataShareService } from '../../core/services/dataShare.service';


@Component({
  selector: 'app-library',
  templateUrl: './library.component.html',
  styleUrl: './library.component.css',
  standalone: true,
  imports: [CommonModule, FormsModule, PlaylistCardComponent]
})
export class LibraryComponent implements OnInit {
  playlists: PlaylistDTO[] = [];
  newPlaylistName: string = '';
  userId: string;

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
      console.log(response)
    });
  }

  addPlaylist(): void {
    const trimmedName = this.newPlaylistName.trim();
    if (!trimmedName) return;

    this.playlistService.createPlaylist(1, trimmedName).subscribe(response => {
      this.playlists.push(response.result);
      this.newPlaylistName = '';
    });
  }
}


