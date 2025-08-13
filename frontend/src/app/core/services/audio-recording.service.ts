import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { SongDTO } from '../../shared/models/Song.dto';

@Injectable({
  providedIn: 'root'
})
export class AudioRecordingService {
  private mediaRecorder: MediaRecorder | null = null;
  private chunks: Blob[] = [];
  private isRecordingSubject = new BehaviorSubject<boolean>(false);
  
  public isRecording$ = this.isRecordingSubject.asObservable();

  constructor(private http: HttpClient) {}

  // 1. Start Recording
  async startRecording(): Promise<MediaStream> {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({
      audio: {
        sampleRate: 44000,      // Phù hợp với AI model
        channelCount: 1,        // Mono
        echoCancellation: true,
        noiseSuppression: true,
        autoGainControl: true  // Giữ nguyên âm lượng gốc
      }
    });

    this.chunks = [];

    // Danh sách các MIME type phổ biến mà MediaRecorder có thể hỗ trợ
    const supportedMimeTypes = [
      'audio/webm;codecs=opus',
      'audio/webm',
      'audio/ogg;codecs=opus',
      'audio/ogg',
      'audio/mp4'
    ];

    // Chọn MIME type đầu tiên mà trình duyệt hỗ trợ
    const mimeType = supportedMimeTypes.find(type => MediaRecorder.isTypeSupported(type));

    if (!mimeType) {
      throw new Error('Trình duyệt không hỗ trợ định dạng ghi âm nào phù hợp.');
    }

    this.mediaRecorder = new MediaRecorder(stream, { mimeType });

    this.mediaRecorder.ondataavailable = (event) => {
      if (event.data.size > 0) {
        this.chunks.push(event.data);
      }
    };

    this.mediaRecorder.start();
    this.isRecordingSubject.next(true);

    return stream;

  } catch (error) {
    console.error('Error starting recording:', error);
    throw new Error('Không thể truy cập microphone. Vui lòng kiểm tra lại quyền truy cập hoặc thiết bị.');
  }
}

  // 2. Stop Recording
  async stopRecording(): Promise<Blob> {
    return new Promise((resolve, reject) => {
      if (!this.mediaRecorder) {
        reject(new Error('No active recording'));
        return;
      }

      this.mediaRecorder.onstop = () => {
        const audioBlob = new Blob(this.chunks, { type: 'audio/wav' });
        this.isRecordingSubject.next(false);
        resolve(audioBlob);
      };

      this.mediaRecorder.stop();
      
      // Stop all tracks to release microphone
      if (this.mediaRecorder.stream) {
        this.mediaRecorder.stream.getTracks().forEach(track => track.stop());
      }
    });
  }

  // 3. Search by Humming - GỬI ĐẾN SPRINGBOOT
  searchByHumming(file: File): Observable<SongDTO[]> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<SongDTO[]>('http://localhost:8080/symphony/search-humming', formData);
  }

  // 4. Upload File Search
  searchByFile(file: File): Observable<SongDTO[]> {
    return this.searchByHumming(file);
  }

  // 5. Check microphone permission
  async checkMicrophonePermission(): Promise<boolean> {
    try {
      const result = await navigator.permissions.query({ name: 'microphone' as PermissionName });
      return result.state === 'granted';
    } catch (error) {
      console.warn('Permission API not supported:', error);
      return false;
    }
  }

  // 6. Get supported MIME types
  getSupportedMimeTypes(): string[] {
    const types = ['audio/wav', 'audio/mp3', 'audio/mp4', 'audio/ogg'];
    return types.filter(type => MediaRecorder.isTypeSupported(type));
  }
}