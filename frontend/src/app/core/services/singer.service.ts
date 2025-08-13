import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseData } from '../../shared/models/ResponseData';
import { environment } from '../../../environments/environment';
import { SingerDTO } from '../../shared/models/Singer.dto';
import { HttpParams } from '@angular/common/http';
import { AuthService } from './auth.service';


@Injectable({
  providedIn: 'root'
})
export class SingerService {
  private apiUrl = `${environment.apiUrl}singer`;
  private token: string | null;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {
    this.token = authService.getToken();

  }

  getSingerById(singerId: number | string): Observable<ResponseData<SingerDTO>> {
    const params = new HttpParams().set('id', singerId.toString());
    return this.http.get<ResponseData<SingerDTO>>(`${this.apiUrl}`, { params });
  }

  getSingersNotIn(list: SingerDTO[]): Observable<ResponseData<SingerDTO[]>> {
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

  deleteSinger(singerId: number): Observable<void> {
    const params = new HttpParams().set('id', singerId.toString());
    return this.http.post<void>(`${this.apiUrl}/delete`, {}, { params });
  }
  
  enable(singerId: number): Observable<void> {
    const params = new HttpParams().set('id', singerId.toString());
    return this.http.post<void>(`${this.apiUrl}/enable`, {}, { params });
  }

  exportSingers(): Observable<Blob> {
    const headers = new HttpHeaders().set(
      "Authorization",
      "Bearer " + this.token
    );
  
    return this.http.post(`${environment.apiUrl}singer/export`, null, {
      headers,
      responseType: 'blob' // rất quan trọng để nhận file nhị phân
    });
  }

    findUserByStageName(stageName: string): Observable<ResponseData<SingerDTO[]>> {
      const headers = new HttpHeaders().set(
        "Authorization",
        "Bearer " + this.token
      );
    
      const params = new HttpParams().set('stageName', stageName);
    
      return this.http.get<ResponseData<SingerDTO[]>>(
        environment.apiUrl + 'singer/stageName',
        { headers, params }
      );
    } 
    
}
