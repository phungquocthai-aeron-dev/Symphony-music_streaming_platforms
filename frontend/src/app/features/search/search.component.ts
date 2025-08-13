import { Component, OnDestroy, OnInit } from '@angular/core';
import { SongService } from '../../core/services/song.service';
import { ActivatedRoute } from '@angular/router';
import { SearchDTO } from '../../shared/models/Search.dto';
import { ResponseData } from '../../shared/models/ResponseData';
import { SongDTO } from '../../shared/models/Song.dto';
import { SingerDTO } from '../../shared/models/Singer.dto';
import { NgFor, NgIf } from '@angular/common';
import { SingerCardComponent } from '../../shared/components/singer-card/singer-card.component';
import { CardComponent } from '../../shared/components/card/card.component';
import { DataShareService } from '../../core/services/dataShare.service';

@Component({
  selector: 'app-search',
  imports: [NgFor, NgIf, SingerCardComponent, CardComponent],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit, OnDestroy {
  searchQuery: string = '';
  songs!: SongDTO[];
  singers!: SingerDTO[];

  constructor(
    private route: ActivatedRoute,
    private songService: SongService,
    private dataShareService: DataShareService
  ) {}

  ngOnInit() {
    this.dataShareService.changeLeftSideInfo("Search");
    this.dataShareService.changeTitle("Tìm kiếm");

    this.route.queryParams.subscribe(params => {
    if (params['type'] === 'humming') {
      this.dataShareService.searchHummingResults.subscribe(results => {
        if (results && results.length > 0) {
          this.songs = results; 
          this.singers = [];
        }
      });
    } else {
      this.searchQuery = params['s'] || '';
      if (this.searchQuery) {
        this.performSearch();
      }
    }
  });
  }

  performSearch() {
    this.refreshData();
    console.log(this.searchQuery)

    this.songService.search(this.searchQuery).subscribe({
      next: (response: ResponseData<SearchDTO>) => {
        const result = response.result;
        this.songs = result.songs;
        this.singers = result.singers;

      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  refreshData() {
    this.singers = [];
    this.songs = [];
  }

  ngOnDestroy(): void {
      this.refreshData();
  }

}
