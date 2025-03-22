import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Title } from '@angular/platform-browser';  // Import Title service
import { DataShareService } from './core/services/dataShare.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  webName = 'Symphony';
  title = "";

  constructor(
    private dataShareService: DataShareService,
    private titleService: Title
  ) {}

  ngOnInit(): void {
    this.title = this.webName;
    this.titleService.setTitle(this.title);

    this.dataShareService.title.subscribe(data => {
      if (data) {
        this.title = `${this.webName} - ${data}`;
        this.titleService.setTitle(this.title);
      }
    });
  }
}
