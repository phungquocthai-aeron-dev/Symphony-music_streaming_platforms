import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SingerService } from '../../core/services/singer.service';
import { SingerDTO } from '../../shared/models/Singer.dto';
import { ResponseData } from '../../shared/models/ResponseData';
import { UserDTO } from '../../shared/models/User.dto';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule, DecimalPipe, NgFor, NgIf } from '@angular/common';
import { SongService } from '../../core/services/song.service';
import { SongDTO } from '../../shared/models/Song.dto';
import { RowCardComponent } from '../../shared/components/row-card/row-card.component';
import { CategoryDTO } from '../../shared/models/Category.dto';
import { DataShareService } from '../../core/services/dataShare.service';
import { FormControl, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { TopSongDTO } from '../../shared/models/TopSong.dto';
import { AlbumDTO } from '../../shared/models/Album.dto';
import { AlbumService } from '../../core/services/album.service';
import { AlbumCardComponent } from '../../shared/components/album-card/album-card.component';
import { PlaylistService } from '../../core/services/playlist.service';
import { PlaylistDTO } from '../../shared/models/Playlist.dto';

declare var bootstrap: any;

@Component({
  selector: 'app-singer',
  imports: [NgIf, NgFor, RowCardComponent, ReactiveFormsModule, DecimalPipe, FormsModule, AlbumCardComponent, CommonModule],
  templateUrl: './singer.component.html',
  styleUrl: './singer.component.css'
})
export class SingerComponent implements OnInit, OnDestroy {
  @ViewChild('editModal', { static: false }) editModalRef!: ElementRef;
  @ViewChild('inputSongCreate', { static: false }) inputSongCreate!: ElementRef<HTMLInputElement>;
  @ViewChild('inputSongEdit', { static: false }) inputSongEdit!: ElementRef<HTMLInputElement>;
  @ViewChild('close_create_modal', { static: false }) closeCreateButton!: ElementRef;
  @ViewChild('close_update_modal', { static: false }) closeUpdateButton!: ElementRef;


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
  playlists: PlaylistDTO[] = [];
  isLoaded = false;

  songForm!: FormGroup;
  musicFile?: File | null;
  lrcFile?: File | null;
  lyricFile?: File | null;
  songImgFile?: File | null;

  lrcFileUpd?: File | null;
  lyricFileUpd?: File | null;
  songImgFileUpd?: File | null;

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

  editSongForm = new FormGroup({
    songName: new FormControl('', Validators.required),
    author: new FormControl('', Validators.required),
    singersId: new FormControl([], Validators.required),
    isVip: new FormControl(false),
    categoryIds: new FormControl<number[]>([], Validators.required),
    duration: new FormControl('', Validators.required),
    total_listens: new FormControl('0', Validators.required),
    releaseDate: new FormControl('', Validators.required),
    song_id: new FormControl('', Validators.required),
    song_img: new FormControl('', Validators.required),
    lrc: new FormControl('', Validators.required),
    lyric: new FormControl('', Validators.required)
  })

  constructor(
    private route: ActivatedRoute,
    private singerService: SingerService,
    private authService: AuthService,
    private songService: SongService,
    private albumService: AlbumService,
    private dataShareService: DataShareService,
    private playlistService: PlaylistService
  ) {}

  ngOnInit() {
    const user = this.authService.getUserInfo();
    if(user) {
      this.playlistService.getPlaylistByUserId(user.userId).subscribe(response => {
        this.playlists = response.result || [];
        console.log(this.playlists)
      });
    }
    
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
              this.albumService.getAlbumsBySingerId(singerId).subscribe({
                next: (data) => {
                  this.albums = data.result;
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

  getTotalListens(): number {
    return this.songs.reduce((sum, song) => sum + song.total_listens, 0);
  }

  showNotification(event: { title: string, content: string, isSuccess: boolean }) {
    this.notifyTitle = event.title;
    this.notifyContent = event.content;
    this.isSuccess = event.isSuccess;
  
    setTimeout(() => {
      this.clearNotify();
    }, 3000);

    if(this.singerId) this.loadSingerData(this.singerId);
  }

  clearNotify() {
    this.notifyTitle = '';
    this.notifyContent = '';
  }

  openModal() {
    if (!this.editModalRef?.nativeElement) {
      console.error('editModalRef chưa được gán!');
      return;
    }
  console.log(this.editModalRef.nativeElement)
    const modal = new bootstrap.Modal(this.editModalRef.nativeElement);
    modal.show();
  }
  

  isOwner(): boolean {
    if(!this.singerOwn) return false;
    return this.singer.singer_id === this.singerOwn.singer_id;
  }

  onSongSelected(song: SongDTO | TopSongDTO) {
    if(!this.isTopSong(song)) {
      this.editSongForm.reset();
      this.musicFile = null;
      this.lrcFile = null;
      this.lyricFile = null;
      this.songImgFile = null;

      this.selectedSong = song;
      this.songSelectedImg = 'http://localhost:8080/symphony/uploads' + song.song_img;

      if (this.selectedSong) {
        this.editSongForm.patchValue({
          songName: this.selectedSong.songName,
          isVip: this.selectedSong.isVip,
          author: this.selectedSong.author,
          releaseDate: this.selectedSong.releaseDate.toString(),
          duration: this.selectedSong.duration.toString(),
          song_id: this.selectedSong.song_id.toString(),
          song_img: this.selectedSong.song_img,
          lyric: this.selectedSong.lyric,
          lrc: this.selectedSong.lrc,
        });
      }

      if (this.selectedSong.categories) {
        const selectedCategoryIds = this.selectedSong.categories.map(cat => cat.category_id);
        this.editSongForm.patchValue({ categoryIds: selectedCategoryIds });
      }
      
      
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

  onSubmitDelete() {
    if(this.singerId) {
      this.songService.deleteSong(this.selectedSong.song_id.toString(), this.singerId.toString()).subscribe({
        next: (data) => {
          if(this.singerId) this.loadSingerData(this.singerId);
          this.notifyTitle = "Xóa bài hát";
          this.notifyContent = "Bài hát đã được xóa!";
          this.isSuccess = true;
          setTimeout(() => {
            this.clearNotify();
          }, 3000);
        },
        error: (error) => {
          this.notifyTitle = "Xóa bài hát";
          this.notifyContent = "Xóa bài hát thất bại";
          this.isSuccess = false;
          console.error(error)
          setTimeout(() => {
            this.clearNotify();
          }, 3000);
        }
      })
    }
  }

  onSubmitCreate() {
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
        this.defaultSongImg = "http://localhost:8080/symphony/uploads/images/other/no-img.png";

        this.closeCreateButton.nativeElement.click();
        if(this.singerId) this.loadSingerData(this.singerId);

        this.notifyTitle = "Thêm bài hát";
        this.notifyContent = "Bài hát đã được thêm!";
        this.isSuccess = true;
        setTimeout(() => {
          this.clearNotify();
        }, 3000);
      },
      error: (error) => {
        this.notifyTitle = "Thêm bài hát";
        this.notifyContent = "Thêm bài hát thất bại";
        this.isSuccess = false;
        setTimeout(() => {
          this.clearNotify();
        }, 3000);
      }
    });
  }

  onSubmitUpdate() {
    if (this.editSongForm.invalid) {
      Object.keys(this.editSongForm.controls).forEach(field => {
        const control = this.editSongForm.get(field);
        control?.markAsTouched({ onlySelf: true });
      });


    }

    const formData = new FormData();
      formData.append('songName', this.editSongForm.value.songName || '');
      formData.append('author', this.editSongForm.value.author || '');
      formData.append('isVip', this.editSongForm.value.isVip ? 'true' : 'false');
      formData.append('duration', this.editSongForm.value.duration || '');
      formData.append('total_listens', this.editSongForm.value.total_listens || '0');
      formData.append('releaseDate', this.editSongForm.value.releaseDate || '');  

      formData.append('song_id', this.editSongForm.value.song_id || ''); 
      formData.append('song_img', this.editSongForm.value.song_img || ''); 
      formData.append('lrc', this.editSongForm.value.lrc || ''); 
      formData.append('lyric', this.editSongForm.value.lyric || ''); 

      if(this.selectedSong) {
        this.selectedSong.singers.forEach((singer: SingerDTO) => {
          if(this.singerId) formData.append('singersId', singer.singer_id.toString());
        })
        if (this.editSongForm.value.singersId && this.editSongForm.value.singersId.length > 0) {
          this.editSongForm.value.singersId.forEach((id: number) => {
          formData.append('singersId', id.toString());
          });
        }
      }


      if (this.editSongForm.value.singersId && this.editSongForm.value.singersId.length > 0) {
        this.editSongForm.value.singersId.forEach((id: number) => {
        formData.append('singersId', id.toString());
        });
      }

      if (this.editSongForm.value.categoryIds && this.editSongForm.value.categoryIds.length > 0) {
        this.editSongForm.value.categoryIds.forEach((id: number) => {
          formData.append('categoryIds', id.toString());
          console.log(id)
        });
      }
  
    // Thêm các file
    if (this.lrcFileUpd) {
      formData.append('lrcFile', this.lrcFileUpd);
      formData.append('lrc', 'null');
    }


    if (this.lyricFileUpd) {
      formData.append('lyricFile', this.lyricFileUpd);
      formData.append('lyric', 'null');
    }

    if (this.songImgFileUpd) {
      formData.append('songImgFile', this.songImgFileUpd);
      formData.append('song_img', 'null');
    }
  
    // Gửi request
    this.songService.songUpdate(formData).subscribe({
      next: (response) => {
        console.log('Upload thành công:', response);
        // Reset form và đóng modal
        this.editSongForm.reset();
        this.musicFile = null;
        this.lrcFile = null;
        this.lyricFile = null;
        this.songImgFile = null;
        
        this.closeUpdateButton.nativeElement.click();
        if(this.singerId) this.loadSingerData(this.singerId);

        this.notifyTitle = "Cập nhật bài hát";
        this.notifyContent = "Bài hát đã được cập nhật!";
        this.isSuccess = true;
        setTimeout(() => {
          this.clearNotify();
        }, 3000);

      },
      error: (error) => {
        this.notifyTitle = "Cập nhật bài hát";
        this.notifyContent = "Cập nhật bài hát thất bại";
        this.isSuccess = false;
        console.error(error);
        setTimeout(() => {
          this.clearNotify();
        }, 3000);
      }
    });
  }

  onFileUpdateChange(event: any, fileType: string) {
    const file = event.target.files[0];
    if (file) {
      switch (fileType) {
        case 'lrc':
          this.lrcFileUpd = file;
          break;
        case 'lyric':
          this.lyricFileUpd = file;
          break;
        case 'image':
          this.songImgFileUpd = file;
          break;
      }
    }
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

  // isCategorySelected(categoryId: number): boolean {
  //   // Kiểm tra selectedSong và selectedSong.categories tồn tại
  //   if (!this.selectedSong || !this.selectedSong.categories) {
  //     return false;
  //   }
  //   return this.selectedSong.categories.some(cat => cat.category_id === categoryId);
  // }

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
    console.log(this.songSelectedImg)
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

  @ViewChild('closeButtonAlbumRef', { static: false }) closeFormCreateAlbum!: ElementRef;
  album!: AlbumDTO;
  albums: AlbumDTO[] = [];
  newAlbumName: string = '';
  songsOfAlbum: SongDTO[] = [];
  quantity = 0;
  selectedImage: File | null = null;
  defaultAlbumImg:string = "http://localhost:8080/symphony/uploads/images/other/no-img.png";
  notifyContent = "";
  notifyTitle = "";
  isSuccess = true;

onFileAlbumImgChange(event: Event): void {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  
  if (!file) {
    return;
  }

  this.selectedImage = file;
  
  // Đọc file và hiển thị preview
  const reader = new FileReader();
  reader.onload = (e: any) => {
    this.defaultAlbumImg = e.target.result;
  };
  
  reader.readAsDataURL(file);

}


  loadAlbums(): void {
    this.albumService.getAlbumsBySingerId(this.originUser.userId).subscribe(response => {
      this.albums = response.result || [];
    });
  }

  turnOn() {
    this.dataShareService.changePlaylistSong(this.songs);
  }

  addAlbum(): void {
    
    if(this.singerId) {

      const trimmedName = this.newAlbumName.trim();
    if (!trimmedName || !this.selectedImage) return;
    this.albumService.createAlbum(this.singer.singer_id, trimmedName, this.selectedImage).subscribe(response => {
      this.albums.push(response.result);
      this.newAlbumName = '';
      this.selectedImage = null;
      this.closeFormCreateAlbum.nativeElement.click();
    });
    }
  }
  
  showAlbumSongs(songs: SongDTO[]) {
    this.songsOfAlbum= songs;
    this.quantity = songs.length;
  }

  albumSelect(album: AlbumDTO): void {
    this.album = album;
    this
  }

  removeAlbum(albumId: number): void {
    this.albums = this.albums.filter(a => a.albumId !== albumId);
  }

  updateAlbumName(event: { albumId: number; newName: string }): void {
    const album = this.albums.find(a => a.albumId === event.albumId);
    if (album) {
      album.albumName = event.newName;
    }
  }
}
