import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseData } from '../../shared/models/ResponseData';
import { environment } from '../../../environments/environment';

export interface Vip {
  vip_id: number;
  vip_title: string;
  description: string;
  duration_days: number;
  price: number;
}

@Injectable({
  providedIn: 'root'
})
export class VipService {

  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAllVipPackages(): Observable<ResponseData<Vip[]>> {
    return this.http.get<ResponseData<Vip[]>>(`${this.baseUrl}vip`);
  }
}
