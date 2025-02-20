import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseData } from '../../shared/models/ResponseData';
import { environment } from '../../../environments/environment';
import { Authentication, AuthenticationResponse } from '../../shared/models/Authentication.dto';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private apiUrl = environment.apiUrl + 'auth/';

  constructor(private http: HttpClient) {}

  authenticate(authentication: Authentication): Observable<ResponseData<AuthenticationResponse>> {
    return this.http.post<any>(this.apiUrl + "login", authentication);
  }
}
