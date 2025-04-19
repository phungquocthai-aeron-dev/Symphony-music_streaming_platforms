import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseData } from '../../shared/models/ResponseData';
import { environment } from '../../../environments/environment';
import { PlaylistDTO } from '../../shared/models/Playlist.dto';

@Injectable({
  providedIn: 'root'
})
export class PlaylistService {
  private apiUrl = environment.apiUrl + 'playlist';

  constructor(private http: HttpClient) {}

  getPlaylistByUserId(userId: number | string): Observable<ResponseData<PlaylistDTO[]>> {
    return this.http.get<ResponseData<PlaylistDTO[]>>(`${this.apiUrl}/user`, {
      params: { userId }
    });
  }  

  createPlaylist(userId: number, playlistName: string): Observable<ResponseData<PlaylistDTO>> {
    const params = new HttpParams()
      .set('userId', userId)
      .set('playlistName', playlistName);
    return this.http.post<ResponseData<PlaylistDTO>>(`${this.apiUrl}/create`, null, { params });
  }
  

  deletePlaylist(playlistId: number): Observable<ResponseData<string>> {
    const params = new HttpParams().set('playlistId', playlistId);
    return this.http.delete<ResponseData<string>>(`${this.apiUrl}/delete`, { params });
  }

  addSongToPlaylist(playlistId: number, songId: number): Observable<ResponseData<string>> {
    const params = new HttpParams()
      .set('playlistId', playlistId)
      .set('songId', songId);
    return this.http.post<ResponseData<string>>(`${this.apiUrl}/add-song`, null, { params });
  }

  removeSongFromPlaylist(playlistId: number, songId: number): Observable<ResponseData<string>> {
    const params = new HttpParams()
      .set('playlistId', playlistId)
      .set('songId', songId);
    return this.http.delete<ResponseData<string>>(`${this.apiUrl}/remove-song`, { params });
  }

  updatePlaylist(playlistId: number, playlistName: string): Observable<any> {
    const params = new HttpParams()
      .set('playlistId', playlistId.toString())
      .set('playlistName', playlistName);

    return this.http.post(`${this.apiUrl}/update`, null, { params });
  }
}
