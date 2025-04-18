import { Component, OnInit } from '@angular/core';
import { UserDTO } from '../../shared/models/User.dto';
import { AuthService } from '../../core/services/auth.service';
import { Router, RouterOutlet } from '@angular/router';
import { environment } from '../../../environments/environment';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-admin-layout',
  imports: [RouterOutlet, NgIf],
  templateUrl: './admin-layout.component.html',
  styleUrl: './admin-layout.component.css'
})
export class AdminLayoutComponent implements OnInit {
  admin!: UserDTO;
  logo = environment.assetsPath + 'symphony-darktheme-icon.png';

  constructor(
      private authService: AuthService,
      private router: Router,
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
  }

  logout() {
    this.authService.logout();
  }
}
