import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { VNPayService } from '../../core/services/vnpay.service';
import { Vip, VipService } from '../../core/services/vip.services';
import { NgClass, NgFor } from '@angular/common';
import { CommonModule } from '@angular/common';
import { DataShareService } from '../../core/services/dataShare.service';


@Component({
  selector: 'app-upgrade',
  imports: [NgFor, CommonModule, NgClass],
  templateUrl: './upgrade.component.html',
  styleUrl: './upgrade.component.css'
})
export class UpgradeComponent implements OnInit {
  vips: Vip[] = [];
  indexColor = 0;

  constructor(
    private authService: AuthService,
    private vnPayService: VNPayService,
    private vipService: VipService,
        private dataShareService: DataShareService
  ) {}
  
  ngOnInit(): void {
    this.dataShareService.changeTitle("Nâng cấp");

      this.vipService.getAllVipPackages().subscribe({
        next: (data) => {
          this.vips = data.result;
        },
        error: () => {
          console.error("Hong có vip gì hết!");
        }
      })
  }

  selectVipPackage(vip: Vip) {
    if(!this.authService.isLoggedIn()) {
      alert("Vui lòng đăng nhập để nâng cấp tài khoản!")
    } else {

      this.vnPayService.createPayment(vip.price, this.authService.getUserInfo().userId, vip.vip_id, ).subscribe({
        next: (res) => {
          const paymentUrl = res.result;
          window.location.href = paymentUrl;
        }, 
        error: (err) => {
          console.error('Lỗi tạo thanh toán:', err);
        }
      })
    }
  }

  parseDescription(desc: string): string[] {
    // Tách theo 2 hoặc nhiều khoảng trắng
    return desc?.split(/\s{2,}/).map(line => line.trim()) ?? [];
  }

  bgColor(): string {
    let result = "bg-pink_strong-color";
    if(this.indexColor === 0) result = "bg-yellow-color";
    if(this.indexColor === 1) result = "bg-green-color";

    this.indexColor = this.indexColor + 1;
    if(this.indexColor === 3) this.indexColor = 0;

    return result;
  }

  textColor(): string {
    if(this.indexColor === 0) return "text-yellow";
    if(this.indexColor === 1) return "text-green";
    else return "text-pink_strong";
    
  }
}
