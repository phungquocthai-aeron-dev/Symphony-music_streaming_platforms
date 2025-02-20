import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

const PUBLIC_GET_ENDPOINTS = [
  '/', '/home', '/singer/', '/song/',
  '/auth/register', '/auth/login',
  '/music/normal/', '/lyric/', '/lrc/', '/images/',
  '/ranking', '/newSongs', '/category', '/recentlyListen', '/favorite'
];

const PUBLIC_POST_ENDPOINTS = [
  '/auth/login', '/auth/register', '/auth/logout', '/auth/refresh'
];

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService)

  const token = authService.getToken()

  if (!token) {
    return next(req); // Nếu không có token, bỏ qua interceptor
  }

  if ((req.url == 'home' || req.url === '/') && !authService.isLoggedIn()) {
    return next(req);
  }

  // Kiểm tra xem URL có nằm trong danh sách endpoint công khai không
  const isPublicEndpoint = [...PUBLIC_GET_ENDPOINTS, ...PUBLIC_POST_ENDPOINTS].some(endpoint =>
    req.url.includes(endpoint)
  );

  if (isPublicEndpoint) {
    return next(req); // Không thêm token cho các endpoint công khai
  }

  // Nếu không phải endpoint công khai, thêm Authorization header
  const clonedReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(clonedReq);
};
