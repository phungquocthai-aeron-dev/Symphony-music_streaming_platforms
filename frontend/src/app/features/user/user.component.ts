import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { SingerService } from '../../core/services/singer.service';
import { AuthService } from '../../core/services/auth.service';
import { DataShareService } from '../../core/services/dataShare.service';
import { UserDTO } from '../../shared/models/User.dto';
import { SingerDTO } from '../../shared/models/Singer.dto';
import { NgIf } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-user',
  imports: [NgIf, ReactiveFormsModule],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})
export class UserComponent implements OnInit {
  @ViewChild('inputAvatar', { static: false }) inputAvatar!: ElementRef<HTMLInputElement>;
  @ViewChild('close_form',  { static: false }) closeForm!: ElementRef;

  user!: UserDTO;
  singer: SingerDTO | null = null;
  isLoaded = false;
  defaultUserImg:string = "http://localhost:8080/symphony/uploads/images/other/no-img.png";


  avatarFile?: File | null;
  
  userForm = new FormGroup({
    fullName: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
    newPassword: new FormControl(''),
    password_confirm: new FormControl('', Validators.required),
    phone: new FormControl('', Validators.required),
    birthday: new FormControl('', Validators.required),
    stageName: new FormControl(''),         
    singer_id: new FormControl(''),        
    gender: new FormControl('', Validators.required),
    avatar: new FormControl(''),             
    id: new FormControl('', Validators.required),
  });

  constructor(
    private singerService: SingerService,
    private authService: AuthService,
    private dataShareService: DataShareService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {

    this.authService.getUser().subscribe({
      next: (data) => {
        this.user = data.result;
        this.dataShareService.changeTitle(this.user.fullName);

        this.defaultUserImg = "http://localhost:8080/symphony/uploads" + this.user.avatar;
        this.fillFormData();

        if(this.user.role === "SINGER") {
          this.singerService.getSingerByUserId(this.user.userId).subscribe({
            next: (data) => {
              this.singer = data.result;
              this.fillFormDataRoleSinger();
            },
            error: (error) => {
              console.error(error);
            }
          })
        }
      },
      error: (error) => {
        console.error(error);
      }
    })
  }

  fillFormData(): void {
    this.userForm.patchValue({
      fullName: this.user.fullName,
      id: this.user.userId.toString(),
      avatar: this.user.avatar,
      gender: this.user.gender.toString(),
      birthday: this.user.birthday,
      phone: this.user.phone
    })
  }

  fillFormDataRoleSinger(): void {

    if(this.singer) {
      this.userForm.patchValue({
        stageName: this.singer.stageName,
        singer_id: this.singer.singer_id.toString()
      })
    }
  }

  onSubmit(): void {
    console.log("A")
    if (this.userForm.invalid) {
      Object.keys(this.userForm.controls).forEach(field => {
        const control = this.userForm.get(field);
        control?.markAsTouched({ onlySelf: true });
      });
      return;
    }

    const password = this.userForm.value.password || '';
    const passwordConfirm = this.userForm.value.password_confirm || '';

    if (password !== passwordConfirm) {
      console.log(password)
      console.log(passwordConfirm)
      return;
    }
  
    const formData = new FormData();
    formData.append('fullName', this.userForm.value.fullName || '');
    formData.append('password', this.userForm.value.password || '');
    formData.append('password_confirm', this.userForm.value.password_confirm || '');
    formData.append('newPassword', this.userForm.value.newPassword || '');
    formData.append('phone', this.userForm.value.phone || '');
    formData.append('birthday', this.userForm.value.birthday || '');
    formData.append('gender', this.userForm.value.gender || '');
    formData.append('id', this.userForm.value.id || '');
  
    if (this.userForm.value.stageName) {
      formData.append('stageName', this.userForm.value.stageName);
    }
  
    if (this.userForm.value.singer_id) {
      formData.append('singer_id', this.userForm.value.singer_id);
    }
  
    if (this.avatarFile) {
      formData.append('avatarFile', this.avatarFile);
    }
  
    this.isLoaded = false;
  
    this.authService.updateUser(formData).subscribe({
      next: (response) => {
        console.log('Cập nhật thành công:', response);
        

        if (this.userForm.value.newPassword && this.userForm.value.newPassword.length > 0) {
          this.isLoaded = true;
          this.closeForm.nativeElement.click();
          this.userForm.reset();
          this.avatarFile = null;
          this.authService.logout();
        }
        else {
          this.isLoaded = true;
          this.closeForm.nativeElement.click();
          this.userForm.reset();
          this.avatarFile = null;
          this.loadData();
        }
      },
      error: (error) => {
        console.error('Lỗi cập nhật:', error);
        this.isLoaded = true;
      }
    });
  }

  onFileChange(event: any) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    
    if (!file) {
      return;
    }
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.defaultUserImg = e.target.result;
    };
    
    reader.readAsDataURL(file);
  }

  triggerFileInput(): void {
    this.inputAvatar.nativeElement.click();
  }
  

}
