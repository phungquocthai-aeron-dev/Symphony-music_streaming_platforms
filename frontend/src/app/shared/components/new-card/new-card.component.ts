import { Component, Input } from '@angular/core';
import { SongDTO } from '../../models/Song.dto';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DataShareService } from '../../../core/services/dataShare.service';

@Component({
  selector: 'app-new-card',
  imports: [DatePipe, NgIf, NgFor, RouterModule],
  templateUrl: './new-card.component.html',
  styleUrl: './new-card.component.css'
})
export class NewCardComponent {
  @Input() song!: SongDTO;

  constructor(private eventSource: DataShareService) {}

  toggleSendSong() {
    this.eventSource.changeData(this.song);
  }
}
