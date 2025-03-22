import { NgFor, NgIf } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { NewCardComponent } from '../../shared/components/new-card/new-card.component';
import { SongDTO } from '../../shared/models/Song.dto';
import { SongService } from '../../core/services/song.service';
import { ResponseData } from '../../shared/models/ResponseData';
import { Subscription } from 'rxjs';
import { DataShareService } from '../../core/services/dataShare.service';

@Component({
  selector: 'app-release',
  imports: [NgFor, NgIf, NewCardComponent],
  templateUrl: './release.component.html',
  styleUrl: './release.component.css'
})
export class ReleaseComponent implements OnInit, OnDestroy {
  songs: SongDTO[] = [];
  private paramSubscription!: Subscription;
  
  constructor(
    private songService: SongService,
    private dataShareService: DataShareService
  ) {}

  ngOnInit(): void {
      this.dataShareService.changeLeftSideInfo("Release");
      this.dataShareService.changeTitle("Mới phát hành");

      this.loadData();
  }

  loadData() {
    this.paramSubscription = this.songService.getNewSongs().subscribe({
      next: (response: ResponseData<SongDTO[]>) => {
        this.songs = response.result;
      },
      error: (error) => {
        console.error(error)
      }
    });
  }

  ngOnDestroy(): void {
      this.paramSubscription.unsubscribe();
  }
}
