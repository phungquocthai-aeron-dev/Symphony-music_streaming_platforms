function TopSong(song, singer, image, dataViews) {
    this.song = song;
    this.singer = singer;
    this.image = image;
    this.dataViews = dataViews

    this.DeepCopy = (other) => {
        if (other instanceof TopSong) {
            this.song = other.song
            this.singer = other.singer
            this.image = other.image
            this.dataViews = other.dataViews
        }
    }
}

const ChartRanking = {
    dataTop1: undefined,
    dataTop2: undefined,
    dataTop3: undefined,
    lineChartRanking: undefined,
    isDarkTheme: true,
    xValues: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23],

    InitRanking: function (topSong1 = null, topSong2 = null, topSong3 = null) {
        if (topSong1 != null && topSong2 != null && topSong3 != null) {
            if (topSong1 instanceof TopSong && topSong2 instanceof TopSong && topSong3 instanceof TopSong) {
                let top1 = 0
                if (topSong1.dataViews[topSong1.dataViews.length - 1] >= topSong2.dataViews[topSong2.dataViews.length - 1] && topSong1.dataViews[topSong1.dataViews.length - 1] >= topSong3.dataViews[topSong3.dataViews.length - 1]) {
                    this.dataTop1 = topSong1
                    top1 = 1
                } else if (topSong2.dataViews[topSong2.dataViews.length - 1] >= topSong1.dataViews[topSong1.dataViews.length - 1] && topSong2.dataViews[topSong2.dataViews.length - 1] >= topSong3.dataViews[topSong3.dataViews.length - 1]) {
                    this.dataTop1 = topSong2
                    top1 = 2
                } else if (topSong3.dataViews[topSong3.dataViews.length - 1] >= topSong1.dataViews[topSong1.dataViews.length - 1] && topSong3.dataViews[topSong3.dataViews.length - 1] >= topSong2.dataViews[topSong2.dataViews.length - 1]) {
                    this.dataTop1 = topSong3
                    top1 = 3
                }

                if (top1 == 1) {
                    if (topSong2.dataViews[topSong2.dataViews.length - 1] >= topSong3.dataViews[topSong3.dataViews.length - 1]) {
                        this.dataTop2 = topSong2
                        this.dataTop3 = topSong3
                    } else {
                        this.dataTop2 = topSong3
                        this.dataTop3 = topSong2
                    }
                } else if (top1 == 2) {
                    if (topSong1.dataViews[topSong1.dataViews.length - 1] >= topSong3.dataViews[topSong3.dataViews.length - 1]) {
                        this.dataTop2 = topSong1
                        this.dataTop3 = topSong3
                    } else {
                        this.dataTop2 = topSong3
                        this.dataTop3 = topSong1
                    }
                } else {
                    if (topSong2.dataViews[topSong2.dataViews.length - 1] <= topSong1.dataViews[topSong1.dataViews.length - 1]) {
                        this.dataTop2 = topSong1
                        this.dataTop3 = topSong2
                    } else {
                        this.dataTop2 = topSong2
                        this.dataTop3 = topSong1
                    }
                }

                this.makeXColumn()
            }
        } else {
            console.error("Ranking Error!")
        }
    },

    makeXColumn: function () {
        const DataHour = []
        let thisHour = (new Date()).getHours()
        let item
        for (i = 0; i < 24; i++) {
            thisHour--
            if (thisHour < 0) thisHour = 23

            if (thisHour % 2 != 0) {
                if (Math.floor(thisHour / 10) >= 1) {
                    item = `${thisHour}:00`
                } else item = `0${thisHour}:00`
            } else {
                item = ''
            }

            DataHour[i] = item

        }

        this.xValues = DataHour.reverse()
    },

    DrawLineChart: function (lineChartId) {
        const thisChart = this
        this.lineChartRanking = new Chart(lineChartId, {
            type: 'line',
            data: {
                labels: thisChart.xValues,
                datasets: [{
                    label: thisChart.dataTop1.song,
                    data: thisChart.dataTop1.dataViews,
                    borderColor: '#0064f1',
                    pointBackgroundColor: '#ffff',
                    tension: 0.4,
                    borderWidth: 2,
                    pointBorderWidth: 3,
                    pointHitRadius: 8,
                    pointHoverRadius: 8,
                    pointRadius: 4,
                    song: thisChart.dataTop1.song,
                    singer: thisChart.dataTop1.singer,
                    image: thisChart.dataTop1.image
                },
                {
                    label: thisChart.dataTop2.song,
                    data: thisChart.dataTop2.dataViews,
                    borderColor: '#00d558',
                    pointBackgroundColor: '#ffff',
                    tension: 0.4,
                    borderWidth: 2,
                    pointBorderWidth: 3,
                    pointHitRadius: 8,
                    pointHoverRadius: 8,
                    pointRadius: 4,
                    song: thisChart.dataTop2.song,
                    singer: thisChart.dataTop2.singer,
                    image: thisChart.dataTop2.image
                },
                {
                    label: thisChart.dataTop3.song,
                    data: thisChart.dataTop3.dataViews,
                    borderColor: '#ff0068',
                    pointBackgroundColor: '#ffff',
                    tension: 0.4,
                    borderWidth: 2,
                    pointBorderWidth: 3,
                    pointHitRadius: 8,
                    pointHoverRadius: 8,
                    pointRadius: 4,
                    song: thisChart.dataTop3.song,
                    singer: thisChart.dataTop3.singer,
                    image: thisChart.dataTop3.image
                }
                ]
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
                                family: 'Montserrat, Open Sans, Inter, sans-serif',
                            },
                            color: function () {
                                return window.getComputedStyle(document.documentElement).getPropertyValue('--text_dark_theme-color')
                            },
                            textAlign: 'center'
                        },
                    },
                    tooltip: {
                        usePointStyle: true,
                        displayColor: false,
                        yAlign: 'bottom',
                        backgroundColor: function (tooltipItem) {
                            tooltipColor = tooltipItem.tooltip.labelColors[0].borderColor
                            return tooltipColor
                        },
                        callbacks: {
                            title: function (context) {
                                return context[0].dataset.label
                            },
                            labelPointStyle: function (context) {
                                const img = new Image(25, 25)
                                img.src = context.dataset.image
                                return {
                                    pointStyle: img
                                }
                            },
                            label: function (context) {
                                return `    ${context.dataset.singer}`
                            }
                        }
                    }
                }
            },

        })
    },

    UpdateAllChart: function () {
        this.lineChartRanking.update()
    }
}
