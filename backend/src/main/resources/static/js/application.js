"use strict"

const $ = document.querySelector.bind(document)
const $$ = document.querySelectorAll.bind(document)

const LOCAL_STORAGE_VARIABLE_NAME = 'Music Streaming Platforms'


const range = $('#myRange')
const progressBar = $('#progressBar')

const inputVolume = $('#inputVolume')
const progressVolume = $('#progressVolume')
const audioStatus = $('#audio-status')


const audioStatusClean = $('#audio-status-clean')
const inputVolumeClean = $('#inputVolume-clean')
const progressVolumeClean = $('#progressVolume-clean')
const menuAudio = $('#menu-audio')

const musicPlaying = $('#play-music')
const audio = $('#audio')
const btnNext = $('.btn-next')
const btnRepeat = $('.btn-repeat')
const btnRandom = $('.btn-random')
const playlist = $('.playlist')
const recentSongs = $('.recentSongs')
const optionWaiting = $('.option-waiting')
const optionRecent = $('.option-recent')
const rightSideControl = $('.control-right-side')
const rightSideControlClean = $('.control-right-side-clean')

const audioDuration = $('#audio-duration')
const currentTimeAudio = $('#current-time')

const listenedForm = document.querySelector('#listenedForm')
const listenedBtn = document.querySelector('#listenedBtn')
const now = Math.floor(Date.now() / 1000)
document.querySelector('#listen_at').value = now

function getCookie(key) {
    const cookiesStr = document.cookie
    if (cookiesStr) {
        const cookiesArr = cookiesStr.split(';');
        for (let i = 0; i < cookiesArr.length; i++) {
            cookiesArr[i] = cookiesArr[i].trim()
            const cookieKey = cookiesArr[i].substring(0, cookiesArr[i].indexOf('='))
            if (cookieKey === key) {
                const value = cookiesArr[i].substring(cookiesArr[i].indexOf('=') + 1, cookiesArr[i].length)
                return value
            }
        }
    }
    return ""
}


const app = {
    songId: -1,
    currentIndex: 0,
    isPlaying: false,
    isRandom: false,
    isRepeat: false,
    isDarkTheme: true,
    inputFlag: false,
    isRecentSong: false,
    isPlaying: false,
    enoughTime: 0,
    intervalListened: '',
    config: JSON.parse(localStorage.getItem(LOCAL_STORAGE_VARIABLE_NAME)) ?? null,

    saveConfig: function () {
        this.config = {
            songId: menuAudio.dataset.songid,
            audioVolume: audio.volume,
            audioProgress: audio.currentTime,
            isRepeat: this.isRepeat,
            isRandom: this.isRandom,
            isRecentSong: this.isRecentSong,
            isPlaying: this.isPlaying
        }
        localStorage.setItem(LOCAL_STORAGE_VARIABLE_NAME, JSON.stringify(this.config))
    },

    loadConfig: async function () {
        if (this.config !== null) {
            audio.volume = this.config.audioVolume ?? 50
            this.isRandom = this.config.isRandom ?? false
            this.isRecentSong = this.config.isRecentSong ?? false
            this.isRepeat = this.config.isRepeat ?? false
            this.isPlaying = this.config.isPlaying ?? false
            this.songId = this.config.songId ?? 0

            if (this.isRandom) btnRandom.classList.toggle('audioActive')
            if (this.isRepeat) btnRepeat.classList.toggle('audioActive')

            inputVolume.value = audio.volume * 100
            inputVolumeClean.value = audio.volume * 100

            this.runAfterInput(progressVolume, inputVolume)
            this.runAfterInput(progressVolumeClean, inputVolumeClean)

            const thisApp = this
            audio.addEventListener('canplaythrough', function () {
                audioDuration.innerText = thisApp.TimeFormat(Math.floor(audio.duration))
                if (menuAudio.dataset.songid == thisApp.songId) {
                    audio.currentTime = thisApp.config.audioProgress
                } else {
                    audio.currentTime = 0
                    this.songId = menuAudio.dataset.songid
                }
            }, { once: true });

            if (this.isPlaying) {
                try {
                    await audio.play()
                } catch (errors) {
                    this.isPlaying = false
                }
            }

        }
    },

    runAfterInput: function (marker, target) {
        const value = target.value
        const max = target.max
        const width = (value / max) * 100 + '%'
        marker.style.width = width
    },

    playRandomSong: function () {
        const listSongRecommend = playlist.querySelectorAll('.container-song')
        const listSongRecommendSize = listSongRecommend.length
        let value = 1
        do {
            value = Math.floor(Math.random() * listSongRecommendSize)
        } while (value < 1 && value >= listSongRecommendSize)
        const songId = listSongRecommend[value].dataset.songid
        return songId
    },

    completedAudioProcess: function () {
        this.isPlaying = true
        if (this.isRandom) {
            window.location.href = '/song/control/play/' + this.playRandomSong()
        } else if (this.isRepeat) {
            audio.currentTime = 0
        } else {
            btnNext.click()
        }
    },

    IconVolumeStatusControl: function () {
        if (audio.volume == 0) {
            audioStatus.innerHTML = '<i class="fa-solid fa-volume-high refer"></i>'
            audioStatusClean.innerHTML = '<i class="fa-solid fa-volume-high refer"></i>'
            audio.volume = this.dataMusicVolume
        } else {
            audioStatus.innerHTML = '<i class="fa-solid fa-volume-xmark"></i>'
            audioStatusClean.innerHTML = '<i class="fa-solid fa-volume-xmark"></i>'
            this.dataMusicVolume = audio.volume
            audio.volume = 0
        }
        inputVolume.value = audio.volume * 100
        inputVolumeClean.value = audio.volume * 100
        this.runAfterInput(progressVolume, inputVolume)
        this.runAfterInput(progressVolumeClean, inputVolumeClean)
        this.runAfterInput(progressVolume, inputVolume)
        this.runAfterInput(progressVolumeClean, inputVolumeClean)
    },

    ChangeVolumeByUser: function (inputVolume, audioStatus) {
        if (inputVolume.value == 0) {
            audioStatus.innerHTML = '<i class="fa-solid fa-volume-xmark refer"></i>'
        } else {
            audioStatus.innerHTML = '<i class="fa-solid fa-volume-high refer"></i>'
        }

        const audioVolume = inputVolume.value / 100
        audio.volume = audioVolume
    },

    TimeFormat: function (seconds) {
        let minutes = 0
        let strSeconds = '00'
        let result = ''


        while (seconds >= 60) {
            seconds = seconds - 60
            minutes++
        }

        if (seconds > 9) {
            strSeconds = seconds + ''
        } else {
            strSeconds = `0${seconds}`
        }


        if (minutes > 9) {
            result = `${minutes}:${strSeconds}`
        } else {
            result = `0${minutes}:${strSeconds}`
        }

        return result
    },

    listenedSong: function () {

        listenedForm.addEventListener('submit', function (e) {
            e.preventDefault()

            const formListenData = new FormData(this)


            const xhr = new XMLHttpRequest()


            xhr.open('POST', '/listened', true)

            xhr.onload = function () {
                if (xhr.status === 200) {
                    console.log('Đã nghe nhạc!');
                } else {
                    console.error('Có lỗi xảy ra!');
                }
            };

            xhr.onerror = function () {
                console.error('Gửi thất bại!');
            };

            xhr.send(formListenData);
        })

        if (this.isPlaying && this.enoughTime < 25) {
            this.intervalListened = setInterval(() => {
                this.enoughTime++
                console.log(this.enoughTime)

                if (this.enoughTime == 25) {
                    listenedBtn.click()
                    clearInterval(this.intervalListened)
                }
            }, 1000)
        }
    },

    handleEvent: function () {
        audio.ontimeupdate = () => {
            if (audio.duration && !this.inputFlag) {
                const seekTime = audio.currentTime / audio.duration * 100
                range.value = seekTime
                currentTimeAudio.innerText = this.TimeFormat(Math.floor(audio.currentTime))
                this.runAfterInput(progressBar, range)
            }
        }

        audio.onended = () => {
            audio.onended = this.completedAudioProcess()
        }

        audioStatus.onclick = () => this.IconVolumeStatusControl()
        audioStatusClean.onclick = () => this.IconVolumeStatusControl()

        musicPlaying.onclick = () => {

            if (this.isPlaying) {
                audio.pause()
                this.isPlaying = false
                musicPlaying.innerHTML = '<i class="fas fa-play icon-play"></i>'
                clearInterval(this.intervalListened)

            } else {
                audio.play()
                this.isPlaying = true
                musicPlaying.innerHTML = '<i class="fas fa-pause icon-pause"></i>'

                if (this.isPlaying && this.enoughTime < 25) {
                    this.intervalListened = setInterval(() => {
                        this.enoughTime++
                        console.log(this.enoughTime)

                        if (this.enoughTime == 25) {
                            listenedBtn.click()
                            clearInterval(this.intervalListened)
                        }

                    }, 1000)
                }

            }

        }

        audio.onpause = () => {
            this.isPlaying = false
            musicPlaying.innerHTML = '<i class="fas fa-play icon-play"></i>'
        }

        audio.onplay = () => {
            this.isPlaying = true
            musicPlaying.innerHTML = '<i class="fas fa-pause icon-pause"></i>'
        }

        inputVolume.onchange = () => {
            this.ChangeVolumeByUser(inputVolume, audioStatus)
            this.ChangeVolumeByUser(inputVolume, audioStatusClean)
        }

        inputVolumeClean.onchange = () => {
            this.ChangeVolumeByUser(inputVolumeClean, audioStatus)
            this.ChangeVolumeByUser(inputVolumeClean, audioStatusClean)
        }

        inputVolume.oninput = () => {
            this.runAfterInput(progressVolume, inputVolume)
            this.runAfterInput(progressVolumeClean, inputVolume)
            inputVolumeClean.value = inputVolume.value
        }
        inputVolumeClean.oninput = () => {
            this.runAfterInput(progressVolumeClean, inputVolumeClean)
            this.runAfterInput(progressVolume, inputVolumeClean)
            inputVolume.value = inputVolumeClean.value
        }

        btnRepeat.onclick = () => {
            this.isRepeat = !this.isRepeat
            btnRepeat.classList.toggle('audioActive')
        }

        btnRandom.onclick = () => {
            this.isRandom = !this.isRandom
            btnRandom.classList.toggle('audioActive')
        }

        range.onchange = (e) => {
            audio.currentTime = e.target.value / 100 * audio.duration
            this.inputFlag = false
        }

        range.oninput = () => {
            this.runAfterInput(progressBar, range)
            this.inputFlag = true
        }

        optionRecent.onclick = () => {
            optionRecent.classList.add('active')
            recentSongs.classList.remove('d-none')
            optionWaiting.classList.remove('active')
            playlist.classList.add('d-none')
            this.isRecentSong = true
        }

        optionWaiting.onclick = () => {
            optionWaiting.classList.add('active')
            optionRecent.classList.remove('active')
            playlist.classList.remove('d-none')
            recentSongs.classList.add('d-none')
            this.isRecentSong = false
        }

        range.oninput()
        inputVolume.oninput()
        inputVolume.onchange()

        inputVolumeClean.oninput()
        inputVolumeClean.onchange()
        this.inputFlag = false

        rightSideControl.onclick = () => {
            $('.right-side').classList.toggle('right-side-appear')
        }
        rightSideControlClean.onclick = () => {
            $('.right-side').classList.toggle('right-side-appear')
        }

        // ĐỒNG BỘ HAI THANH ÂM LƯỢNG

        window.onbeforeunload = () => {
            this.saveConfig()
        }


        if (this.isRecentSong) optionRecent.click()

    },

    init: function () {
        this.loadConfig()
        this.listenedSong()
        this.handleEvent()
    }
}

