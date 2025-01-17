import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/store'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path : "/login",
      name : "login",
      component: () => import('@/views/Auth/LoginView.vue')
    },
    {
      path :"/private",
      component :  () => import('@/layout/Layout.vue'),
      children: [
        // users
        {
          path: '/private/user/list',
          name: 'list_user',
          component: ()=> import('@/views/Users/ListUser.vue'),
        },
        {
          path: '/private/user/create',
          name: 'create_user',
          component: ()=> import('@/views/Users/CreateUsers.vue'),
        },
        {
          path: '/private/user/update',
          name: 'update_user',
          component: ()=> import('@/views/Users/UpdateUser.vue'),
        },
        {
          path: '/private/user/profile',
          name: 'profile_user',
          component: ()=> import('@/views/Users/UserDetails.vue'),
        },
        {
          path: '/private/user/reset_password',
          name: 'reset_password',
          component: ()=> import('@/views/Users/Reset_password.vue'),
        },
        // contract
        {
          path: '/private/user/contract/update',
          name: 'contract_update',
          component: ()=> import('@/views/Contract/UpdateContract.vue'),
        },
        {
          path: '/private/user/contract/create',
          name: 'contract_create',
          component: ()=> import('@/views/Contract/CreateContract.vue'),
        },
        // demande
        {
          path: '/private/user/demand/list',
          name: 'list_demand',
          component: ()=> import('@/views/Demand/DemandsList.vue'),
        },
        {
          path: '/private/user/demand/create',
          name: 'create_demand',
          component: ()=> import('@/views/Demand/CreateDemand.vue'),
        },
        // notification
        {
          path: '/private/notification',
          name: 'notification',
          component: ()=> import('@/views/Notification/Notification.vue'),
        },
      ]
    }

  ],
})


router.beforeEach(async (to, from, next) => {
  const auth = useAuthStore();
  await auth.checkauth();

  // Vérification de l'authentification
  if (!auth.user && to.name !== "login") {
    return next({ name: "login" });
  }

  // Gestion des utilisateurs désactivés
  if (auth.user?.status === false && to.name !== "login") {
    return next({ name: "login" });
  }

  // Redirection pour le premier login
  if (auth.user?.first_login === true && to.name !== "reset_password") {
    return next({ name: "reset_password" });
  }

  next();
});



export default router
