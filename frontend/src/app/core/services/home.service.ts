import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HomeDTO } from '../../shared/models/Home.dto';
import { ResponseData } from '../../shared/models/ResponseData';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class HomeService {
  private apiUrl = environment.apiUrl + 'home';

  constructor(private http: HttpClient) {}

  getData(): Observable<ResponseData<HomeDTO>> {
    return this.http.get<any>(this.apiUrl);
  }
}
