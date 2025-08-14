import { AfterViewInit, Component, ElementRef, Input, OnDestroy, OnInit, Output, ViewChild, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { SongDTO } from '../../models/Song.dto';
import { RouterModule } from '@angular/router';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { SongService } from '../../../core/services/song.service';
import { TimeFormatPipe } from '../../pipes/time-format.pipe';
import { DataShareService } from '../../../core/services/dataShare.service';
import { UserDTO } from '../../models/User.dto';
import { PlaylistDTO } from '../../models/Playlist.dto';
import { PlaylistService } from '../../../core/services/playlist.service';

@Component({
  selector: 'app-audio-menu',
  imports: [ RouterModule, NgFor, NgIf, TimeFormatPipe, CommonModule],
  templateUrl: './audio-menu.component.html',
  styleUrl: './audio-menu.component.css'
})

export class AudioMenuComponent implements AfterViewInit, OnChanges {
  @Input() song!: SongDTO;
  @Input() user!: UserDTO;
  @Input() playlistSongs: SongDTO[] = [];
  @Input() isOptionPlaylist = true;
  @Input()playlists: PlaylistDTO[] = [];
  @Input() recentSongs: SongDTO[] = [];  @ViewChild('audio') audioRef!: ElementRef<HTMLAudioElement>;
  @ViewChild('inputVolume') volumeRef!: ElementRef<HTMLInputElement>;
  @ViewChild('progressVolume') progressVolumeRef!: ElementRef<HTMLInputElement>;
  @ViewChild('inputProgress') progressRef!: ElementRef<HTMLInputElement>;
  @ViewChild('currentTimeRef', { static: false }) currentTimeRef!: ElementRef;
  @ViewChild('progressBar') progressBar!: ElementRef<HTMLDivElement>;

  @Output() turnRightSide = new EventEmitter<boolean>();
  @Output() turnLyric = new EventEmitter<boolean>();
  @Output() playingSong = new EventEmitter<boolean>();
  @Output() songProgress = new EventEmitter<number>();
  @Output() notify = new EventEmitter<{ title: string, content: string, isSuccess: boolean }>();


  currentTime: number = 0; // s

  isPlaying: boolean = false;
  isTurnRightSide: boolean = false;
  isTurnLyric: boolean = false;
  OptionStatus: string = "UNSELECTED";
  hasReported: boolean = false;
  volume: number = 20; // miền giá trị 0 đến 100
  isMuted: boolean = false;
  progress: string = '0'; // miền giá trị 0 đến 100
  inputtingProgress: boolean = false;
  duration = 0;
  notifyContent = "";
  notifyTitle = "";
  isSuccess = true;

  constructor(
      private authService: AuthService,
      private songService: SongService,
      private dataShareService: DataShareService,
      private playlistService: PlaylistService
    ) {}

    ngAfterViewInit(): void {

      setTimeout(() => {
        if (!this.audioRef || !this.volumeRef || !this.progressRef || !this.progressBar || !this.progressVolumeRef) {
          return;
        }
        this.loadData();
      }, 100);

      
    }

  loadData() {
    const audio = this.audioRef.nativeElement;
      const volumeInput = this.volumeRef.nativeElement;
      const progressInput = this.progressRef.nativeElement;
      const progressBar = this.progressBar.nativeElement;
      const progressVolume = this.progressVolumeRef.nativeElement;

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

      audio.addEventListener('play', () => {
        this.isPlaying = true;
        this.playingSong.emit(this.isPlaying);
      })

      audio.addEventListener('pause', () => {
        this.isPlaying = false;
        this.playingSong.emit(this.isPlaying);
      })

      audio.addEventListener('ended', () => {
        this.isPlaying = false;

        switch (this.OptionStatus) {
          case "REPEAT":
              this.playAudio();
              this.hasReported = false;
              break;
          case "RANDOM":
              this.handleRandomOption();
              break;
          default:
              this.progressRef.nativeElement.value = "100"
              this.progressBar.nativeElement.style.width = `100%`;
              this.OptionStatus = "UNSELECTED"
      }
      });

      
      audio.ontimeupdate = () => {

        this.currentTime = Math.floor(audio.currentTime)
        const time = Math.floor(audio.currentTime * 100) / 100;
        this.songProgress.emit(time);

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
    let song: SongDTO;
    let randomIndex;

    if(this.isOptionPlaylist) {
      if (this.playlistSongs.length) {
        
        do {
          randomIndex = Math.floor(Math.random() * this.playlistSongs.length);
          song = this.playlistSongs[randomIndex];
          // console.log(song.song_id + ";" + this.song.song_id)
        } while(song.song_id === this.song.song_id && this.playlistSongs.length > 1);

        this.song = this.playlistSongs[randomIndex];
        this.dataShareService.changeData(this.playlistSongs[randomIndex])
      }
    }
    else {
      if (this.recentSongs.length) {
        do {
          randomIndex = Math.floor(Math.random() * this.recentSongs.length);
          song = this.recentSongs[randomIndex];
        } while(song.song_id === this.song.song_id && this.recentSongs.length > 1);
        
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
      if(option === this.OptionStatus) {
        this.OptionStatus = "UNSELECTED";
        return;
      }

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

  get loadAudio(): string {
    if(!this.song.isVip) return "http://localhost:8080/symphony/uploads" + this.song.path;
    if(this.user) {
      if(this.song.isVip && ["VIP", "ADMIN", "SINGER"].includes(this.user.role)) return "http://localhost:8080/symphony/uploads" + this.song.path;
    }
    return "assets/other/advice.wav";
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

  toggleRightSide() {
    this.isTurnRightSide = !this.isTurnRightSide;
    this.turnRightSide.emit(this.isTurnRightSide);
  }

  toggleLyric() {
    this.isTurnLyric = !this.isTurnLyric;
    this.turnLyric.emit(this.isTurnLyric);
  }

    loadUserPlaylists() {
  const userId = this.authService.getUserInfo().userId;
  if (!userId) {
    alert('Vui lòng đăng nhập!');
    return;
  }
}

  openAddToPlaylist(song: SongDTO) {
    this.dataShareService.changeSongPlaylist(song);
    this.loadUserPlaylists();
  }
  
  addSongToPlaylist(playlistId: number) {
    let song: any = null;
    const subscription = this.dataShareService.currentSongToPlaylist.subscribe(data => {
      if (data) {
        song = data;
        this.playlistService.addSongToPlaylist(playlistId, song.song_id).subscribe({
          next: (data) => {
            this.notify.emit({
              title: 'Thêm bài hát vào playlist',
              content: 'Bài hát đã được thêm vào playlist!',
              isSuccess: true
            });
            subscription.unsubscribe();
          },
          error: (err) => {
            this.notify.emit({
              title: 'Thêm bài hát vào playlist',
              content: 'Thêm bài hát vào playlist thất bại!',
              isSuccess: true
            });
            subscription.unsubscribe(); 
          }
        });
      } else {
        subscription.unsubscribe();
      }
    });  
    this.dataShareService.changeSongPlaylist(null);
  
  
  }

}
