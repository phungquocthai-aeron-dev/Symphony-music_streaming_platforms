import { Component, OnInit } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { environment } from '../../../../environments/environment';

interface ThemeSave {
  isDarkTheme: boolean;
}

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  private COOKIE_NAME = 'music-streaming-theme';

  pathLogoDarkTheme = environment.assetsPath + 'symphony-darktheme-icon.png';
  pathLogoLightTheme = environment.assetsPath + 'symphony-lighttheme-icon.png';
  isDarkTheme = true;
  private dataTheme: ThemeSave | null = null;

  constructor(private cookieService: CookieService) {
    const storedTheme = this.cookieService.get(this.COOKIE_NAME);
    this.dataTheme = storedTheme ? JSON.parse(storedTheme) : null;
  }

  ngOnInit(): void {
    this.loadConfig();
  }

  get assetsPath() {
    return environment.assetsPath;
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
