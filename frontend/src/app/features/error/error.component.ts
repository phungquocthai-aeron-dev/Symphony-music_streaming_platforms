import { Component, OnInit } from '@angular/core';
import { DataShareService } from '../../core/services/dataShare.service';

@Component({
  selector: 'app-error',
  imports: [],
  templateUrl: './error.component.html',
  styleUrl: './error.component.css'
})
export class ErrorComponent implements OnInit {
  constructor(private dataShareService: DataShareService) {}

  ngOnInit(): void {
      this.dataShareService.changeTitle("Lỗi 404");
  }
}
