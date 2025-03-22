import { Component, Input, OnInit } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../../core/services/auth.service';
import { UserDTO } from '../../models/User.dto';
import { NgIf } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SingerDTO } from '../../models/Singer.dto';

interface ThemeSave {
  isDarkTheme: boolean;
}

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  imports: [NgIf, RouterModule]
})
export class HeaderComponent implements OnInit {
  private COOKIE_NAME = 'music-streaming-theme';

  pathLogoDarkTheme = environment.assetsPath + 'symphony-darktheme-icon.png';
  pathLogoLightTheme = environment.assetsPath + 'symphony-lighttheme-icon.png';
  isDarkTheme = true;
  private dataTheme: ThemeSave | null = null;
  isLoggedIn: boolean = false;

  @Input() user!: UserDTO;
  @Input() singer!: SingerDTO;

  constructor(
    private cookieService: CookieService,
    private authService: AuthService
  ) {
    const storedTheme = this.cookieService.get(this.COOKIE_NAME);
    this.dataTheme = storedTheme ? JSON.parse(storedTheme) : null;
  }

  ngOnInit(): void {
    this.loadConfig();
    this.isLoggedIn = this.authService.isLoggedIn();
  }

  get assetsPath() {
    return environment.assetsPath;
  }

  logout() {
    this.authService.logout();
  }

  loadConfig(): void {
    if (this.dataTheme) {
      this.isDarkTheme = this.dataTheme.isDarkTheme;
      this.applyTheme();
    }
  }

  saveConfig(): void {
    this.dataTheme = { isDarkTheme: this.isDarkTheme };
    this.cookieService.set(this.COOKIE_NAME, JSON.stringify(this.dataTheme), { expires: 30, path: '/' }); // Lưu 30 ngày
  }

  ChangeTheme(): void {
    this.isDarkTheme = !this.isDarkTheme;
    this.applyTheme();
    this.saveConfig();
  }

  private applyTheme(): void {
    if (this.isDarkTheme) {
      document.documentElement.style.setProperty('--white-color', '#0a0a0a');
      document.documentElement.style.setProperty('--black-color', '#ffff');
      document.documentElement.style.setProperty('--text_light_theme-color', '#ffff');
      document.documentElement.style.setProperty('--text_dark_theme-color', '#55555');
      document.documentElement.style.setProperty('--dark_grey-color', '#d9dce1');
      document.documentElement.style.setProperty('--light_grey-color', '#1e1e1e');
    } else {
      document.documentElement.style.setProperty('--black-color', '#0a0a0a');
      document.documentElement.style.setProperty('--white-color', '#ffff');
      document.documentElement.style.setProperty('--text_light_theme-color', '#5555');
      document.documentElement.style.setProperty('--text_dark_theme-color', '#ffff');
      document.documentElement.style.setProperty('--dark_grey-color', '#1e1e1e');
      document.documentElement.style.setProperty('--light_grey-color', '#d9dce1');
    }
  }
}
