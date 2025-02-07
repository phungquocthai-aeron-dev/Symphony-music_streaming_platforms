// Đặt trong những trang không có thanh phát nhạc

const changeTheme = document.querySelector('.change_theme')
const changeThemeClean = document.querySelector('.change_theme-clean')
const LOCAL_STORAGE_VARIABLE_THEME = 'Music Streaming Platforms Theme'

const Theme = {
    pathLogoDarkTheme: '',
    pathLogoLightTheme: '',
    isDarkTheme: true,
    dataTheme: JSON.parse(localStorage.getItem(LOCAL_STORAGE_VARIABLE_THEME)) ?? null,

    loadConfig: function () {
        if (this.dataTheme != null) {
            this.isDarkTheme = this.dataTheme.isDarkTheme
        }
    },

    saveConfig: function () {
        this.dataTheme = {
            isDarkTheme: this.isDarkTheme
        }
        localStorage.setItem(LOCAL_STORAGE_VARIABLE_THEME, JSON.stringify(this.dataTheme))
    },

    ChangeTheme: function () {

        const darkThemeIcon = document.querySelector('.fa-moon')
        const lightThemeIcon = document.querySelector('.fa-sun')
        const logo = document.querySelector('#logo')

        if (this.isDarkTheme) {
            document.documentElement.style.setProperty('--white-color', '#0a0a0a')
            document.documentElement.style.setProperty('--black-color', '#ffff')
            document.documentElement.style.setProperty('--text_light_theme-color', '#ffff')
            document.documentElement.style.setProperty('--text_dark_theme-color', '#55555')
            document.documentElement.style.setProperty('--dark_grey-color', '#d9dce1')
            document.documentElement.style.setProperty('--light_grey-color', '#1e1e1e')

            darkThemeIcon.classList.add('d-none')
            lightThemeIcon.classList.remove('d-none')
            logo.src = this.pathLogoLightTheme

        } else {
            document.documentElement.style.setProperty('--black-color', '#0a0a0a')
            document.documentElement.style.setProperty('--white-color', '#ffff')
            document.documentElement.style.setProperty('--text_light_theme-color', '#5555')
            document.documentElement.style.setProperty('--text_dark_theme-color', '#ffff')
            document.documentElement.style.setProperty('--dark_grey-color', '#1e1e1e')
            document.documentElement.style.setProperty('--light_grey-color', '#d9dce1')

            lightThemeIcon.classList.add('d-none')
            darkThemeIcon.classList.remove('d-none')
            logo.src = this.pathLogoDarkTheme
        }
        this.isDarkTheme = !this.isDarkTheme

        window.onbeforeunload = () => {
            this.saveConfig()
        }
    },

    init: function (pathLogoDarkTheme, pathLogoLightTheme) {
        this.loadConfig()
        this.pathLogoDarkTheme = pathLogoDarkTheme
        this.pathLogoLightTheme = pathLogoLightTheme
        changeTheme.onclick = () => this.ChangeTheme()
        if (changeThemeClean) changeThemeClean.onclick = () => this.ChangeTheme()


        this.isDarkTheme = !this.isDarkTheme
        this.ChangeTheme()

    }
}