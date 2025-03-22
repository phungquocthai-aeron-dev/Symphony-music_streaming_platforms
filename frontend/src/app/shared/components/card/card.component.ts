import { Component, Input } from '@angular/core';
import { SongDTO } from '../../models/Song.dto';
import { NgFor, NgIf } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DataShareService } from '../../../core/services/dataShare.service';

@Component({
  selector: 'app-card',
  imports: [ NgFor, NgIf, RouterModule ],
  templateUrl: './card.component.html',
  styleUrl: './card.component.css'
})
export class CardComponent {
  @Input() song!: SongDTO;

  constructor(private eventSource: DataShareService) {}

  toggleSendSong() {
    this.eventSource.changeData(this.song);
  }
}
