import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Authentication, AuthenticationResponse } from '../../../shared/models/Authentication.dto';
import { LoginService } from '../../../core/services/login.service';
import { Router } from '@angular/router';
import { ResponseData } from '../../../shared/models/ResponseData';


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
    private loginService: LoginService,
    private router: Router
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

    console.log(authentication)
    this.loginService.authenticate(authentication).subscribe(
      (response: ResponseData<AuthenticationResponse>) => {
        console.log(response);
        
        if (response.result && response.result.authenticated) {
          const jwtInfo = {
            jwt: response.result.token
          }
          localStorage.setItem('SYMPHONY_USER', JSON.stringify(jwtInfo));
          this.router.navigate(['/']);
        } else {
          console.log("Xác thực không thành công");
        }
      },
      (error) => {
        console.error('Login error:', error);
      }
    )
  }
}
