import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import { SongDTO } from "../../shared/models/Song.dto";
import { SingerDTO } from "../../shared/models/Singer.dto";

@Injectable({
    providedIn: 'root'
  })
export class DataShareService {
    private dataSource = new BehaviorSubject<any>(null);
    currentData = this.dataSource.asObservable();
  
    private singerSource = new BehaviorSubject<any>(null);
    currentSinger = this.singerSource.asObservable();

    private titleSource = new BehaviorSubject<any>(null);
    title = this.titleSource.asObservable();

    private leftSideInfoSource = new BehaviorSubject<any>(null);
    leftSideInfo = this.leftSideInfoSource.asObservable();
    
    changeData(data: SongDTO) {
      this.dataSource.next(data);
    }

    changeSinger(data: SingerDTO) {
      this.singerSource.next(data);
    }

    changeTitle(data: string) {
      this.titleSource.next(data);
    }

    changeLeftSideInfo(data: string) {
      this.leftSideInfoSource.next(data);
    }
  }