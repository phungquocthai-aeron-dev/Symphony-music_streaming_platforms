import { Component, Input } from '@angular/core';
import { TopSongDTO } from '../../models/TopSong.dto';

@Component({
  selector: 'app-rank-card',
  imports: [],
  templateUrl: './rank-card.component.html',
  styleUrl: './rank-card.component.css'
})
export class RankCardComponent {
  @Input() song!:TopSongDTO;
}
