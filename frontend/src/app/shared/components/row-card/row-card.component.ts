import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SongDTO } from '../../models/Song.dto';
import { NgFor, NgIf } from '@angular/common';
import { SongService } from '../../../core/services/song.service';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { TimeFormatPipe } from '../../pipes/time-format.pipe';
import { DataShareService } from '../../../core/services/dataShare.service';
import { TopSongDTO } from '../../models/TopSong.dto';


@Component({
  selector: 'app-row-card',
  imports: [NgIf, NgFor, RouterModule, TimeFormatPipe],
  templateUrl: './row-card.component.html',
  styleUrl: './row-card.component.css'
})
export class RowCardComponent {
  @Input() song!: SongDTO | TopSongDTO;
  @Input() isOwner: boolean = false;
  @Output() isEdit = new EventEmitter<boolean>();
  @Output() isDelete = new EventEmitter<boolean>();
  @Output() songSelected = new EventEmitter<SongDTO | TopSongDTO>();
  @Output() openModalEdit = new EventEmitter<void>();



  constructor(
    private songService: SongService,
    private authService: AuthService,
    private eventSource: DataShareService
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
}
