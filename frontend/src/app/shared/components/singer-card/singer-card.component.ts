import { Component, Input, OnInit } from '@angular/core';
import { SingerDTO } from '../../models/Singer.dto';
import { RouterModule } from '@angular/router';
import { UserDTO } from '../../models/User.dto';
import { AuthService } from '../../../core/services/auth.service';
import { ResponseData } from '../../models/ResponseData';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-singer-card',
  imports: [RouterModule, NgIf],
  templateUrl: './singer-card.component.html',
  styleUrl: './singer-card.component.css'
})
export class SingerCardComponent implements OnInit {
  @Input() singer!: SingerDTO;
  user!: UserDTO;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
      if(this.singer) {
        this.authService.getUserBySingerId(this.singer.singer_id).subscribe({
          next: (response: ResponseData<UserDTO>) => {
            this.user = response.result;
          },
          error: (error) => {
            console.error(error)
          }
        });
      }
  }
}
