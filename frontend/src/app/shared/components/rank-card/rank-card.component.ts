import { Component, Input } from '@angular/core';
import { RowCardComponent } from '../row-card/row-card.component';
import { TimeFormatPipe } from '../../pipes/time-format.pipe';
import { RouterModule } from '@angular/router';
import { NgClass, NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-rank-card',
  imports: [NgIf, NgFor, RouterModule, TimeFormatPipe, NgClass],
  templateUrl: './rank-card.component.html',
  styleUrl: './rank-card.component.css'
})
export class RankCardComponent extends RowCardComponent {
  @Input() _rank: number = 0;

  get rank(): string {
    return this._rank === 1 ? 'top1-ranking'
         : this._rank === 2 ? 'top2-ranking'
         : this._rank === 3 ? 'top3-ranking'
         : '';
  }
}
