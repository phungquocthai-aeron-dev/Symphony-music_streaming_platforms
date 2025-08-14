import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseData } from '../../shared/models/ResponseData';
import { environment } from '../../../environments/environment';
import { NotificationDTO } from '../../shared/models/Notification.dto';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = environment.apiUrl + 'notification';

  constructor(private http: HttpClient) {}

  getUserNotifications(userId: number): Observable<NotificationDTO[]> {
    return this.http.get<NotificationDTO[]>(`${this.apiUrl}/user/${userId}`);
  }

  sendToUser(userId: number, sendId: number, message: string, type: string): Observable<ResponseData<NotificationDTO>> {
    const params = new HttpParams()
      .set('message', message)
      .set('sendId', sendId)
      .set('type', type);

    return this.http.post<ResponseData<NotificationDTO>>(
      `${this.apiUrl}/send-to-user/${userId}`,
      null,
      { params }
    );
  }

  sendToAll(message: string, sendId: number, type: string): Observable<ResponseData<NotificationDTO>> {
    const params = new HttpParams()
      .set('message', message)
      .set('sendId', sendId)
      .set('type', type);

    return this.http.post<ResponseData<NotificationDTO>>(
      `${this.apiUrl}/send-to-all`,
      null,
      { params }
    );
  }

  markAsRead(notificationId: number, userId: number): Observable<any> {
    const params = new HttpParams().set('userId', userId);
    return this.http.put(`${this.apiUrl}/${notificationId}/read`, null, { params });
  }
}
