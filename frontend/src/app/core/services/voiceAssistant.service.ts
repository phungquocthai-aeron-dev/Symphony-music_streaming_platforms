import { Injectable, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

declare var webkitSpeechRecognition: any;

@Injectable({
  providedIn: 'root'
})
export class VoiceAssistantService {
  private recognition: any;
  private isListening = false;
  private isProcessingCommand = false;
  
  // Observable to track assistant state
  private assistantStateSubject = new BehaviorSubject<string>('idle'); // idle, listening, processing
  public assistantState$ = this.assistantStateSubject.asObservable();

  // Trigger phrases
  private triggerPhrases = ['hey symphony', 'xin chào', 'hey symphony', 'hello symphony'];

  constructor(
    private ngZone: NgZone,
    private router: Router
  ) {
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
      this.recognition.interimResults = false;

      this.recognition.onresult = (event: any) => {
        const transcript = event.results[0][0].transcript.toLowerCase();
        
        this.ngZone.run(() => {
          if (this.isProcessingCommand) {
            this.processCommand(transcript);
          } else {
            // Check if the trigger phrase was spoken
            if (this.triggerPhrases.some(phrase => transcript.includes(phrase))) {
              console.warn('Trigger phrase detected: ' + transcript);
              this.startCommandMode();
            }
          }
        });
      };

      this.recognition.onend = () => {
        if (this.isListening) {
          this.recognition.start();
        }
        
        if (this.isProcessingCommand) {
          this.isProcessingCommand = false;
          this.assistantStateSubject.next('idle');
        }
      };
    }
  }

  public startListening(): void {
    if (!this.recognition) {
      console.error('Speech recognition not available');
      return;
    }

    if (!this.isListening) {
      this.isListening = true;
      this.assistantStateSubject.next('listening');
      this.recognition.start();
      console.warn('Voice assistant is listening for trigger phrase');
    }
  }

  public stopListening(): void {
    if (this.isListening) {
      this.isListening = false;
      this.isProcessingCommand = false;
      this.assistantStateSubject.next('idle');
      this.recognition.stop();
      console.warn('Voice assistant stopped listening');
    }
  }

  private startCommandMode(): void {
    this.isProcessingCommand = true;
    this.assistantStateSubject.next('processing');
    
    // Stop current recognition instance to prepare for command
    this.recognition.stop();
    
    // Prompt user for command
    console.warn('Đang lắng nghe lệnh của bạn...');
    
    // After a short delay, start listening for the actual command
    setTimeout(() => {
      this.recognition.start();
    }, 500);
  }

  private processCommand(command: string): void {
    console.warn('Xử lý yêu cầu: ' + command);
    
    // Process different types of commands
    if (command.includes('chuyển bài') || command.includes('bài tiếp theo') || command.includes('next')) {
      console.warn('Yêu cầu: Chuyển sang bài tiếp theo');
      // Implementation would connect to your music service
    } 
    else if (command.includes('quay lại') || command.includes('bài trước') || command.includes('previous')) {
      console.warn('Yêu cầu: Quay lại bài trước');
      // Implementation would connect to your music service
    }
    else if (command.includes('random') || command.includes('ngẫu nhiên')) {
      console.warn('Yêu cầu: Phát nhạc ngẫu nhiên');
      // Implementation would connect to your music service
    }
    else if (command.includes('tạm dừng') || command.includes('pause')) {
      console.warn('Yêu cầu: Tạm dừng phát nhạc');
      // Implementation would connect to your music service
    }
    else if (command.includes('tiếp tục') || command.includes('play')) {
      console.warn('Yêu cầu: Tiếp tục phát nhạc');
      // Implementation would connect to your music service
    }
    else if (command.includes('phát bài') || command.includes('mở bài')) {
      const songTitle = command.replace(/phát bài|mở bài/gi, '').trim();
      console.warn(`Yêu cầu: Phát bài "${songTitle}"`);
      
      // Navigate to search with the song title
      this.ngZone.run(() => {
        this.router.navigate(['/search'], { 
          queryParams: { s: songTitle }
        });
      });
    }
    else {
      console.warn('Không hiểu yêu cầu. Vui lòng thử lại.');
    }
  }
}