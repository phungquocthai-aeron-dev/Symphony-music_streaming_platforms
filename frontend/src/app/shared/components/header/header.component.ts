import { Component, ElementRef, Input, NgZone, OnInit, ViewChild } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../../core/services/auth.service';
import { UserDTO } from '../../models/User.dto';
import { NgIf } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { SingerDTO } from '../../models/Singer.dto';
import { FormsModule } from '@angular/forms';

declare var webkitSpeechRecognition: any;

interface ThemeSave {
  isDarkTheme: boolean;
  mainColor: string;
}

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  imports: [NgIf, RouterModule, FormsModule]
})

export class HeaderComponent implements OnInit {
  @ViewChild('voiceSearch') voiceSearchIcon!: ElementRef;

  private COOKIE_NAME = 'music-streaming-theme';
  search: string = '';
  recognition: any;
  isThemeMenuOpen = false;

  pathLogoDarkTheme = environment.assetsPath + 'symphony-darktheme-icon.png';
  pathLogoLightTheme = environment.assetsPath + 'symphony-lighttheme-icon.png';
  isDarkTheme = true;
  mainColor = "#ff6337";
  private dataTheme: ThemeSave | null = null;
  isLoggedIn: boolean = false;
  isListening: boolean = false;

  @Input() user!: UserDTO;
  @Input() singer!: SingerDTO;

  constructor(
    private cookieService: CookieService,
    private authService: AuthService,
    private router: Router,
    private ngZone: NgZone
  ) {
    const storedTheme = this.cookieService.get(this.COOKIE_NAME);
    this.dataTheme = storedTheme ? JSON.parse(storedTheme) : null;
    if (typeof window !== 'undefined') {
      const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;

      if (!SpeechRecognition) {
        console.error('Trình duyệt không hỗ trợ Web Speech API');
        return;
      }

      this.recognition = new SpeechRecognition();
      this.recognition.lang = 'vi-VN';
      this.recognition.continuous = false;
      this.recognition.interimResults = true;

      this.recognition.onresult = (event: any) => {
        const transcript = event.results[0][0].transcript;
        this.ngZone.run(() => {
          this.search = transcript;
        });
      };

      this.recognition.onerror = (event: any) => {
        console.error('Lỗi speech recognition:', event.error);
      };

      this.recognition.onend = () => {
        if(this.isListening) this.handleSearch();
        else this.ngZone.run(() => {this.search = "";});
        
      };
    }
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
      this.mainColor = this.dataTheme.mainColor;
      this.applyTheme();
    }
  }

  saveConfig(): void {
    this.dataTheme = { isDarkTheme: this.isDarkTheme, mainColor: this.mainColor };
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
    document.documentElement.style.setProperty('--orange-color', this.mainColor);
  }

  startListening(): void {
    this.search = "";
    if (this.recognition) {

      if(this.isListening) {
        this.recognition.stop();
        this.isListening = false;
      }
      else {
        this.recognition.start();
        this.isListening = true;
      }
    } else {
      alert('Trình duyệt không hỗ trợ nhận diện giọng nói.');
    }
  }

  selectColor(colorCode: string): void {
    this.mainColor = colorCode;
    document.documentElement.style.setProperty('--orange-color', colorCode);
    this.saveConfig();
  }

handleSearch() {
  this.search = this.search.trim();
  this.isListening = false;

  if (this.search) {
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() => {
      this.router.navigate(['/search'], { 
        queryParams: { s: this.search }
      });
    });
  }
}
}
