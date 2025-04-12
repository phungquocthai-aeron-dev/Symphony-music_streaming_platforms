import { Component, Input, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { Chart } from 'chart.js/auto';
import { TopSongDTO } from '../../models/TopSong.dto';
import { ListeningStatsDTO } from '../../models/ListeningStats.dto';

@Component({
  selector: 'app-line-chart',
  templateUrl: './line-chart.component.html',
  styleUrl: './line-chart.component.css'
})
export class LineChartComponent implements OnInit, AfterViewInit {
  @ViewChild('lineChart') lineChartRef!: ElementRef;

  @Input() topSong1!: TopSongDTO;
  @Input() topSong2!: TopSongDTO;
  @Input() topSong3!: TopSongDTO;

  @Input() topSong1Stats!: ListeningStatsDTO[];
  @Input() topSong2Stats!: ListeningStatsDTO[];
  @Input() topSong3Stats!: ListeningStatsDTO[];

  chart: any;
  xValues: string[] = [];

  constructor() {}

  ngOnInit(): void {
    this.makeXColumn();
  }

  ngAfterViewInit(): void {
    this.drawChart();
  }

  makeXColumn() {
    const DataHour: string[] = [];
    let thisHour = new Date().getHours();
    for (let i = 0; i < 24; i++) {
      thisHour--;
      if (thisHour < 0) thisHour = 23;
      DataHour[i] = thisHour % 2 !== 0 ? `${thisHour.toString().padStart(2, '0')}:00` : '';
    }
    this.xValues = DataHour.reverse();
  }

  drawChart() {
    const ctx = this.lineChartRef.nativeElement.getContext('2d');
    this.chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: this.xValues,
        datasets: [
          {
            label: this.topSong1.songName,
            data: this.generateLast24HourData(this.topSong1Stats),
            borderColor: '#0064f1',
            pointBackgroundColor: '#fff',
            tension: 0.4,
            pointRadius: 4,
            pointBorderWidth: 3,
            pointHitRadius: 8,
            pointHoverRadius: 8,
            // song: this.topSong1.songName,
            // singer: this.topSong1.singers.map(singer => singer.stageName).join(', '),
            // image: 'http://localhost:8080/symphony/uploads' + this.topSong1.song_img
          },
          {
            label: this.topSong2.songName,
            data: this.generateLast24HourData(this.topSong2Stats),
            borderColor: '#00d558',
            pointBackgroundColor: '#fff',
            tension: 0.4,
            pointRadius: 4,
            pointBorderWidth: 3,
            pointHitRadius: 8,
            pointHoverRadius: 8,
            // song: this.topSong2.songName,
            // singer: this.topSong2.singers.map(singer => singer.stageName).join(', '),
            // image: 'http://localhost:8080/symphony/uploads' + this.topSong2.song_img
          },
          {
            label: this.topSong3.songName,
            data: this.generateLast24HourData(this.topSong3Stats),
            borderColor: '#ff0068',
            pointBackgroundColor: '#fff',
            tension: 0.4,
            pointRadius: 4,
            pointBorderWidth: 3,
            pointHitRadius: 8,
            pointHoverRadius: 8,
            // song: this.topSong3.songName,
            // singer: this.topSong3.singers.map(singer => singer.stageName).join(', '),
            // image: 'http://localhost:8080/symphony/uploads' + this.topSong3.song_img
          }
        ] as any
      },
      options: {
        responsive: true,
                scales: {
                    x: {
                        ticks: {
                            font: {
                                size: 10,
                                family: 'Montserrat, Open Sans, Inter, sans-serif'
                            },
                            color: function () {
                                return window.getComputedStyle(document.documentElement).getPropertyValue('--text_dark_theme-color')
                            }
                        }
                    },
                    y: {
                        grid: {
                            color: '#65606d',
                        },
                        border: {
                            dash: [10, 10]
                        },
                        ticks: {
                            display: false
                        }
                    },

                },
        plugins: {
          legend: {
            display: true,
            position: 'top',
            labels: {
              font: {
                size: 12,
                family: 'Montserrat, Open Sans, Inter, sans-serif'
              }
            }
          },
        }        
      }
    });
  }

  updateChart() {
    if (this.chart) {
      this.chart.update();
    }
  }

  generateLast24HourData(stats: ListeningStatsDTO[]): number[] {
    const now = new Date();
    let currentHour = now.getHours(); // số giờ hiện tại (0-23)

    const result: number[] = [];
    const hourMap = new Map<number, number>();

    // Tạo map dữ liệu: key là giờ, value là số lượt nghe
    for (const stat of stats) {
      // Chỉ lấy bản ghi của hôm nay
      const statDate = new Date(stat.listen_date);
      if (statDate.toDateString() === now.toDateString()) {
        hourMap.set(stat.hour, stat.total_listens_per_hour);
      }
    }

    // Lùi 24 giờ từ hiện tại, nếu không có dữ liệu thì gán 0
    for (let i = 0; i < 24; i++) {
      currentHour--;
      if (currentHour < 0) currentHour = 23;

      const listens = hourMap.get(currentHour) ?? 0;
      result.push(listens);
    }

    return result.reverse(); // đảo lại cho đúng thứ tự từ cũ -> mới
  }
}
