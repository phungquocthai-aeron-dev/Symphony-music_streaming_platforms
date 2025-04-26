import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, AbstractControl, ValidationErrors, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { UserRegistrationDTO } from '../../../shared/models/UserRegistration.dto';
import { NgFor, NgIf } from '@angular/common';
import { DataShareService } from '../../../core/services/dataShare.service';

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
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\$%\\^&\\*])[A-Za-z\\d!@#\\$%\\^&\\*]{8,}$')
    ]),
    phone: new FormControl('', [Validators.required, Validators.pattern('^(03|05|07|08|09|01[2|6|8|9])+([0-9]{8})\\b$')]),
    gender: new FormControl('', Validators.required),
    agree: new FormControl('', Validators.required),
    password_confirm: new FormControl('', Validators.required),

  },
  { validators: this.passwordMatchValidator });

  isLoading = false;
  errorMessage: string | null = null;
  maxDate?: string;


  constructor(
    private authService: AuthService, 
    private router: Router,
    private dataShareService: DataShareService) {}

  ngOnInit() {
    this.dataShareService.changeTitle("Đăng ký");

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
        console.error(error)
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
  
  // Thêm hàm này vào RegisterComponent
getFormValidationErrors(): { field: string; errors: string[] }[] {
  const validationErrors: { field: string; errors: string[] }[] = [];
  
  // Hàm helper để lấy tên hiển thị cho từng field
  const getFieldDisplayName = (fieldName: string): string => {
    const displayNames: { [key: string]: string } = {
      'fullName': 'Họ và tên',
      'birthday': 'Ngày sinh',
      'password': 'Mật khẩu',
      'password_confirm': 'Xác nhận mật khẩu',
      'phone': 'Số điện thoại',
      'gender': 'Giới tính',
      'agree': 'Đồng ý điều khoản'
    };
    return displayNames[fieldName] || fieldName;
  };

  // Kiểm tra lỗi ở cấp Form
  if (this.registerForm.errors) {
    if (this.registerForm.errors['passwordMismatch']) {
      validationErrors.push({
        field: 'Form',
        errors: ['Mật khẩu và xác nhận mật khẩu không khớp']
      });
    }
  }

  // Kiểm tra lỗi ở từng FormControl
  Object.keys(this.registerForm.controls).forEach(key => {
    const control = this.registerForm.get(key);
    if (control && control.errors && (control.dirty || control.touched || this.isSubmitted)) {
      const fieldErrors: string[] = [];
      const fieldName = getFieldDisplayName(key);
      
      Object.keys(control.errors).forEach(errorKey => {
        switch (errorKey) {
          case 'required':
            fieldErrors.push(`${fieldName} là bắt buộc`);
            break;
          case 'minlength':
            fieldErrors.push(`${fieldName} phải có ít nhất ${control.errors?.[errorKey].requiredLength} ký tự`);
            break;
          case 'pattern':
            fieldErrors.push(`${fieldName} không đúng định dạng`);
            break;
          default:
            fieldErrors.push(`${fieldName} không hợp lệ (${errorKey})`);
            break;
        }
      });
      
      if (fieldErrors.length > 0) {
        validationErrors.push({
          field: key,
          errors: fieldErrors
        });
      }
    }
  });
  
  return validationErrors;
}

hasError(controlName: string, errorName: string): boolean {
  const control = this.registerForm.get(controlName);
  return control ? 
    (control.dirty || control.touched || this.isSubmitted) && 
    control.hasError(errorName) : 
    false;
}

getErrorMessage(controlName: string): string {
  const control = this.registerForm.get(controlName);
  if (!control || !control.errors || (!control.dirty && !control.touched && !this.isSubmitted)) {
    return '';
  }
  
  const errors = control.errors;
  if (errors['required']) {
    return 'Trường này là bắt buộc';
  }
  if (errors['minlength']) {
    return `Tối thiểu ${errors['minlength'].requiredLength} ký tự`;
  }
  if (errors['pattern']) {
    return 'Giá trị không đúng định dạng';
  }
  
  return 'Trường này không hợp lệ';
}
}
