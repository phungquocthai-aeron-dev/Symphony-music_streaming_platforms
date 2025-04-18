import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Observable, of, BehaviorSubject, throwError, EMPTY } from 'rxjs';
import { map, catchError, tap, switchMap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { ResponseData } from '../../shared/models/ResponseData';
import { Authentication, AuthenticationResponse } from '../../shared/models/Authentication.dto';
import { UserRegistrationDTO } from '../../shared/models/UserRegistration.dto';
import { UserDTO } from '../../shared/models/User.dto';
import { HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private jwtHelper = new JwtHelperService();
  private authStatusSubject = new BehaviorSubject<boolean>(false);
  public authStatus$ = this.authStatusSubject.asObservable();
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId); // Kiểm tra có đang chạy trên trình duyệt không
    if (this.isBrowser) {
      this.authStatusSubject.next(this.isLoggedIn());
    }
  }

  // Lấy token từ localStorage chỉ khi chạy trên trình duyệt
  getToken(): string | null {
    if (!this.isBrowser) return null; // Nếu không phải trình duyệt, return null
    const userStr = localStorage.getItem('SYMPHONY_USER');
    if (!userStr) return null;
    
    try {
      const userInfo = JSON.parse(userStr);
      return userInfo.jwt || null;
    } catch (e) {
      this.logout();
      return null;
    }
  }

  // Cập nhật trạng thái xác thực
  private updateAuthStatus(): void {
    if (!this.isBrowser) return;
    this.authStatusSubject.next(this.isLoggedIn());
  }

  // Kiểm tra xem người dùng đã đăng nhập hay chưa dựa trên JWT
  isLoggedIn(): boolean {
    if (!this.isBrowser) return false;
    const token = this.getToken();
    return !!token && !this.jwtHelper.isTokenExpired(token);
  }

  // Lấy thông tin người dùng từ token
  getUserInfo(): any {
    if (!this.isBrowser) return null;
    const token = this.getToken();
    if (!token) return null;
    
    try {
      const decodedToken = this.jwtHelper.decodeToken(token);
      return {
        userId: decodedToken.sub,
        phone: decodedToken.phone,
        role: decodedToken.scope
      };
    } catch (e) {
      return null;
    }
  }

  deleteUser(userId: number): Observable<void> {
    const headers = new HttpHeaders().set(
      "Authorization",
      "Bearer " + this.getToken()
    );
  
    const params = new HttpParams().set('id', userId.toString());
  
    return this.http.post<void>(`${environment.apiUrl}users/delete`, null, { headers, params });
  }

  getUser(): Observable<ResponseData<UserDTO>> {
    const userInfo = this.getUserInfo();
    if(userInfo) {
      const params = new HttpParams().set('id', userInfo.userId);
    return this.http.get<ResponseData<UserDTO>>(environment.apiUrl + 'user', { params });
    }
    return EMPTY;
  }

  getUsers(): Observable<ResponseData<UserDTO[]>> {
    const headers = new HttpHeaders().set(
      "Authorization",
      "Bearer " + this.getToken()
    );
    return this.http.get<ResponseData<UserDTO[]>>(environment.apiUrl + 'user/users', { headers });
  }

  findUserByPhone(phone: string): Observable<ResponseData<UserDTO>> {
    const headers = new HttpHeaders().set(
      "Authorization",
      "Bearer " + this.getToken()
    );
  
    const params = new HttpParams().set('phone', phone);
  
    return this.http.get<ResponseData<UserDTO>>(
      environment.apiUrl + 'user/phone',
      { headers, params }
    );
  }  

  getUserById(id: number | string): Observable<ResponseData<UserDTO>> {
    const params = new HttpParams().set('id', id);
    return this.http.get<ResponseData<UserDTO>>(environment.apiUrl + 'user', { params });
  }

  getUserBySingerId(singerId: number): Observable<ResponseData<UserDTO>> {
    const params = new HttpParams().set('id', singerId);
    return this.http.get<ResponseData<UserDTO>>(environment.apiUrl + 'user/singer', { params });
  }

  updateUser(formData: FormData): Observable<ResponseData<UserDTO>> {
    const headers = new HttpHeaders().set(
      "Authorization",
      "Bearer " + this.getToken()
    );
    return this.http.post<ResponseData<UserDTO>>(environment.apiUrl + 'user/update', formData, { headers });
  }

  // Đăng ký
  register(userData: UserRegistrationDTO): Observable<ResponseData<UserDTO>> {
    return this.http.post<any>(environment.apiUrl + 'auth/register', userData).pipe(
      tap((response) => {
        console.log('Đăng ký thành công:', response);
      }),
      catchError((error) => {
        console.error('Lỗi đăng ký:', error);
        return throwError(() => new Error(error.error?.message || 'Đăng ký thất bại!'));
      })
    );
  }

  // Đăng nhập và lưu JWT
  // login(credentials: { phone: string; password: string }): Observable<ResponseData<AuthenticationResponse>> {
  //   return this.http.post<ResponseData<AuthenticationResponse>>(environment.apiUrl + 'auth/login', credentials).pipe(
  //     tap((response) => {
  //       if (response.result && response.result.authenticated) {
  //         const jwtInfo = {
  //           jwt: response.result.token
  //         };
  //         localStorage.setItem('SYMPHONY_USER', JSON.stringify(jwtInfo));
  //         this.router.navigate(['/']);
  //       } else {
  //         console.log("Xác thực không thành công");
  //       }
  //     })
  //   );
  // }

  login(auth: Authentication): Observable<ResponseData<AuthenticationResponse>> {  
    return this.http.post<ResponseData<AuthenticationResponse>>(
      environment.apiUrl + "auth/login",
      auth
    );
  }

  // Đăng xuất
  logout(): void {
    if (this.isBrowser) {
      localStorage.removeItem('SYMPHONY_USER');
    }
    this.updateAuthStatus();
    this.router.navigate(['/login']);
  }

  // Xác thực token với server
  validateTokenWithServer(): Observable<boolean> {
    const token = this.getToken();
    if (!token) return of(false);
    
    return this.http.post<any>(environment.apiUrl + 'valid-token', token, {
      headers: new HttpHeaders({
        'Content-Type': 'text/plain'
      })
    }).pipe(
      map(() => {
        this.updateAuthStatus();
        return true;
      }),
      catchError(() => {
        this.logout();
        return of(false);
      })
    );
  }

  // Kiểm tra xem token có sắp hết hạn không
  isTokenExpiringSoon(minutesThreshold: number = 10): boolean {
    if (!this.isBrowser) return true;
    const token = this.getToken();
    if (!token) return true;
    
    try {
      const expiration = this.jwtHelper.getTokenExpirationDate(token);
      if (!expiration) return true;
      
      const now = new Date();
      const timeUntilExpiry = expiration.getTime() - now.getTime();
      return timeUntilExpiry < (minutesThreshold * 60 * 1000);
    } catch (e) {
      return true;
    }
  }

  // Refresh token nếu sắp hết hạn
  refreshTokenIfNeeded(): Observable<boolean> {
    if (!this.isLoggedIn()) {
      return of(false);
    }
    
    if (this.isTokenExpiringSoon()) {
      return this.refreshToken();
    }
    
    return of(true);
  }

  // Refresh token
  private refreshToken(): Observable<boolean> {
    const token = this.getToken();
    if (!token) return of(false);
    
    return this.http.post<any>('/api/refresh-token', { token }).pipe(
      map(response => {
        if (response && response.result && response.result.token) {
          if (this.isBrowser) {
            const jwtInfo = { jwt: response.result.token };
            localStorage.setItem('SYMPHONY_USER', JSON.stringify(jwtInfo));
            this.updateAuthStatus();
          }
          return true;
        }
        return false;
      }),
      catchError(() => {
        this.logout();
        return of(false);
      })
    );
  }

  // Đảm bảo rằng người dùng đã đăng nhập trước khi thực hiện hành động quan trọng
  ensureAuthenticated(): Observable<boolean> {
    if (!this.isLoggedIn()) {
      this.router.navigate(['/login']);
      return of(false);
    }
    
    return this.refreshTokenIfNeeded().pipe(
      switchMap(refreshed => {
        if (!refreshed) return of(false);
        return this.validateTokenWithServer();
      })
    );
  }
  
exportUsers(): Observable<Blob> {
  const headers = new HttpHeaders().set(
    "Authorization",
    "Bearer " + this.getToken()
  );

  return this.http.post(`${environment.apiUrl}user/export`, null, {
    headers,
    responseType: 'blob' // rất quan trọng để nhận file nhị phân
  });
}

}
