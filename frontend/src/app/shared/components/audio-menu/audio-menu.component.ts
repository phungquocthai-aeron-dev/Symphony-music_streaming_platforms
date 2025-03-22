import { AfterViewInit, Component, ElementRef, Input, OnDestroy, OnInit, Output, ViewChild, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { SongDTO } from '../../models/Song.dto';
import { RouterModule } from '@angular/router';
import { NgFor, NgIf } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { SongService } from '../../../core/services/song.service';
import { TimeFormatPipe } from '../../pipes/time-format.pipe';
import { DataShareService } from '../../../core/services/dataShare.service';

@Component({
  selector: 'app-audio-menu',
  imports: [ RouterModule, NgFor, NgIf, TimeFormatPipe],
  templateUrl: './audio-menu.component.html',
  styleUrl: './audio-menu.component.css'
})

export class AudioMenuComponent implements OnInit, AfterViewInit, OnDestroy, OnChanges {
  @Input() song!: SongDTO;
  @Input() playlistSongs: SongDTO[] = [];
  @Input() isOptionPlaylist = true;
  @Input() recentSongs: SongDTO[] = [];  @ViewChild('audio') audioRef!: ElementRef<HTMLAudioElement>;
  @ViewChild('inputVolume') volumeRef!: ElementRef<HTMLInputElement>;
  @ViewChild('progressVolume') progressVolumeRef!: ElementRef<HTMLInputElement>;
  @ViewChild('inputProgress') progressRef!: ElementRef<HTMLInputElement>;
  @ViewChild('currentTimeRef', { static: false }) currentTimeRef!: ElementRef;
  @ViewChild('progressBar') progressBar!: ElementRef<HTMLDivElement>;

  @Output() turnRightSide = new EventEmitter<boolean>();

  currentTime: number = 0; // s

  isPlaying: boolean = false;
  isTurnRightSide: boolean = false;
  OptionStatus: string = "UNSELECTED";
  hasReported: boolean = false;
  volume: number = 20; // miền giá trị 0 đến 100
  isMuted: boolean = false;
  progress: string = '0'; // miền giá trị 0 đến 100
  inputtingProgress: boolean = false;
  duration = 0;

  constructor(
      private authService: AuthService,
      private songService: SongService,
      private dataShareService: DataShareService
    ) {}

    ngOnInit(): void {
        console.log(this.song)
    }

    ngAfterViewInit(): void {

      setTimeout(() => {
        if (!this.audioRef || !this.volumeRef || !this.progressRef || !this.progressBar || !this.progressVolumeRef) {
          console.error("Lỗi: ViewChild chưa được khởi tạo.");
          return;
        }
        this.loadData();
      }, 100);

      
    }

  ngOnDestroy(): void {
    // this.audioRef.nativeElement.ontimeupdate = null;
    console.log("Destroyed")
  }

  loadData() {
    const audio = this.audioRef.nativeElement;
      const volumeInput = this.volumeRef.nativeElement;
      const progressInput = this.progressRef.nativeElement;
      const progressBar = this.progressBar.nativeElement;
      const progressVolume = this.progressVolumeRef.nativeElement;

      console.log(progressVolume)
      console.log("FFFFFFFFFF")
      audio.addEventListener('loadedmetadata', () => {
        this.duration = audio.duration;
      });

      audio.volume = this.volume / 100;
      progressVolume.style.width = `${volumeInput.value}%`;

  
      volumeInput.addEventListener('change', () => {
        this.volume = Number(volumeInput.value);
        audio.volume = this.volume / 100;
        this.isMuted = this.volume === 0;
        progressVolume.style.width = `${volumeInput.value}%`;
      });

      volumeInput.addEventListener('input', () => {
        this.volume = Number(volumeInput.value);
        audio.volume = this.volume / 100;
        this.isMuted = this.volume === 0;
        progressVolume.style.width = `${volumeInput.value}%`;
      });

      progressInput.addEventListener('input', () => {
        this.inputtingProgress = true;
        let value = Number(progressInput.value);
        progressBar.style.width = `${value}%`;
      })

      progressInput.addEventListener('change', () => {
        if(!isNaN(this.duration)) {
          this.progress = progressInput.value;
          audio.currentTime = Number(this.progress) / 100 * this.duration;
          progressBar.style.width = `${this.progress}%`;
          this.inputtingProgress = false;
        }
      })

      audio.addEventListener('ended', () => {
        this.isPlaying = false;

        switch (this.OptionStatus) {
          case "REPEAT":
              this.playAudio();
              break;
          case "RANDOM":
              this.handleRandomOption();
              break;
          default:
              this.OptionStatus = "UNSELECTED"
      }
      });

      
      audio.ontimeupdate = () => {

        this.currentTime = Math.floor(audio.currentTime)

        if(isNaN(this.duration)) {
          this.duration = audio.duration;
        }

        if(!isNaN(this.duration) && !this.inputtingProgress) {
          
          this.progress = this.currentTime/this.duration*100 + "";
          progressInput.value = this.progress;
          progressBar.style.width = `${this.progress}%`;
          console.log(this.inputtingProgress)
        }
        
        if (audio.currentTime >= 10 && !this.hasReported) {
          
          this.songService.reportListenedSong(this.song.song_id).subscribe({
            next: (response) => {
              console.log(`Total listens: ${response}`)
              this.song.total_listens = response
              this.hasReported = true;
            },
            error: (error) => {
              console.error("Error reporting song listen:", error)
              this.hasReported = false;
            }
          })

          if(this.authService.isLoggedIn()) {
            this.songService.userListened(this.song.song_id).subscribe({
              next: (response) => {
                console.log("OK");
              },
              error: (error) => {
                console.log(error);
              }
            });
          }

          this.hasReported = true;
        }
      };
  }

  getSongIndex(list: SongDTO[]): number {
    return list.findIndex(s => s.song_id === this.song.song_id);
  }

  handleNextOption() {
    if(this.isOptionPlaylist) {
      if (this.playlistSongs.length) {
        const length = this.playlistSongs.length;
        const currentIndex = this.getSongIndex(this.playlistSongs);
        let resultIndex = currentIndex + 1;
        if(resultIndex >= length) resultIndex = 0;

        this.song = this.playlistSongs[resultIndex];
        this.dataShareService.changeData(this.playlistSongs[resultIndex])
      }
    }
    else {
      if (this.recentSongs.length) {
        const length = this.recentSongs.length;
        const currentIndex = this.getSongIndex(this.recentSongs);
        let resultIndex = currentIndex + 1;
        if(resultIndex >= length) resultIndex = 0;

        this.song = this.recentSongs[resultIndex];
        this.dataShareService.changeData(this.recentSongs[resultIndex])
      }
    }

    this.hasReported = false;
    this.reloadAudio();
  }

  handlePreviousOption() {
    if(this.isOptionPlaylist) {
      if (this.playlistSongs.length) {
        const length = this.playlistSongs.length;
        const currentIndex = this.getSongIndex(this.playlistSongs);
        let resultIndex = currentIndex - 1;
        if(resultIndex < 0) resultIndex = length - 1;

        this.song = this.playlistSongs[resultIndex];
        this.dataShareService.changeData(this.playlistSongs[resultIndex])
      }
    }
    else {
      if (this.recentSongs.length) {
        const length = this.recentSongs.length;
        const currentIndex = this.getSongIndex(this.recentSongs);
        let resultIndex = currentIndex - 1;
        if(resultIndex < 0) resultIndex = length - 1;

        this.song = this.recentSongs[resultIndex];
        this.dataShareService.changeData(this.recentSongs[resultIndex])
      }
    }

    this.hasReported = false;
    this.reloadAudio();
  }  

  handleRandomOption() {
    if(this.isOptionPlaylist) {
      if (this.playlistSongs.length) {
        const randomIndex = Math.floor(Math.random() * this.playlistSongs.length);
        this.song = this.playlistSongs[randomIndex];
        this.dataShareService.changeData(this.playlistSongs[randomIndex])
      }
    }
    else {
      if (this.recentSongs.length) {
        const randomIndex = Math.floor(Math.random() * this.recentSongs.length);
        this.song = this.recentSongs[randomIndex];
        this.dataShareService.changeData(this.recentSongs[randomIndex])
      }
    }

    this.hasReported = false;
    this.reloadAudio();
  }  

  // Play Audio
  private playAudio(): void {
    const audio = this.audioRef.nativeElement;
    
    audio.play()
      .then(() => console.log("Playing..."))
      .catch(err => console.error("Play failed:", err));
  }

  // Pause Audio
  private pauseAudio(): void {
    this.audioRef.nativeElement.pause();
  }

  // Change volume (0.0 - 1.0)
  changeVolume(volume: number): void {
    this.audioRef.nativeElement.volume = volume;
  }

  turnOffVolume(): void {
    const audio = this.audioRef.nativeElement;
    const volumeInput = this.volumeRef.nativeElement;
    const progressVolume = this.progressVolumeRef.nativeElement;

    if(!this.isMuted) {

      audio.volume = 0;
      this.isMuted = true;
      volumeInput.value = '0'
      progressVolume.style.width = `0`;
    } else {

      audio.volume = this.volume / 100;
      this.isMuted = false;
      volumeInput.value = this.volume + "";
      progressVolume.style.width = `${this.volume}%`;
    }
  }

  // Change source and reload
  changeSrc(newSrc: string): void {
    const audio = this.audioRef.nativeElement;
    audio.src = newSrc;
    audio.load(); // Reload new source
    audio.play(); // Auto-play after load
  }

  reloadAudio() {
    if(this.audioRef) {
      const audio = this.audioRef.nativeElement;
    audio.pause();  // Dừng trước khi load
    audio.currentTime = 0; 
    audio.load(); 
  
    audio.oncanplaythrough = () => {
      audio.play();
      this.isPlaying = true;
    };
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['song']) {
      this.hasReported = false;
      this.reloadAudio();
    }
  }

  handleSelected(option: string): void {

      switch (option) {
          case "NEXT":
              this.handleNextOption();
              break;
          case "PREVIOUS":
              this.handlePreviousOption();
              break;
          case "REPEAT":
              this.OptionStatus = "REPEAT"
              break;
          case "RANDOM":
              this.OptionStatus = "RANDOM"
              break;
          case "PLAYING":
              this.isPlaying = !this.isPlaying;
              if(this.isPlaying) {
                this.playAudio();
              } else {
                this.pauseAudio();
              }
              break;
          default:
              this.OptionStatus = "UNSELECTED"
      }
  
  }

  checkOption(option: string): boolean {
    return this.OptionStatus === option;
  }

  toggleFavorite() {
    if(!this.authService.isLoggedIn()) {
      alert("Vui lòng đăng nhập để thực hiện chức năng này!")
    }
    else {
      this.songService.favoriteSong(this.song.song_id).subscribe({
        next: (response) => {
          this.song.favorite = !this.song.favorite;
        }, 
        error: (error) => {
          console.error(error)
        }
      })
    }
  }

  value = false;

  toggleRightSide() {
    this.isTurnRightSide = !this.isTurnRightSide;
    this.turnRightSide.emit(this.isTurnRightSide);
  }
}
