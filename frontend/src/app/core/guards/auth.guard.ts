// import { Injectable, PLATFORM_ID, inject } from '@angular/core';
// import { CanActivate, Router } from '@angular/router';
// import { HttpClient } from '@angular/common/http';
// import { Observable, of } from 'rxjs';
// import { map, catchError } from 'rxjs/operators';
// import { isPlatformBrowser } from '@angular/common';
// import { environment } from '../../../environments/environment';

// @Injectable({
//   providedIn: 'root'
// })
// export class AuthGuard implements CanActivate {

//   private platformId = inject(PLATFORM_ID);

//   constructor(private router: Router, private http: HttpClient) {}

//   canActivate(): Observable<boolean> | boolean {
//     if (isPlatformBrowser(this.platformId)) {
//         const userInfo = localStorage.getItem('SYMPHONY_USER');
//         // console.log(userInfo)
//         if (!userInfo) {
//           return true;
//         }
    
//         try {
//           const parsedInfo = JSON.parse(userInfo);
//           if (parsedInfo?.jwt) {
//             console.log(userInfo)
//             this.checkTokenValidity(parsedInfo.jwt).pipe(
//                 map(result => {
//                   if (!result.valid) {
//                     this.router.navigate(['/home']);
//                     return false;
//                   }
//                   return false;
//                 })
//               );
            
//           }
//         } catch (error) {
//           console.error('Lỗi khi đọc JWT:', error);
//         }
//     }

//     return true;
//   }

//   private checkTokenValidity(token: string): Observable<{ valid: boolean }> {
  
//     return this.http.post<{ valid: boolean }>(environment.apiUrl + 'auth/valid-token', { token }, { observe: 'response' })
//       .pipe(
//         map(response => {
//             console.log(response)
//           if (response.status === 200) {
//             console.log("A")
//             return { valid: false };
//           }
//           return { valid: true };
//         }),
//         catchError(() => of({ valid: true }))
//       );
//   }
  
// }
