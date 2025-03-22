import { NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { SongDTO } from '../../shared/models/Song.dto';
import { AuthService } from '../../core/services/auth.service';
import { SongService } from '../../core/services/song.service';
import { ResponseData } from '../../shared/models/ResponseData';
import { CardComponent } from '../../shared/components/card/card.component';

@Component({
  selector: 'app-recent',
  imports: [NgIf, NgFor, CardComponent],
  templateUrl: './recent.component.html',
  styleUrl: './recent.component.css'
})
export class RecentComponent implements OnInit {
  songs:SongDTO[] = [];
  isLoggedIn: boolean = false;

  constructor(
    private authService: AuthService,
    private songService: SongService
  ) {}

  ngOnInit(): void {
      this.isLoggedIn = this.authService.isLoggedIn();
      if(this.isLoggedIn) this.loadData();
  }

  loadData() {
    this.songService.getRecentlyListenSongs().subscribe({
      next: (response: ResponseData<SongDTO[]>) => {
        this.songs = response.result;
        console.log("OK")
      },
      error: (error) => {
        console.error(error);
      }
    })
  }
}
