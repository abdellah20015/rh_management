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
      path :"/private",
      component :  () => import('@/layout/Layout.vue'),
      children: [
        // users
        {
          path: '/user/list',
          name: 'list_user',
          component: ()=> import('@/views/Users/ListUser.vue'),
        },       
        {
          path: '/user/create',
          name: 'create_user',
          component: ()=> import('@/views/Users/CreateUsers.vue'),
        },       
        {
          path: '/user/update',
          name: 'update_user',
          component: ()=> import('@/views/Users/UpdateUser.vue'),
        },       
        {
          path: '/user/profile',
          name: 'profile_user',
          component: ()=> import('@/views/Users/UserDetails.vue'),
        },       
        {
          path: '/user/reset_password',
          name: 'reset_password',
          component: ()=> import('@/views/Users/Reset_password.vue'),
        },       
        // contract
        {
          path: '/user/contract/update',
          name: 'contract_update',
          component: ()=> import('@/views/Contract/UpdateContract.vue'),
        },       
        {
          path: '/user/contract/create',
          name: 'contract_create',
          component: ()=> import('@/views/Contract/CreateContract.vue'),
        },     
        // demande
        {
          path: '/user/demand/list',
          name: 'list_demand',
          component: ()=> import('@/views/Demand/DemandsList.vue'),
        },       
        {
          path: '/user/demand/create',
          name: 'create_demand',
          component: ()=> import('@/views/Demand/CreateDemand.vue'),
        },   
        // notification
        {
          path: '/notification',
          name: 'notification',
          component: ()=> import('@/views/Notification/Notification.vue'),
        },   
      ]
    }

  ],
})

export default router
