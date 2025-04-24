import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { SongDTO } from '../../models/Song.dto';
import { AlbumDTO } from '../../models/Album.dto';
import { SongService } from '../../../core/services/song.service';
import { AlbumService } from '../../../core/services/album.service';
import { DataShareService } from '../../../core/services/dataShare.service';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-album-card',
  templateUrl: './album-card.component.html',
  styleUrls: ['./album-card.component.css'],
  standalone: true,
  imports: [NgIf, FormsModule]
})
export class AlbumCardComponent implements OnInit {
  @Input() album!: AlbumDTO;

  @Output() albumDetail = new EventEmitter<SongDTO[]>();
  @Output() albumSelect = new EventEmitter<AlbumDTO>();
  @Output() deleted = new EventEmitter<number>();
  @Output() renamed = new EventEmitter<{ albumId: number; newName: string }>();

  songs: SongDTO[] = [];
  newAlbumName: string = '';
  selectedImage: File | null = null;
  defaultAlbumImg!: string;

  @ViewChild('deleteButtonRef', { static: false }) closeFormDelete!: ElementRef;
  @ViewChild('renameButtonRef', { static: false }) closeFormUpdate!: ElementRef;

  constructor(
    private songService: SongService,
    private albumService: AlbumService,
    private dataShareService: DataShareService
  ) {}

  ngOnInit(): void {
    if (this.album?.albumId) {
      this.defaultAlbumImg = 'http://localhost:8080/symphony/uploads' + this.album.albumImg;
      this.songService.getSongsByAlbumId(this.album.albumId).subscribe({
        next: (data) => {
          this.songs = data.result || [];
        },
        error: err => console.error('Lỗi khi lấy danh sách bài hát:', err)
      });
    }
    if(this.album) this.newAlbumName = this.album.albumName;
  }
  
  onFileImgChange(event: Event): void {
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

  selectAlbum() {
    this.albumDetail.emit(this.songs);
    this.albumSelect.emit(this.album);
    this.dataShareService.changeCurrentAlbum(this.album);
  }

  confirmDelete(): void {
    this.albumService.deleteAlbum(this.album.albumId).subscribe({
      next: () => {
        this.deleted.emit(this.album.albumId);
        this.closeFormDelete.nativeElement.click();
      },
      error: err => console.error('Lỗi khi xóa album:', err)
    });
  }

  onUpdate(): void {
    const newName = this.newAlbumName?.trim();
    if (!newName || newName === this.album.albumName) return;

    this.albumService.updateAlbum(this.album.albumId, newName, this.selectedImage).subscribe({
      next: () => {
        this.renamed.emit({ albumId: this.album.albumId, newName });
        this.closeFormUpdate.nativeElement.click();
      },
      error: err => console.error('Lỗi khi đổi tên album:', err)
    });
  }
}
