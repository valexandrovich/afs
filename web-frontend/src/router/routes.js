import SearchView from "@/views/SearchView.vue";
import SchedulerView from "@/views/SchedulerView.vue";
import ProgressView from "@/views/ProgressView.vue";
import UploadView from "@/views/UploadView.vue";

export const routes = [
    {
        path: '/',
        name: 'search',
        component: SearchView,
        label: 'Пошук',
        icon: ['fas', 'magnifying-glass']
    },
    {
        path: '/scheduler',
        name: 'scheduler',
        component: SchedulerView,
        label: 'Розклад',
        icon: ['fas', 'calendar']
    },
    {
        path: '/progress',
        name: 'progress',
        component: ProgressView,
        label: 'Прогрес',
        icon: ['fas', 'bars-progress']
    },
    {
        path: '/upload',
        name: 'upload',
        component: UploadView,
        label: 'Завантаження',
        icon: ['fas', 'cloud-arrow-up']
    },
    {
        path: '/monitoring',
        name: 'monitoring',
        component: UploadView,
        label: 'Моніторинг',
        icon: ['fas', 'desktop']
    }
]