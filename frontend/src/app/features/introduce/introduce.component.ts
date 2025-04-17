import { Component } from '@angular/core';
import { DataShareService } from '../../core/services/dataShare.service';

@Component({
  selector: 'app-introduce',
  templateUrl: './introduce.component.html',
  styleUrl: './introduce.component.css'
})
export class IntroduceComponent {
  constructor(private dataShareService: DataShareService) {}

  ngOnInit(): void {
      this.dataShareService.changeTitle("Giới thiệu");
  }
}
