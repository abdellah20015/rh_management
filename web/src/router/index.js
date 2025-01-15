import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path : "/login",
      component: () => import('@/views/Auth/LoginView.vue')
    },
    {
      path :"/",
      component :  () => import('@/layout/Layout.vue'),
      children: [
        {
          path: '/',
          name: 'home',
          component: HomeView,
        },       
      ]
    }

  ],
})

export default router
