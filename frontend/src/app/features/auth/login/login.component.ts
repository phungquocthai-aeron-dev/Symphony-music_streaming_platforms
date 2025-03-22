import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Authentication } from '../../../shared/models/Authentication.dto';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { DataShareService } from '../../../core/services/dataShare.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})

export class LoginComponent implements OnInit {
  isSubmitted = false;
  auth = new FormGroup({
    phone: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  })

  constructor(
    private authService: AuthService,
    private router: Router,
    private dataShareService: DataShareService
  ){}

  ngOnInit(): void {
    this.dataShareService.changeTitle("Đăng nhập");
  }

  get phone() {
    return this.auth.get('phone')
  }

  get password() {
    return this.auth.get('password')
  }

  handleLogin() :void {
    if(this.phone?.hasError('required') || this.password?.hasError('required')) {
      this.isSubmitted = true
      return
    }
    const authentication: Authentication = {
      phone: String(this.phone?.value),
      password: String(this.password?.value)
    }

    this.authService.login(authentication).subscribe({
      next: (response) => {
        if (response.code === 1000 && response.result.authenticated) {
          const jwtInfo = { jwt: response.result.token };
          localStorage.setItem('SYMPHONY_USER', JSON.stringify(jwtInfo));
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        console.error('Đăng nhập thất bại', err);
      }
    });
  }
}

