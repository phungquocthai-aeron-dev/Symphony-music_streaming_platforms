import { Component } from '@angular/core';
import { DataShareService } from '../../core/services/dataShare.service';

@Component({
  selector: 'app-clause',
  templateUrl: './clause.component.html',
  styleUrl: './clause.component.css'
})
export class ClauseComponent {
  constructor(private dataShareService: DataShareService) {}

  ngOnInit(): void {
      this.dataShareService.changeTitle("Điều khoản");
  }
}
