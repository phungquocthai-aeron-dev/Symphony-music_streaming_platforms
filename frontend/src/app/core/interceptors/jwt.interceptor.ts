import { HttpInterceptorFn } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID, inject } from '@angular/core';

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
  const platformId = inject(PLATFORM_ID);
  let token: string | null = null;

  // Kiểm tra xem có đang chạy trên trình duyệt không
  if (isPlatformBrowser(platformId)) {
    const userInfo = localStorage.getItem('SYMPHONY_USER');
    if (userInfo) {
      try {
        const parsedInfo = JSON.parse(userInfo);
        if (parsedInfo?.jwt) {
          token = parsedInfo.jwt;
        }
      } catch (error) {
        console.error('Error parsing JWT info:', error);
      }
    }
  }

  if (req.url.includes('/home') || req.url === '/') {
    const authReq = token 
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) 
      : req;
    return next(authReq);
  }

  // Kiểm tra xem request có nằm trong danh sách public không
  const isPublicRequest = (
    (req.method === 'GET' && PUBLIC_GET_ENDPOINTS.some(url => req.url.includes(url))) ||
    (req.method === 'POST' && PUBLIC_POST_ENDPOINTS.some(url => req.url.includes(url)))
  );

  // Nếu request là public, không thêm token
  const authReq = isPublicRequest
    ? req
    : req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });

  return next(authReq);
};
