import { Component, Input, EventEmitter, Output } from '@angular/core';
import { SongDTO } from '../../models/Song.dto';
import { AuthService } from '../../../core/services/auth.service';
import { RouterModule } from '@angular/router';
import { NgFor, NgIf } from '@angular/common';
import { SongService } from '../../../core/services/song.service';
import { DataShareService } from '../../../core/services/dataShare.service';

@Component({
  selector: 'app-side-item',
  templateUrl: './side-item.component.html',
  styleUrls: ['./side-item.component.css'],
  imports: [RouterModule, NgIf, NgFor]
})
export class SideItemComponent {
  @Input() song!: SongDTO;
  @Input() isActive: boolean = false;
  @Output() playSongEvent = new EventEmitter<number>();
  @Output() toggleFavoriteEvent = new EventEmitter<number>();

  constructor(
    private authService: AuthService,
    private songService: SongService,
    private eventSource: DataShareService
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

  haveData(): boolean {
    return this.song != null;
  }  

  favoriteSong(): boolean {
    return this.song.favorite;
  }

  isLoggedin(): boolean {
    return this.authService.isLoggedIn();
  }
}