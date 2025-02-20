import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Authentication } from '../../../shared/models/Authentication.dto';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})

export class LoginComponent {
  isSubmitted = false;
  auth = new FormGroup({
    phone: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  })

  constructor(
    private authService: AuthService,
  ){}

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

    this.authService.login({ phone: '123456789', password: 'password' }).subscribe({
      next: (response) => console.log('Đăng nhập thành công:', response),
      error: (error) => console.error('Lỗi đăng nhập:', error.message)
    });
  }
}

