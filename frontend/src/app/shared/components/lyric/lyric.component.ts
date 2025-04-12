import { AfterViewInit, Component, ElementRef, Input, OnChanges, OnInit, QueryList, SimpleChanges, ViewChild, ViewChildren } from '@angular/core';
import { DataShareService } from '../../../core/services/dataShare.service';
import { SongDTO } from '../../models/Song.dto';
import { NgFor, NgIf } from '@angular/common';
import { HttpClient } from '@angular/common/http';

interface LyricLine {
  time: number;
  text: string;
}

@Component({
  selector: 'app-lyric',
  imports: [NgIf, NgFor],
  templateUrl: './lyric.component.html',
  styleUrl: './lyric.component.css'
})

export class LyricComponent implements OnInit, AfterViewInit, OnChanges {
  @ViewChild('cdThumb') cdThumb!: ElementRef<HTMLImageElement>;
  @ViewChildren('lyricLine') lyricLineElements!: QueryList<ElementRef>;

  @Input() disabled: boolean = false;
  @Input() isPlaying:boolean = false;
  @Input() songProgress: number = 0;
  lyricLines: LyricLine[] = [];
  isLoadingLyric: boolean = false;

  song!: SongDTO;
  cdThumbAnimation!: Animation;
  private previousActiveIndex: number = -1;


  constructor(
    private dataShareService: DataShareService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
      this.dataShareService.currentData.subscribe(data => {
        if(data) {
          this.song = data;

          if (this.song.lyric) {
            const lyricPath = 'http://localhost:8080/symphony/uploads' + this.song.lrc;
            this.loadLyricFile(lyricPath);
          }
        }
      })

  }

    ngOnChanges(changes: SimpleChanges) {
      if (changes['isPlaying']) {
        if(this.isPlaying) this.play();
        else this.pause();
      }

      if(changes['songProgress']) {
        this.scrollToActiveLine();
      }
    }

    activeLyricLine() {

    }

  ngAfterViewInit() {
    if(this.cdThumb) {
      this.cdThumbAnimation = this.cdThumb.nativeElement.animate([
        { transform: 'rotate(360deg)' }
      ], {
        duration: 18000,
        iterations: Infinity,
        easing: 'linear'
      });
  
      this.pause();
  
      if(this.isPlaying) this.play();
    }
  }

  loadLyricFile(lyricPath: string) {
    this.isLoadingLyric = true;
    this.http.get(lyricPath, { responseType: 'text' })
      .subscribe({
        next: (data) => {

        this.lyricLines = data
          .split('\n')
          .filter(line => line.trim() !== '')
          .map(line => {
            const timeTag = this.extractTimeTag(line);
            const time = timeTag ? this.parseTimeTag(timeTag) : 0;
            const text = line.replace(timeTag ?? '', '').trim();
            return { time, text };
          });
        this.isLoadingLyric = false;
        },
        error: (error) => {
          console.error('Không thể tải file lời bài hát:', error);
          this.lyricLines = [];
          this.isLoadingLyric = false;
        }
      });
  }

  get activeIndex(): number {
    const index = this.lyricLines.findIndex((line, i, arr) => {
      const next = arr[i + 1];
      return this.songProgress >= line.time && (!next || this.songProgress < next.time);
    });
    return index;
  }

  scrollToActiveLine() {
    const currentIndex = this.activeIndex;
  
    if (currentIndex === this.previousActiveIndex) return;
  
    const activeElement = this.lyricLineElements.get(currentIndex);
    if (activeElement) {
      activeElement.nativeElement.scrollIntoView({
        behavior: 'smooth',
        block: 'center',
      });
      this.previousActiveIndex = currentIndex;
    }
  }
  

  play() {
    if(this.cdThumbAnimation) this.cdThumbAnimation.play();
  }

  pause() {
    if(this.cdThumbAnimation) this.cdThumbAnimation.pause();
  }

  private extractTimeTag(line: string): string | null {
    const match = line.match(/\[\d{2}:\d{2}(?:\.\d{1,2})?\]/);
    return match ? match[0] : null;
  }
  
  private parseTimeTag(timeTag: string): number {
    const match = timeTag.match(/\[(\d{2}):(\d{2}(?:\.\d{1,2})?)\]/);
    if (!match) return 0;
  
    const minutes = parseInt(match[1], 10);
    const seconds = parseFloat(match[2]);
  
    const totalSeconds = minutes * 60 + seconds;
    return Math.round(totalSeconds * 100) / 100; // Làm tròn đến 2 chữ số thập phân
  }
  
}
