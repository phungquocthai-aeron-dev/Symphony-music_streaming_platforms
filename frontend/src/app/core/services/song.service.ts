import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseData } from '../../shared/models/ResponseData';
import { environment } from '../../../environments/environment';
import { SongDTO } from '../../shared/models/Song.dto';
import { SongUpdateDTO } from '../../shared/models/SongUpdate.dto';
import { RankingDTO } from '../../shared/models/Ranking.dto';
import { HttpParams } from '@angular/common/http';
import { CategoryDTO } from '../../shared/models/Category.dto';
import { AuthService } from './auth.service';
import { SearchDTO } from '../../shared/models/Search.dto';


@Injectable({
  providedIn: 'root'
})
export class SongService {
  private apiUrl = `${environment.apiUrl}song`;
  private token: string | null;

  constructor(
    private authService: AuthService,
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.token = authService.getToken()
  }

  getSongById(songId: number | string): Observable<ResponseData<SongDTO>> {
    const params = new HttpParams().set('id', songId.toString());
    return this.http.get<ResponseData<SongDTO>>(`${this.apiUrl}`, { params });
  }

  getAllSongs(): Observable<ResponseData<SongDTO[]>> {
    return this.http.get<ResponseData<SongDTO[]>>(`${this.apiUrl}/songs`);
  }

  createSong(formData: FormData): Observable<ResponseData<SongDTO>> {
    const headers = new HttpHeaders().set(
      "Authorization",
      "Bearer " + this.token
    );
    return this.http.post<ResponseData<SongDTO>>(`${this.apiUrl}/create`, formData, { headers });
  }

  deleteSong(songId: number): Observable<ResponseData<SongDTO>> {
    const params = new HttpParams().set('songId', songId.toString());
    return this.http.post<ResponseData<SongDTO>>(`${this.apiUrl}/delete`, {}, { params });
  }

  songUpdate(
    dto: SongUpdateDTO,
    lrcFile?: File,
    lyricFile?: File,
    songImgFile?: File
  ): Observable<ResponseData<SongDTO>> {
    const formData = new FormData();
  
    // Truyền DTO vào FormData dưới dạng chuỗi JSON
    formData.append('dto', JSON.stringify(dto));
  
    // Thêm các file vào nếu có
    if (lrcFile) {
      formData.append('lrcFile', lrcFile);
    }
    if (lyricFile) {
      formData.append('lyricFile', lyricFile);
    }
    if (songImgFile) {
      formData.append('songImgFile', songImgFile);
    }
  
    return this.http.post<ResponseData<SongDTO>>(`${this.apiUrl}/update`, formData);
  }

  getNewSongs(limit = 50): Observable<ResponseData<SongDTO[]>> {
    const params = new HttpParams().set('limit', limit.toString());

    return this.http.get<ResponseData<SongDTO[]>>(`${this.apiUrl}/newSongs`, { params });
  }

  getCategories(): Observable<ResponseData<CategoryDTO[]>> {
    return this.http.get<ResponseData<CategoryDTO[]>>(`${this.apiUrl}/categories`);
  }

  favoriteSong(songId: number): Observable<void> {
    const params = new HttpParams().set('id', songId.toString());
    const headers = new HttpHeaders().set(
      "Authorization",
      "Bearer " + this.token
    );
  
    return this.http.post<void>(`${this.apiUrl}/favorite`, null, { headers, params });
  }
  
  // unfavoriteSong(songId: number): Observable<void> {
  //   const params = new HttpParams().set('id', songId.toString());
  //   const headers = new HttpHeaders().set(
  //     "Authorization",
  //     "Bearer " + this.token
  //   );
  
  //   return this.http.post<void>(`${this.apiUrl}/unfavorite`, null, { headers, params });
  // }
  
  updateTotalListen(songId: number): Observable<void> {
    const params = new HttpParams().set('songId', songId.toString());
  
    return this.http.post<void>(`${this.apiUrl}/listenedSong`, {}, { params });
  }

  getSongsByCategory(categoryId: number): Observable<ResponseData<SongDTO[]>> {
    const params = new HttpParams().set('categoryId', categoryId.toString());

    return this.http.get<ResponseData<SongDTO[]>>(`${this.apiUrl}/category`, { params });
  }

  getSongsBySinger(singerId: number): Observable<ResponseData<SongDTO[]>> {
    const params = new HttpParams().set('id', singerId.toString());

    return this.http.get<ResponseData<SongDTO[]>>(`${this.apiUrl}/songsOfSinger`, { params });
  }

  getCurrentSongIdFromCookie(): string | null {
    // Kiểm tra xem đang chạy trên trình duyệt
    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }

    const cookies = document.cookie.split(';');
    for (const cookie of cookies) {
      const [name, value] = cookie.trim().split('=');
      if (name === 'currentSongId') {
        return value;
      }
    }
    return null;
  }

  setCurrentSongIdCookie(songId: string): void {
    // Kiểm tra xem đang chạy trên trình duyệt
    if (isPlatformBrowser(this.platformId)) {
      document.cookie = `currentSongId=${songId}; path=/; max-age=${60 * 60 * 24 * 30}`; // 30 ngày
    }
  }
  getCurrentSong(): Observable<ResponseData<SongDTO>> {
    const currentSongId = this.getCurrentSongIdFromCookie();
    
    if(currentSongId) {
      return this.getSongById(currentSongId);
    }
    return this.http.get<ResponseData<SongDTO>>(`${this.apiUrl}/new`);
  }


 getTopSong(limit: number = 10): Observable<ResponseData<RankingDTO>> {
   // Tạo params để truyền limit
   const params = new HttpParams().set('limit', limit.toString());
   
   // Gọi API ranking
   return this.http.get<ResponseData<RankingDTO>>(`${this.apiUrl}/ranking`, { params });
 }

 getRecommendedSongs(ids: number[], limit: number = 6): Observable<ResponseData<SongDTO[]>> {
  const params = new HttpParams()
    .set('ids', ids.join(',')) // Chuyển danh sách ID thành chuỗi
    .set('limit', limit.toString()); // Chuyển limit thành chuỗi
  
  return this.http.get<ResponseData<SongDTO[]>>(`${this.apiUrl}/recommend`, { params });
}

getRecentlyListenSongs(limit = 50): Observable<ResponseData<SongDTO[]>> {
  const params = new HttpParams().set('limit', limit.toString());
  return this.http.get<ResponseData<SongDTO[]>>(`${environment.apiUrl}user/recently`, { params });
}

getFavoriteSongs(): Observable<ResponseData<SongDTO[]>> {
  const headers = new HttpHeaders().set(
    "Authorization",
    "Bearer " + this.token
  );
  
  return this.http.get<ResponseData<SongDTO[]>>(`${environment.apiUrl}user/favorite`, { headers });
}

reportListenedSong(songId: number): Observable<number> {
  const params = new HttpParams().set('id', songId.toString());
  return this.http.post<number>(this.apiUrl + "/listenedSong", {}, { params });
}

userListened(songId: number): Observable<void> {
  const params = new HttpParams().set('id', songId.toString());
  return this.http.get<void>(`${environment.apiUrl}user/listened`, { params });
}

search(search: string): Observable<ResponseData<SearchDTO>> {
  const params = new HttpParams().set('s', search);
  return this.http.get<ResponseData<SearchDTO>>(`${environment.apiUrl}search`, { params });
}

}
