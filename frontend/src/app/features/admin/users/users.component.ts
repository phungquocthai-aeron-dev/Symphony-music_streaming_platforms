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
    if (confirm('Bạn có chắc chắn muốn vô hiệu hóa người dùng ' + user.fullName + '. Id:' + user.userId +  ' không?')) {
      this.authService.deleteUser(user.userId).subscribe({
        next: () => {
        this.notifyTitle = "Vo hiệu hóa tài khoản người dùng";
        this.notifyContent = "Đã vô hiệu hóa tài khoản người dùng " + user.fullName + ", id: " + user.userId + "!";
        this.isSuccess = true;
          this.loadData();

        },
        error: (err) => {
          this.notifyTitle = "Vô hiệu hóa người dùng";
          this.notifyContent = "Vô hiệu hóa tài khoản người dùng thất bại!";
          this.isSuccess = false;
        }
      });
    }
  }

  enableUser(user: UserDTO): void {
    if (confirm('Bạn có chắc chắn muốn khôi phục người dùng ' + user.fullName + '. Id:' + user.userId +  ' không?')) {
      this.authService.enable(user.userId).subscribe({
        next: () => {
        this.notifyTitle = "Khôi phục tài khoản người dùng";
        this.notifyContent = "Đã khôi phục tài khoản người dùng " + user.fullName + ", id: " + user.userId + "!";
        this.isSuccess = true;
          this.loadData();

        },
        error: (err) => {
          this.notifyTitle = "Khôi phục người dùng";
          this.notifyContent = "Khôi phục tài khoản người dùng thất bại!";
          this.isSuccess = false;
        }
      });
    }
  }

  deleteSinger(singer: SingerDTO): void {
    if (confirm('Bạn có chắc chắn muốn vô hiệu hóa ca sĩ ' + singer.stageName + '. Id:' + singer.singer_id +  ' không?')) {
      this.singerService.deleteSinger(singer.singer_id).subscribe({
        next: () => {
          this.notifyTitle = "Vô hiệu hóa ca sĩ";
          this.notifyContent = "Đã vô hiệu hóa ca sĩ " + singer.stageName + ", id: " + singer.singer_id + "!";
          this.isSuccess = true;

          this.loadData();
        },
        error: (err) => {
          this.notifyTitle = "Vô hiệu hóa ca sĩ";
          this.notifyContent = "Vô hiệu hóa ca sĩ thất bại!";
          this.isSuccess = false;
        }
      });
    }
  }

  enableSinger(singer: SingerDTO): void {
    if (confirm('Bạn có chắc chắn muốn khôi phục hóa ca sĩ ' + singer.stageName + '. Id:' + singer.singer_id +  ' không?')) {
      this.singerService.enable(singer.singer_id).subscribe({
        next: () => {
          this.notifyTitle = "Khôi phục ca sĩ";
          this.notifyContent = "Đã khôi phục ca sĩ " + singer.stageName + ", id: " + singer.singer_id + "!";
          this.isSuccess = true;

          this.loadData();
        },
        error: (err) => {
          this.notifyTitle = "Khôi phục ca sĩ";
          this.notifyContent = "Khôi phục ca sĩ thất bại!";
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

  // onDeleteUser(user: UserDTO) {
  //   this.authService.deleteUser(user.userId).subscribe({
  //     next: () => {
  //       this.notifyTitle = "Xóa tài khoản người dùng";
  //       this.notifyContent = "Đã xóa tài khoản người dùng " + user.fullName + ", id: " + user.userId + "!";
  //       this.isSuccess = true;
  //     },
  //     error: (error) => {
  //       this.notifyTitle = "Xóa tài khoản người dùng";
  //       this.notifyContent = "Lỗi xóa tài khoản người dùng!";
  //       this.isSuccess = false;
  //       console.error(error)
  //     }
  //   })
  // }

  // onDeleteSinger(singer: SingerDTO) {
  //   this.singerService.deleteSinger(singer.singer_id).subscribe({
  //     next: () => {
  //       this.notifyTitle = "Xóa ca sĩ";
  //       this.notifyContent = "Đã xóa ca sĩ " + singer.stageName + ", id: " + singer.singer_id + "!";
  //       this.isSuccess = true;
  //     },
  //     error: () => {
  //       this.notifyTitle = "Xóa ca sĩ";
  //       this.notifyContent = "Lỗi xóa ca sĩ!";
  //       this.isSuccess = false;
  //       console.error(error)
  //     }
  //   })
  // }

  onGrantSinger(user: UserDTO) {
    if (confirm('Bạn có chắc chắn muốn cấp quyền ca sĩ cho người dùng ' + user.fullName + '. Id:' + user.userId +  ' không?')) {
    this.authService.grantSinger(user.userId).subscribe({
      next: () => {
        this.notifyTitle = "Cấp quyền ca sĩ";
        this.notifyContent = "Đã quyền ca sĩ cho người dùng " + user.fullName + ", id: " + user.userId + "!";
        this.isSuccess = true;

        this.loadData();
      },
      error: (error) => {
        this.notifyTitle = "Cấp quyền ca sĩ";
        this.notifyContent = "Lỗi cấp quyền ca sĩ!";
        this.isSuccess = false;
        console.error(error)
      }
    })
  }
}
}
