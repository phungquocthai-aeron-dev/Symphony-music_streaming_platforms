import { Component, OnInit } from '@angular/core';
import { SongService } from '../../core/services/song.service';
import { CategoryDTO } from '../../shared/models/Category.dto';
import { ResponseData } from '../../shared/models/ResponseData';
import { NgFor } from '@angular/common';
import { DataShareService } from '../../core/services/dataShare.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-topic',
  imports: [NgFor, RouterModule],
  templateUrl: './topic.component.html',
  styleUrl: './topic.component.css'
})
export class TopicComponent implements OnInit {
  categories:CategoryDTO[] = []

  constructor(
    private songService: SongService,
    private dataShareService: DataShareService) {}
  
  ngOnInit(): void {
    this.dataShareService.changeLeftSideInfo("Topic");
    this.dataShareService.changeTitle("Chủ đề");

    this.loadData();
  }

  loadData() {
      this.songService.getCategories().subscribe({
        next: (response: ResponseData<CategoryDTO[]>) => {
          if (response.code === 1000) {
            this.categories = response.result;
          }
        },
        error: (error) => {
          console.log(error)
        }
      })
  }
}
