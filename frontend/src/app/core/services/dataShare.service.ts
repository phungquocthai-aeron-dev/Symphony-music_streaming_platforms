import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import { SongDTO } from "../../shared/models/Song.dto";
import { SingerDTO } from "../../shared/models/Singer.dto";
import { TopSongDTO } from "../../shared/models/TopSong.dto";
import { PlaylistDTO } from "../../shared/models/Playlist.dto";
import { AlbumDTO } from "../../shared/models/Album.dto";

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

    private dataAddSongPlaylist = new BehaviorSubject<any>(null);
    currentSongToPlaylist = this.dataAddSongPlaylist.asObservable();

    private currentPlaylistSource = new BehaviorSubject<any>(null);
    currentPlaylist = this.currentPlaylistSource.asObservable();

    private dataAddSongAlbum = new BehaviorSubject<any>(null);
    currentSongToAlbum = this.dataAddSongAlbum.asObservable();

    private currentAlbumSource = new BehaviorSubject<any>(null);
    currentAlbum = this.currentAlbumSource.asObservable();

    changeData(data: SongDTO | TopSongDTO) {
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

    changeSongPlaylist(data: SongDTO | TopSongDTO | null) {
      this.dataAddSongPlaylist.next(data);
    }

    changeCurrentPlaylist(data: PlaylistDTO) {
      this.currentPlaylistSource.next(data);
    }

    changeSongAlbum(data: SongDTO | TopSongDTO | null) {
      this.dataAddSongAlbum.next(data);
    }

    changeCurrentAlbum(data: AlbumDTO) {
      this.currentAlbumSource.next(data);
    }
  }