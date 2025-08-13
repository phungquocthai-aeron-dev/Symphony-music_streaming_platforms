import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { SongService } from '../../../core/services/song.service';
import { CommonModule, DatePipe, NgFor, NgIf } from '@angular/common';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { SongDTO } from '../../../shared/models/Song.dto';
import { DataShareService } from '../../../core/services/dataShare.service';
import { SingerDTO } from '../../../shared/models/Singer.dto';
import { SingerService } from '../../../core/services/singer.service';
import { ResponseData } from '../../../shared/models/ResponseData';
import { CategoryDTO } from '../../../shared/models/Category.dto';

@Component({
  selector: 'app-songs',
  imports: [NgFor, NgIf, FormsModule, DatePipe, CommonModule, ReactiveFormsModule],
  templateUrl: './songs.component.html',
  styleUrls: ['./songs.component.css']
})
export class SongsComponent implements OnInit {
  @ViewChild('inputSongEdit', { static: false }) inputSongEdit!: ElementRef<HTMLInputElement>;
  @ViewChild('close_update_modal', { static: false }) closeUpdateButton!: ElementRef;

  songs: SongDTO[] = [];
  songSelectedImg!: string;
  singerNotPresent!: SingerDTO[];
  categories!: CategoryDTO[];

  maxDate?: string;
  song!: SongDTO;
  displayEditModal = false;
  

    lrcFileUpd?: File | null;
  lyricFileUpd?: File | null;
  songImgFileUpd?: File | null;

    songForm!: FormGroup;
  musicFile?: File | null;
  lrcFile?: File | null;
  lyricFile?: File | null;
  songImgFile?: File | null;

  searchTitle = "";

  notifyContent = "";
  notifyTitle = "";
  isSuccess = true;

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
    private songService: SongService,
    private router: Router,
    private dataShareService: DataShareService,
     private singerService: SingerService
  ) {}

  ngOnInit() {
    this.dataShareService.changeLeftSideInfo("Songs");
    this.dataShareService.changeTitle("Quản lý bài hát");
    this.loadData();
  }

  loadData(): void {
    setTimeout(() => {
      this.clearNotify();
    }, 3000);
   const today = new Date();
      today.setMinutes(today.getMinutes() - today.getTimezoneOffset()); // Điều chỉnh theo múi giờ địa phương
      this.maxDate = today.toISOString().split('T')[0]; 
    this.loadSongs();
  }

  loadSongs() {
    this.songService.getAllSongs().subscribe({
      next: (data) => {
        this.songs = data.result;
      },
      error: (error) => {
        console.error(error);
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

  searchSongs(): void {
    if(this.searchTitle.trim().length > 0) {
      this.songService.findBySongName(this.searchTitle).subscribe({
        next: (data) => {
          this.songs = data.result;
        },
        error: () => {
          this.songs = [];
        }
      });
    } else {
      this.loadSongs();
    }
  }

  disableSong(song: SongDTO): void {
    if (confirm(`Bạn có chắc chắn muốn vô hiệu hóa bài hát "${song.songName}" (ID: ${song.song_id}) không?`)) {
      this.songService.disable(song.song_id + "").subscribe({
        next: () => {
          this.notifyTitle = "Vô hiệu hóa bài hát";
          this.notifyContent = `Đã vô hiệu hóa bài hát "${song.songName}", ID: ${song.song_id}!`;
          this.isSuccess = true;
          this.loadData();
        },
        error: (err) => {
          this.notifyTitle = "Vô hiệu hóa bài hát";
          this.notifyContent = "Vô hiệu hóa bài hát thất bại!";
          this.isSuccess = false;
          console.error(err);
        }
      });
    }
  }

  enableSong(song: SongDTO): void {
    if (confirm(`Bạn có chắc chắn muốn khôi phục bài hát "${song.songName}" (ID: ${song.song_id}) không?`)) {
      this.songService.enable(song.song_id + "").subscribe({
        next: () => {
          this.notifyTitle = "Khôi phục bài hát";
          this.notifyContent = `Đã khôi phục bài hát "${song.songName}", ID: ${song.song_id}!`;
          this.isSuccess = true;
          this.loadData();
        },
        error: (err) => {
          this.notifyTitle = "Khôi phục bài hát";
          this.notifyContent = "Khôi phục bài hát thất bại!";
          this.isSuccess = false;
          console.error(err);
        }
      });
    }
  }

  onExportSongsExcel(): void {
    this.songService.exportSongs().subscribe({
      next: (data: Blob) => {
        const blob = new Blob([data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'danh_sach_bai_hat.xlsx';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  clearNotify() {
    this.notifyTitle = '';
    this.notifyContent = '';
  }

  editSong(song: SongDTO) {

      this.editSongForm.reset();
      this.musicFile = null;
      this.lrcFile = null;
      this.lyricFile = null;
      this.songImgFile = null;

      this.song = song;
      this.songSelectedImg = 'http://localhost:8080/symphony/uploads' + song.song_img;

      if (this.song) {
        this.editSongForm.patchValue({
          songName: this.song.songName,
          isVip: this.song.isVip,
          author: this.song.author,
          releaseDate: this.song.releaseDate.toString(),
          duration: this.song.duration.toString(),
          song_id: this.song.song_id.toString(),
          song_img: this.song.song_img,
          lyric: this.song.lyric,
          lrc: this.song.lrc,
        });
      }

      if (this.song.categories) {
        const selectedCategoryIds = this.song.categories.map(cat => cat.category_id);
        this.editSongForm.patchValue({ categoryIds: selectedCategoryIds });
      }
      
          this.displayEditModal = true;

    this.singerService.getSingersNotIn(this.song.singers).subscribe({
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

      if(this.song) {
        
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
        this.loadData();
        this.displayEditModal = false;

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

    triggerFileInputEdit(): void {
    this.inputSongEdit.nativeElement.click();
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
  
}
