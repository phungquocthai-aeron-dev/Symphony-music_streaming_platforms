import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AlbumDTO } from '../../shared/models/Album.dto';
import { ResponseData } from '../../shared/models/ResponseData';

@Injectable({
  providedIn: 'root'
})
export class AlbumService {
  private apiUrl = environment.apiUrl + 'album';

  constructor(private http: HttpClient) {}

  getAlbumsBySingerId(singerId: number | string): Observable<ResponseData<AlbumDTO[]>> {
    const params = new HttpParams().set('singerId', singerId);
    return this.http.get<ResponseData<AlbumDTO[]>>(`${this.apiUrl}/singer`, { params });
  }

  createAlbum(
    singerId: number,
    albumName: string,
    imgFile: File
  ): Observable<ResponseData<AlbumDTO>> {
    const formData = new FormData();
    formData.append('singerId', singerId.toString());
    formData.append('albumName', albumName);
    formData.append('imgFile', imgFile);
  
    return this.http.post<ResponseData<AlbumDTO>>(`${this.apiUrl}/create`, formData);
  }
  
  updateAlbum(albumId: number, albumName: string, imgFile: File | null): Observable<ResponseData<string>> {
    const formData = new FormData();
    formData.append('albumId', albumId.toString());
    formData.append('albumName', albumName);
    
    if (imgFile) {
      formData.append('imgFile', imgFile);
    }
  
    return this.http.post<ResponseData<string>>(`${this.apiUrl}/update`, formData);
  }  

  deleteAlbum(albumId: number): Observable<ResponseData<string>> {
    const params = new HttpParams().set('albumId', albumId);
    return this.http.delete<ResponseData<string>>(`${this.apiUrl}/delete`, { params });
  }

  addSongToAlbum(albumId: number, songId: number): Observable<ResponseData<string>> {
    const params = new HttpParams()
      .set('albumId', albumId)
      .set('songId', songId);

    return this.http.post<ResponseData<string>>(`${this.apiUrl}/add-song`, null, { params });
  }

  removeSongFromAlbum(albumId: number, songId: number): Observable<ResponseData<string>> {
    const params = new HttpParams()
      .set('albumId', albumId)
      .set('songId', songId);

    return this.http.delete<ResponseData<string>>(`${this.apiUrl}/remove-song`, { params });
  }
}
