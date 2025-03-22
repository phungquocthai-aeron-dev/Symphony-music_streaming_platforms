import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseData } from '../../shared/models/ResponseData';
import { environment } from '../../../environments/environment';
import { SingerDTO } from '../../shared/models/Singer.dto';
import { HttpParams } from '@angular/common/http';


@Injectable({
  providedIn: 'root'
})
export class SingerService {
  private apiUrl = `${environment.apiUrl}singer`;

  constructor(
    private http: HttpClient,
  ) {}

  getSingerById(singerId: number | string): Observable<ResponseData<SingerDTO>> {
    const params = new HttpParams().set('id', singerId.toString());
    return this.http.get<ResponseData<SingerDTO>>(`${this.apiUrl}`, { params });
  }

  getSingersNotIn(list: SingerDTO[]): Observable<ResponseData<SingerDTO[]>> {
    console.log(list)
    return this.http.post<ResponseData<SingerDTO[]>>(
      `${this.apiUrl}/exclude`, 
      list);  
  }

  getSingerByUserId(userId: number | string): Observable<ResponseData<SingerDTO>> {
    const params = new HttpParams().set('id', userId.toString());
    return this.http.get<ResponseData<SingerDTO>>(`${this.apiUrl}/user`, { params });
  }

  getAllSingers(): Observable<ResponseData<SingerDTO[]>> {
    return this.http.get<ResponseData<SingerDTO[]>>(`${this.apiUrl}/singers`);
  }

  createSinger(formData: FormData): Observable<ResponseData<SingerDTO>> {
    return this.http.post<ResponseData<SingerDTO>>(`${this.apiUrl}/create`, formData);
  }

  deleteSinger(singerId: number): Observable<ResponseData<SingerDTO>> {
    const params = new HttpParams().set('singerId', singerId.toString());
    return this.http.post<ResponseData<SingerDTO>>(`${this.apiUrl}/delete`, {}, { params });
  }

}
