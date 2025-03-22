import { Component, OnInit } from '@angular/core';
import { SongService } from '../../core/services/song.service';
import { CategoryDTO } from '../../shared/models/Category.dto';
import { ResponseData } from '../../shared/models/ResponseData';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-topic',
  imports: [NgFor],
  templateUrl: './topic.component.html',
  styleUrl: './topic.component.css'
})
export class TopicComponent implements OnInit {
  categories:CategoryDTO[] = []

  constructor(private songService: SongService) {}
  
  ngOnInit(): void {
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
