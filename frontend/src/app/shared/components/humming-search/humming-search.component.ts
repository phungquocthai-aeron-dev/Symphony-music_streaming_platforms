import { Component, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { AudioRecordingService } from '../../../core/services/audio-recording.service';
import { Subscription } from 'rxjs';
import { NgFor, NgIf, NgClass, DecimalPipe } from '@angular/common';
import { SongDTO } from '../../models/Song.dto';
import { DataShareService } from '../../../core/services/dataShare.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-humming-search',
  imports: [ NgIf, NgClass],
  templateUrl: './humming-search.component.html',
  styleUrl: './humming-search.component.css'
})
export class HummingSearchComponent implements OnInit, OnDestroy {
  @Output() close = new EventEmitter<void>();

  // Recording state
  isRecording = false;
  recordingTime = 0;
  recordedAudio: Blob | null = null;
  audioUrl: string | null = null;

  // Upload state
  selectedFile: File | null = null;

  // Search state
  isSearching = false;
  searchResults: SongDTO[] = [];
  errorMessage: string | null = null;

  // Subscriptions
  private subscriptions: Subscription[] = [];
  private recordingTimer: any;

  constructor(
    private audioService: AudioRecordingService,
    private dataShareService: DataShareService,
    private router: Router
  ) {}

  ngOnInit() {
    this.subscriptions.push(
      this.audioService.isRecording$.subscribe(recording => {
        this.isRecording = recording;
        if (recording) {
          this.startTimer();
        } else {
          this.stopTimer();
        }
      })
    );
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.stopTimer();
    if (this.audioUrl) {
      URL.revokeObjectURL(this.audioUrl);
    }
  }

  // Recording methods
  async startRecording() {
  try {
    this.errorMessage = null;
    this.selectedFile = null; // Reset file upload khi bắt đầu ghi âm
    await this.audioService.startRecording();
  } catch (error: any) {
    this.errorMessage = error.message;
  }
}

async stopRecording() {
  try {
    this.recordedAudio = await this.audioService.stopRecording();
    this.selectedFile = null; // Reset file upload khi có file ghi âm mới
    this.createAudioUrl();
  } catch (error: any) {
    this.errorMessage = error.message;
  }
}

  // File upload
  onFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files[0]) {
      this.selectedFile = target.files[0];
      this.recordedAudio = null; // Clear recorded audio
      this.createAudioUrl();
    }
  }

  // Search methods
  search() {
    
  let audioToSearch: File | null = null;

  if (this.selectedFile) {
    audioToSearch = this.selectedFile;
  } else if (this.recordedAudio) {
    audioToSearch = new File([this.recordedAudio], `recording-${Date.now()}.wav`, { type: 'audio/wav' });
  } else {
    this.errorMessage = 'Vui lòng ghi âm hoặc chọn file âm thanh!';
    return;
  }

  this.isSearching = true;
  this.errorMessage = null;
  this.searchResults = [];

  this.audioService.searchByHumming(audioToSearch).subscribe({
    next: (response) => {
      if (response && response.length > 0) {
        this.searchResults = response;
        console.log('Kết quả tìm kiếm:', response);
        this.closeSearch();
        this.dataShareService.changeSearchHummingResults(response);
        this.router.navigate(['/search'], { queryParams: { type: 'humming' } });
      } else {
        this.errorMessage = 'Không tìm thấy bài hát phù hợp. Hãy thử ghi âm rõ ràng hơn.';
      }
      this.isSearching = false;
    },
    error: (error) => {
      console.error('Search error:', error);
      this.errorMessage = error.error?.message || 'Tìm kiếm thất bại. Vui lòng thử lại.';
      this.isSearching = false;
    }
  });
}


  // Utility methods
  private createAudioUrl() {
    if (this.audioUrl) {
      URL.revokeObjectURL(this.audioUrl);
    }

    const audio = this.selectedFile || this.recordedAudio;
    if (audio) {
      this.audioUrl = URL.createObjectURL(audio);
    }
  }

  private startTimer() {
    this.recordingTime = 0;
    this.recordingTimer = setInterval(() => {
      this.recordingTime++;
    }, 1000);
  }

  private stopTimer() {
    if (this.recordingTimer) {
      clearInterval(this.recordingTimer);
      this.recordingTimer = null;
    }
  }

  formatTime(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  // Play song
  playSong(result: SongDTO) {
    // Ví dụ: mở đường dẫn bài hát hoặc thực hiện gì đó
    // Bạn cần bổ sung logic cụ thể dựa theo model của bạn
    console.log('Play song:', result.songName);
  }

  closeSearch() {
    this.close.emit();
  }
}
