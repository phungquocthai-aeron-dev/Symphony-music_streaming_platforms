import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { SingerService } from '../../../core/services/singer.service';
import { CommonModule, DatePipe, DecimalPipe, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserDTO } from '../../../shared/models/User.dto';
import { SingerDTO } from '../../../shared/models/Singer.dto';
import { DataShareService } from '../../../core/services/dataShare.service';
import { error } from 'console';


@Component({
  selector: 'app-users',
  imports: [NgFor, NgIf, FormsModule, DatePipe, DecimalPipe, CommonModule],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent implements OnInit {
  users: UserDTO[] = [];
  singers: SingerDTO[] = [];

  userPhone = "";
  stageName = "";

  notifyContent = "";
  notifyTitle = "";
  isSuccess = true;

  constructor(
    private authService: AuthService,
    private singerService: SingerService,
    private router: Router,
    private dataShareService: DataShareService
  ) {}

  ngOnInit() {
    this.dataShareService.changeLeftSideInfo("Users");
    this.dataShareService.changeTitle("Quản lý người dùng");
    this.loadData();

  }

  loadData(): void {
    setTimeout(() => {
      this.clearNotify();
    }, 3000);

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
        }, 
        error: () => {
          this.users = [];
        }
      })
    }
  }

  searchSingers(): void {
    if(this.stageName.length > 0) {
      this.singerService.findUserByStageName(this.stageName).subscribe({
        next: (data) => {
          this.singers = [];
          this.singers = data.result;
        },
        error: () => {
          this.singers = []
        }
      })
    }
  }
  
  editUser(id: string): void {
    this.router.navigate(['/users/edit', id]);
  }
  
  deleteUser(user: UserDTO): void {
    if (confirm('Bạn có chắc chắn muốn xóa người dùng ' + user.fullName + '. Id:' + user.userId +  ' không?')) {
      this.authService.deleteUser(user.userId).subscribe({
        next: () => {
          this.loadData();
          this.notifyTitle = "Xóa người dùng";
          this.notifyContent = "Đã xóa tài khoản người dùng!";
          this.isSuccess = true;
        },
        error: (err) => {
          this.notifyTitle = "Xóa người dùng";
          this.notifyContent = "Xóa tài khoản người dùng thất bại!";
          this.isSuccess = false;
        }
      });
    }
  }

  deleteSinger(singer: SingerDTO): void {
    if (confirm('Bạn có chắc chắn muốn xóa ca sĩ ' + singer.stageName + '. Id:' + singer.singer_id +  ' không?')) {
      this.singerService.deleteSinger(singer.singer_id).subscribe({
        next: () => {
          this.loadData();
          this.notifyTitle = "Xóa ca sĩ";
          this.notifyContent = "Đã xóa quyền ca sĩ!";
          this.isSuccess = true;
        },
        error: (err) => {
          this.notifyTitle = "Xóa ca sĩ";
          this.notifyContent = "Xóa ca sĩ thất bại!";
          this.isSuccess = false;
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
  
  
  clearNotify() {
    this.notifyTitle = '';
    this.notifyContent = '';
  }
}
