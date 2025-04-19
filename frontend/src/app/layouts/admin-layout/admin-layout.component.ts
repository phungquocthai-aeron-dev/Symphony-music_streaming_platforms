import { Component, OnInit } from '@angular/core';
import { UserDTO } from '../../shared/models/User.dto';
import { AuthService } from '../../core/services/auth.service';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { environment } from '../../../environments/environment';
import { NgIf } from '@angular/common';
import { DataShareService } from '../../core/services/dataShare.service';

@Component({
  selector: 'app-admin-layout',
  imports: [RouterOutlet, NgIf, RouterModule],
  templateUrl: './admin-layout.component.html',
  styleUrl: './admin-layout.component.css'
})
export class AdminLayoutComponent implements OnInit {
  admin!: UserDTO;
  logo = environment.assetsPath + 'symphony-lighttheme-icon.png';
  currentPage = "";

  constructor(
      private authService: AuthService,
      private router: Router,
      private dataShareService: DataShareService
  ) {}

  ngOnInit(): void {
      this.authService.getUser().subscribe({
        next: (data) => {
          this.admin = data.result;
          if(this.admin.role !== "ADMIN") this.router.navigate(['/home']);
        },
        error: (error) => {
          console.error(error);
        }
      })

      this.dataShareService.leftSideInfo.subscribe(data => {
        if (data) {
          this.currentPage = data;
        }
      });
  }

  activeItem(item: string): boolean {
    return item === this.currentPage;
  }

  logout() {
    this.authService.logout();
  }
}
