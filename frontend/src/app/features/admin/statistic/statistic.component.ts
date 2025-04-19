import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule } from '@angular/forms';
import { Chart, registerables } from 'chart.js';
import { HomeService } from '../../../core/services/home.service';
import { NgIf } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { DataShareService } from '../../../core/services/dataShare.service';

Chart.register(...registerables);

@Component({
  selector: 'app-statistic',
  imports: [NgIf, FormsModule],
  templateUrl: './statistic.component.html',
  styleUrl: './statistic.component.css'
})
export class StatisticComponent implements OnInit {
  year: number = new Date().getFullYear();
  listenData: number[] = [];
  chart: any;

  revenueYear: number = new Date().getFullYear();
  revenueData: number[] = [];
  revenueChart: any;

  constructor(
    private homeService: HomeService,
    private authService: AuthService,
    private dataShareService: DataShareService
  ) {}

  ngOnInit(): void {
    this.dataShareService.changeLeftSideInfo("Overview");
    this.dataShareService.changeTitle("Tổng quan");
    const year: number = new Date().getFullYear();
    this.loadListenStatistics(year);
    this.loadRevenueStatistics(this.revenueYear);
  }

  loadListenStatistics(year: number): void {
    this.listenData = [];
    
    // Lấy dữ liệu cho 12 tháng trong năm
    for (let month = 1; month <= 12; month++) {
      this.homeService.thongKeLuotNgheTheoThang(month, year).subscribe({
        next: (response) => {
          const data = response.result
          this.listenData[month - 1] = data.soLuotNghe;
          
          // Khi đã có đủ dữ liệu cho 12 tháng, vẽ biểu đồ
          if (this.listenData.filter(item => item !== undefined).length === 12) {
            this.createChart();
          }
        },
        error: (error) => {
          console.error(`Lỗi khi lấy dữ liệu tháng ${month}:`, error);
          this.listenData[month - 1] = 0;
        }
      });
    }
  }

  onListenStatisticSubmit(): void {
    if(this.year > 0) {
      this.loadListenStatistics(this.year);
    }
  }

  createChart(): void {
    if (this.chart) {
      this.chart.destroy();
    }
  
    const ctx = document.getElementById('listenChart') as HTMLCanvasElement;
    this.chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 
                'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'],
        datasets: [
          {
            label: 'Lượt nghe',
            data: this.listenData,
            borderColor: '#2196F3',
            backgroundColor: 'rgba(33, 150, 243, 0.1)',
            borderWidth: 3,
            tension: 0.3,
            fill: true
          }
        ]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 10
            }
          }
        }
      }
    });
  }

  loadRevenueStatistics(year: number): void {
    this.revenueData = [];
    
    for (let month = 1; month <= 12; month++) {
      this.homeService.revenueStatisticByMonth(month, year).subscribe({
        next: (response) => {
          const data = response.result
          this.revenueData[month - 1] = data.revenue;
          
          if (this.revenueData.filter(item => item !== undefined).length === 12) {
            this.createRevenueChart();
          }
        },
        error: (error) => {
          console.error(`Lỗi khi lấy dữ liệu tháng ${month}:`, error);
          this.revenueData[month - 1] = 0;
        }
      });
    }
  }

  onRevenueStatisticSubmit(): void {
    if(this.revenueYear > 0) {
      this.loadRevenueStatistics(this.revenueYear);
    }
  }

  createRevenueChart(): void {
    if (this.revenueChart) {
      this.revenueChart.destroy();
    }
  
    const ctx = document.getElementById('revenueChart') as HTMLCanvasElement;
    this.revenueChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 
                'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'],
        datasets: [
          {
            label: 'Doanh thu',
            data: this.revenueData,
            borderColor: '#00d558',
            backgroundColor: 'rgba(33, 243, 124, 0.1)',
            borderWidth: 3,
            tension: 0.3,
            fill: true
          }
        ]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 10
            }
          }
        }
      }
    });
  }

  exportListenStatisticExcel(): void {
    if(this.year) {
      this.authService.exportListenStatistic(this.year).subscribe((blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'thong_ke_luot_nghe.xlsx';
        a.click();
        window.URL.revokeObjectURL(url);
      });
    }
    
  }

  exportRevenueStatisticExcel(): void {
    if(this.revenueYear) {
      this.authService.exportRevenueStatistic(this.revenueYear).subscribe((blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'thong_ke_doanh_thu.xlsx';
        a.click();
        window.URL.revokeObjectURL(url);
      });
    }
    
  }
}
