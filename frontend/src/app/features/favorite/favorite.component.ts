import { Component, OnInit } from '@angular/core';
import { SongDTO } from '../../shared/models/Song.dto';
import { SongService } from '../../core/services/song.service';
import { AuthService } from '../../core/services/auth.service';
import { NgFor, NgIf } from '@angular/common';
import { RowCardComponent } from '../../shared/components/row-card/row-card.component';
import { ResponseData } from '../../shared/models/ResponseData';
import { DataShareService } from '../../core/services/dataShare.service';

@Component({
  selector: 'app-favorite',
  imports: [NgFor, NgIf, RowCardComponent],
  templateUrl: './favorite.component.html',
  styleUrl: './favorite.component.css'
})
export class FavoriteComponent implements OnInit {
  songs: SongDTO[] = [];
  isLoggedIn = false;
  quantity = 0;

  constructor(
    private songService: SongService,
    private authService: AuthService,
    private dataShareService: DataShareService
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
  }

}
