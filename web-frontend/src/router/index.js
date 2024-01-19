import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SchedulerView from "@/views/SchedulerView.vue";
import ProgressView from "@/views/ProgressView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/scheduler',
      name: 'scheduler',
      component: SchedulerView
    },
    {
      path: '/progress',
      name: 'progress',
      component: ProgressView
    }
  ]
})

export default router
