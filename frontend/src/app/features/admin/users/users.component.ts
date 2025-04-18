import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { SingerService } from '../../../core/services/singer.service';
import { DatePipe, DecimalPipe, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserDTO } from '../../../shared/models/User.dto';
import { SingerDTO } from '../../../shared/models/Singer.dto';


@Component({
  selector: 'app-users',
  imports: [NgFor, NgIf, FormsModule, DatePipe, DecimalPipe],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent implements OnInit {
  users: UserDTO[] = [];
  singers: SingerDTO[] = [];

  userPhone = "";
  stageName = "";

  constructor(
    private authService: AuthService,
    private singerService: SingerService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData(): void {
    this.loadUser();
    this.loadSinger();
  }

  loadUser() {
    this.authService.getUsers().subscribe({
      next: (data) => {
        this.users = data.result;
        console.log(this.users)
      },
      error: (error) => {
        console.error(error)
      }
    })
  }

  loadSinger() {
    this.singerService.getAllSingers().subscribe({
      next: (data) => {
        this.singers = data.result;
      },
      error: (error) => {
        console.error(error);
      }
    })
  }
  
  searchUsers(): void {
    if(this.userPhone.length > 0) {
      this.authService.findUserByPhone(this.userPhone).subscribe({
        next: (data) => {
          this.users = [];
          this.users[0] = data.result;
        }
      })
    }
  }

  searchSingers(): void {
    if(this.userPhone.length > 0) {
      this.singerService.findUserByStageName(this.stageName).subscribe({
        next: (data) => {
          this.singers = [];
          this.singers = data.result;
        }
      })
    }
  }
  
  editUser(id: string): void {
    this.router.navigate(['/users/edit', id]);
  }
  
  deleteUser(id: number): void {
    if (confirm('Bạn có chắc chắn muốn xóa không?')) {
      this.authService.deleteUser(id).subscribe({
        next: () => {
          this.loadData(); // Reload list
        },
        error: (err) => {
          console.error(err);
        }
      });
    }
  }
  
  onExportUsersExcel(): void {
    this.authService.exportUsers().subscribe({
      next: (data: Blob) => {
        const blob = new Blob([data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'danh_sach_nguoi_dung.xlsx';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  onExportSingersExcel(): void {
    this.singerService.exportSingers().subscribe({
      next: (data: Blob) => {
        const blob = new Blob([data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'danh_sach_ca_si.xlsx';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error(error);
      }
    });
  }
  
  getGenderText(gender: number): string {
    switch (gender) {
      case 0:
        return 'Nữ';
      case 1:
        return 'Nam';
      case 2:
        return 'Khác';
      default:
        return 'Không xác định';
    }
  }
  
  
}
