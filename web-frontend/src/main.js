import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'


/* import the fontawesome core */
import { library } from '@fortawesome/fontawesome-svg-core'

/* import font awesome icon component */
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

import { faUserSecret } from '@fortawesome/free-solid-svg-icons'
library.add(faUserSecret)

import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons'
library.add(faMagnifyingGlass)

import { faBarsProgress } from '@fortawesome/free-solid-svg-icons'
library.add(faBarsProgress)


import { faCalendar } from '@fortawesome/free-solid-svg-icons'
library.add(faCalendar)


import { faDesktop } from '@fortawesome/free-solid-svg-icons'
library.add(faDesktop)


import { faCloudArrowUp } from '@fortawesome/free-solid-svg-icons'
library.add(faCloudArrowUp)


import { faChevronDown } from '@fortawesome/free-solid-svg-icons'
library.add(faChevronDown)
import { faChevronRight } from '@fortawesome/free-solid-svg-icons'
library.add(faChevronRight)


import { faSpinner } from '@fortawesome/free-solid-svg-icons'
library.add(faSpinner)
import { faCircleExclamation } from '@fortawesome/free-solid-svg-icons'
library.add(faCircleExclamation)
import { faCircleCheck } from '@fortawesome/free-solid-svg-icons'
library.add(faCircleCheck)

import { faPlay } from '@fortawesome/free-solid-svg-icons'
library.add(faPlay)




const app = createApp(App)

app.component('font-awesome-icon', FontAwesomeIcon)

app.use(createPinia())
app.use(router)

app.mount('#app')
