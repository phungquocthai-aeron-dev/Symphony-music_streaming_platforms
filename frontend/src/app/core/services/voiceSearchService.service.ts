import { Injectable, NgZone } from '@angular/core';
import { Observable, Subject } from 'rxjs';

declare var webkitSpeechRecognition: any;

@Injectable({
  providedIn: 'root'
})
export class VoiceSearchService {
  private recognition: any;
  private isListening = false;
  private transcriptSubject = new Subject<string>();

  constructor(private ngZone: NgZone) {
    this.initSpeechRecognition();
  }

  private initSpeechRecognition(): void {
    if (typeof window !== 'undefined') {
      const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;

      if (!SpeechRecognition) {
        console.error('Trình duyệt không hỗ trợ Web Speech API');
        return;
      }

      this.recognition = new SpeechRecognition();
      this.recognition.lang = 'vi-VN';
      this.recognition.continuous = false;
      this.recognition.interimResults = true;

      this.recognition.onresult = (event: any) => {
        const transcript = event.results[0][0].transcript;
        this.ngZone.run(() => {
          this.transcriptSubject.next(transcript);
        });
      };

      this.recognition.onend = () => {
        this.isListening = false;
        this.transcriptSubject.complete();
      };
    }
  }

  public startListening(): Observable<string> {
    this.transcriptSubject = new Subject<string>();
    
    if (!this.recognition) {
      console.error('Speech recognition not available');
      this.transcriptSubject.error('Speech recognition not available');
      return this.transcriptSubject.asObservable();
    }

    if (!this.isListening) {
      this.isListening = true;
      this.recognition.start();
      console.warn('Voice search is listening...');
    }

    return this.transcriptSubject.asObservable();
  }

  public stopListening(): void {
    if (this.isListening) {
      this.isListening = false;
      this.recognition.stop();
      this.transcriptSubject.complete();
    }
  }
}