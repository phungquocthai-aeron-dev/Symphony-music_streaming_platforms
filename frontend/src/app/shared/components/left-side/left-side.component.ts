import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DataShareService } from '../../../core/services/dataShare.service';

@Component({
  selector: 'app-left-side',
  imports: [RouterModule],
  templateUrl: './left-side.component.html',
  styleUrl: './left-side.component.css'
})
export class LeftSideComponent implements OnInit {
  currentPage = "";

  constructor(private dataShareService: DataShareService) {}
  
  ngOnInit(): void {
    this.dataShareService.leftSideInfo.subscribe(data => {
      if (data) {
        this.currentPage = data;
      }
    });
  }

  checkCurrentPage(currentData: string): boolean {
    return this.currentPage === currentData;
  }
}
