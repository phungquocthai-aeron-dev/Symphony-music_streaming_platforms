import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseData } from '../../shared/models/ResponseData';
import { environment } from '../../../environments/environment';
import { SongDTO } from '../../shared/models/Song.dto';

@Injectable({
  providedIn: 'root'
})
export class SongService {
  private apiUrl = environment.apiUrl + 'symphony/song/';

  constructor(private http: HttpClient) {}

  getSongById(id: number): Observable<ResponseData<SongDTO>> {
    return this.http.get<any>(`${this.apiUrl}${id}`);
  }
}
