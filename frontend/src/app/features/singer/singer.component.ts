import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SingerService } from '../../core/services/singer.service';
import { SingerDTO } from '../../shared/models/Singer.dto';
import { ResponseData } from '../../shared/models/ResponseData';
import { UserDTO } from '../../shared/models/User.dto';
import { AuthService } from '../../core/services/auth.service';
import { DecimalPipe, NgFor, NgIf } from '@angular/common';
import { SongService } from '../../core/services/song.service';
import { SongDTO } from '../../shared/models/Song.dto';
import { RowCardComponent } from '../../shared/components/row-card/row-card.component';
import { CategoryDTO } from '../../shared/models/Category.dto';
import { DataShareService } from '../../core/services/dataShare.service';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { TopSongDTO } from '../../shared/models/TopSong.dto';
import { error } from 'node:console';

declare var bootstrap: any;

@Component({
  selector: 'app-singer',
  imports: [NgIf, NgFor, RowCardComponent, ReactiveFormsModule, DecimalPipe],
  templateUrl: './singer.component.html',
  styleUrl: './singer.component.css'
})
export class SingerComponent implements OnInit, OnDestroy {
  @ViewChild('editModel', { static: false }) editModalRef!: ElementRef;
  @ViewChild('inputSongCreate', { static: false }) inputSongCreate!: ElementRef<HTMLInputElement>;
  @ViewChild('inputSongEdit', { static: false }) inputSongEdit!: ElementRef<HTMLInputElement>;
  @ViewChild('close_create_modal', { static: false }) closeCreateButton!: ElementRef;


  defaultSongImg:string = "http://localhost:8080/symphony/uploads/images/other/no-img.png";
  songSelectedImg!: string;
  singerId: number | string | null = null;
  singer: SingerDTO = {} as SingerDTO;
  songs: SongDTO[] = [];
  originUser!: UserDTO;
  singerOwn!: SingerDTO;
  selectedSong!: SongDTO;
  singerNotPresent!: SingerDTO[];
  singerExclude!: SingerDTO[];
  categories!: CategoryDTO[];

  isLoaded = false;

  songForm!: FormGroup;
  musicFile?: File | null;
  lrcFile?: File | null;
  lyricFile?: File | null;
  songImgFile?: File | null;

  maxDate?: string;

  private paramSubscription!: Subscription;

  createSongForm = new FormGroup({
    songName: new FormControl('', Validators.required),
    author: new FormControl('', Validators.required),
    singersId: new FormControl([], Validators.required),
    isVip: new FormControl(false),
    categoryIds: new FormControl([], Validators.required),
    duration: new FormControl('', Validators.required),
    total_listens: new FormControl('0', Validators.required),
    releaseDate: new FormControl('', Validators.required)
  })

  updateSongForm = new FormGroup({
    songNameUpd: new FormControl('', Validators.required),
    authorUpd: new FormControl('', Validators.required),
    singersIdUpd: new FormControl([], Validators.required),
    isVipUpd: new FormControl(false),
    categoryIdsUpd: new FormControl([], Validators.required),
    durationUpd: new FormControl('', Validators.required),
    total_listensUpd: new FormControl('0', Validators.required),
    releaseDateUpd: new FormControl('', Validators.required)
  })

  constructor(
    private route: ActivatedRoute,
    private singerService: SingerService,
    private authService: AuthService,
    private songService: SongService,
    private dataShareService: DataShareService
  ) {}

  ngOnInit() {
    this.dataShareService.changeLeftSideInfo("Singer");

    this.paramSubscription = this.route.paramMap.subscribe(params => {
      this.singerId = params.get('id');
      console.log(this.singerId)
      if(this.singerId) {
        this.isLoaded = false;
        this.loadSingerData(this.singerId);
      }
    });
  }

  loadSingerData(singerId: string | number) {
    
    if(singerId) {
      this.isLoaded = true;

      this.singerService.getSingerById(singerId).subscribe({
          next: (response: ResponseData<SingerDTO>) => {
            if (response.code === 1000) {
              this.singer = response.result;
              this.dataShareService.changeTitle(this.singer.stageName);

              this.authService.getUserBySingerId(this.singer.singer_id).subscribe({
                next: (response: ResponseData<UserDTO>) => {
                  if (response.code === 1000) {
                    this.originUser = response.result;
           
                  }

                  if(this.authService.isLoggedIn()) {
                    this.singerService.getSingersNotIn([this.singer]).subscribe({
                      next: (response: ResponseData<SingerDTO[]>) => {
                        if (response.code === 1000) {
                          console.log(response.result)
                          this.singerExclude = response.result;
                        }
                      },
                      error: (error) => {
                        console.log(error)
                      }
                    })
                  }
                 
                },
                error: (error) => {
                  console.log(error)
                  
                }
              })
              this.songService.getSongsBySinger(this.singer.singer_id).subscribe({
                next: (response: ResponseData<SongDTO[]>) => {
                  if (response.code === 1000) {
                    this.songs = response.result;
                  }
                },
                error: (error) => {
                  console.log(error)
                }
              })

            }
          },
          error: (error) => {
            console.log(error)
          }
      }) 

      const today = new Date();
      today.setMinutes(today.getMinutes() - today.getTimezoneOffset()); // Điều chỉnh theo múi giờ địa phương
      this.maxDate = today.toISOString().split('T')[0]; 
    

      this.dataShareService.currentSinger.subscribe(data => {
        if (data) {
          this.singerOwn = data;
        }
      });

      this.songService.getCategories().subscribe({
        next: (response: ResponseData<CategoryDTO[]>) => {
          if (response.code === 1000) {
            this.categories = response.result;
          }
        },
        error: (error) => {
          console.log(error)
        }
      })
    }

   
  }

  openModal() {
    const modal = new bootstrap.Modal(this.editModalRef.nativeElement);
    modal.show();
  }

  isOwner(): boolean {
    if(!this.singerOwn) return false;
    return this.singer.singer_id === this.singerOwn.singer_id;
  }

  onSongSelected(song: SongDTO | TopSongDTO) {
    if(!this.isTopSong(song)) {
      this.selectedSong = song;
      this.songSelectedImg = 'http://localhost:8080/symphony' + song.song_img;
    }
  }

  onEdit() {
    if(this.selectedSong) {
      this.singerService.getSingersNotIn(this.selectedSong.singers).subscribe({
        next: (response: ResponseData<SingerDTO[]>) => {
          if (response.code === 1000) {
            this.singerNotPresent = response.result;
          }
        },
        error: (error) => {
          console.log(error)
        }
      })
    }
  }

  onSubmitEdit() {

  }

  onSubmitDelete() {
    console.log(this.selectedSong)
    this.songService.deleteSong(this.selectedSong.song_id).subscribe({
      next: (data) => {
        if(this.singerId) this.loadSingerData(this.singerId);
      },
      error: (error) => {
        console.error("Xoá bài hát thất bại!")
      }
    })
  }

  onSubmitCrate() {
    if (this.createSongForm.invalid) {
      Object.keys(this.createSongForm.controls).forEach(field => {
        const control = this.createSongForm.get(field);
        control?.markAsTouched({ onlySelf: true });
      });


    }

    const formData = new FormData();
      formData.append('songName', this.createSongForm.value.songName || '');
      formData.append('author', this.createSongForm.value.author || '');
      formData.append('isVip', this.createSongForm.value.isVip ? 'true' : 'false');
      formData.append('duration', this.createSongForm.value.duration || '');
      formData.append('total_listens', this.createSongForm.value.total_listens || '0');
      formData.append('releaseDate', this.createSongForm.value.releaseDate || '');  

      if(this.singerId) formData.append('singersId', this.singerId.toString());
      if (this.createSongForm.value.singersId && this.createSongForm.value.singersId.length > 0) {
        this.createSongForm.value.singersId.forEach((id: number) => {
        formData.append('singersId', id.toString());
        });
      }

      if (this.createSongForm.value.categoryIds && this.createSongForm.value.categoryIds.length > 0) {
        this.createSongForm.value.categoryIds.forEach((id: number) => {
          formData.append('categoryIds', id.toString());
        });
      }
  
    // Thêm các file
    if (this.musicFile) formData.append('musicFile', this.musicFile);
    if (this.lrcFile) formData.append('lrcFile', this.lrcFile);
    if (this.lyricFile) formData.append('lyricFile', this.lyricFile);
    if (this.songImgFile) formData.append('songImgFile', this.songImgFile);
  
    // Gửi request
    this.songService.createSong(formData).subscribe({
      next: (response) => {
        console.log('Upload thành công:', response);
        // Reset form và đóng modal
        this.createSongForm.reset();
        this.musicFile = null;
        this.lrcFile = null;
        this.lyricFile = null;
        this.songImgFile = null;
        
        this.closeCreateButton.nativeElement.click();
        if(this.singerId) this.loadSingerData(this.singerId);
      },
      error: (error) => {
        console.error('Lỗi khi upload:', error);
              }
    });
  }

  onFileCreateChange(event: any, fileType: string) {
    const file = event.target.files[0];
    if (file) {
      switch (fileType) {
        case 'music':
          this.musicFile = file;
          break;
        case 'lrc':
          this.lrcFile = file;
          break;
        case 'lyric':
          this.lyricFile = file;
          break;
        case 'image':
          this.songImgFile = file;
          break;
      }
    }
  }

  // isCategorySelected(categoryId: number): boolean {
  //   if(this.categories) return false;
  //   return this.selectedSong.categories.some(cat => cat.category_id === categoryId);
  // }

  isCategorySelected(categoryId: number): boolean {
    // Kiểm tra selectedSong và selectedSong.categories tồn tại
    if (!this.selectedSong || !this.selectedSong.categories) {
      return false;
    }
    return this.selectedSong.categories.some(cat => cat.category_id === categoryId);
  }

  triggerFileInput(): void {
    this.inputSongCreate.nativeElement.click();
  }

  triggerFileInputEdit(): void {
    this.inputSongEdit.nativeElement.click();
  }

  // Xử lý khi chọn file
  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    
    if (!file) {
      return;
    }
    
    // Đọc file và hiển thị preview
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.defaultSongImg = e.target.result;
    };
    
    reader.readAsDataURL(file);
  }

  onFileEditChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    
    if (!file) {
      return;
    }
    
    // Đọc file và hiển thị preview
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.songSelectedImg = e.target.result;
    };
    
    reader.readAsDataURL(file);
  }
  
  ngOnDestroy() {
    // Hủy subscription khi component bị hủy để tránh rò rỉ bộ nhớ
    if (this.paramSubscription) {
      this.paramSubscription.unsubscribe();
    }
  }
  

  private isTopSong(song: SongDTO | TopSongDTO): song is TopSongDTO {
    return 'total_listens_per_hour' in song;  // Kiểm tra xem có thuộc tính 'topRank' hay không
  }
}
