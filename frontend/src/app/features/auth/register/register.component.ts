import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, AbstractControl, ValidationErrors, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { UserRegistrationDTO } from '../../../shared/models/UserRegistration.dto';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  imports: [NgIf, ReactiveFormsModule]
})
export class RegisterComponent implements OnInit {
  isSubmitted = false;

  registerForm = new FormGroup({
    fullName: new FormControl('', Validators.required),
    birthday: new FormControl('', Validators.required),
    password: new FormControl('', [Validators.required, Validators.minLength(6)]),
    phone: new FormControl('', [Validators.required, Validators.pattern('^(03|05|07|08|09|01[2|6|8|9])+([0-9]{8})\\b$')]),
    gender: new FormControl('', Validators.required),
    agree: new FormControl('', Validators.required),
    password_confirm: new FormControl('', Validators.required),

  },
  { validators: this.passwordMatchValidator });

  isLoading = false;
  errorMessage: string | null = null;
  maxDate?: string;


  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    const today = new Date();
today.setMinutes(today.getMinutes() - today.getTimezoneOffset()); // Điều chỉnh theo múi giờ địa phương
this.maxDate = today.toISOString().split('T')[0]; 
  }

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('password_confirm')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  onSubmit() {

    if (this.registerForm.invalid) {
      this.isSubmitted = true
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    const data: UserRegistrationDTO = {
      fullName: String(this.fullName?.value),
      birthday: String(this.birthday?.value),
      password: String(this.password?.value),
      phone: String(this.phone?.value),
      gender: Number(this.gender?.value)
    };

    this.authService.register(data).subscribe({
      next: (response) => {
        this.router.navigate(['/login'])
      },
      error: (error) => {
        this.errorMessage = error.message || 'Đăng ký thất bại!'
        this.isLoading = false
      }
    })
  }

  get fullName() {
    return this.registerForm.get('fullName')
  }
  
  get birthday() {
    return this.registerForm.get('birthday')
  }
  
  get phone() {
    return this.registerForm.get('phone')
  }
  
  get password() {
    return this.registerForm.get('password')
  }
  
  get gender() {
    return this.registerForm.get('gender')
  }

  get agree() {
    return this.registerForm.get('agree')
  }

  get confirmPassword() {
    return this.registerForm.get('password_confirm')
  }

  get passwordMismatch() {
    return this.registerForm.hasError('passwordMismatch')
  }
  
}
