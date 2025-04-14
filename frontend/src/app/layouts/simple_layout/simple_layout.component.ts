import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from '../../shared/components/header/header.component';
import { FooterComponent } from '../../shared/components/footer/footer.component';
import { UserDTO } from '../../shared/models/User.dto';
import { SingerDTO } from '../../shared/models/Singer.dto';
import { SingerService } from '../../core/services/singer.service';
import { SongService } from '../../core/services/song.service';
import { AuthService } from '../../core/services/auth.service';
import { DataShareService } from '../../core/services/dataShare.service';

@Component({
  selector: 'app-simple-layout',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, FooterComponent],
  templateUrl: './simple_layout.component.html'
})
export class SimpleLayoutComponent implements OnInit {
    user!: UserDTO;
    singer!: SingerDTO;

    constructor(
        private authService: AuthService,
        private singerService: SingerService,
        private dataShareService: DataShareService
      ) {}

    ngOnInit(): void {
        if(this.authService.isLoggedIn()) {
              
                this.authService.getUser().subscribe({
                  next: (response) => {
                    if (response.code === 1000) {
                      this.user = response.result;
                      if(this.user.role === "SINGER") {
                        this.singerService.getSingerByUserId(this.user.userId).subscribe({
                          next: (response) => {
                            if (response.code === 1000) {
                              this.singer = response.result;
                              this.dataShareService.changeSinger(this.singer);
                            } else {
                              console.warn('Không lấy được thông tin ca sĩ:', response.message);
                            }
                          },
                          error: (err) => {
                            console.error('Lỗi khi tìm ca sĩ:', err);
                          }
                        });
                      }
          
                    } else {
                      console.warn('Không lấy được thông tin người dùng:', response.message);
                    }
                  },
                  error: (err) => {
                    console.error('Lỗi khi tìm người dùng:', err);
                  }
                })
              
            }
    }
}