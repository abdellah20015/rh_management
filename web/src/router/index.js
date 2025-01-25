import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/store'
import utils from '@/shared/utils';

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
          meta : {
            permission : "view_users"
          },
          component: ()=> import('@/views/Users/ListUser.vue'),
        },
        {
          path: '/private/user/create',
          name: 'create_user',
          meta : {
            permission : "create_user"
          },
          component: ()=> import('@/views/Users/CreateUsers.vue'),
        },
        {
          path: '/private/user/update/:id',
          name: 'update_user',
          meta : {
            permission : "update_user"
          },
          component: ()=> import('@/views/Users/UpdateUser.vue'),
        },
        {
          path: '/private/user/profile/:id',
          name: 'profile_user',
          component: ()=> import('@/views/Users/UserDetails.vue'),
        },
        {
          path: '/private/user/details/:id',
          name: 'details_user',
          meta : {
            permission : "view_users"
          },
          component: ()=> import('@/views/Users/UserDetails.vue'),
        },
        {
          path: '/private/user/reset_password',
          name: 'reset_password',
          component: ()=> import('@/views/Users/Reset_password.vue'),
        },
        // contract
        {
          path: '/private/user/contract/update/:id',
          name: 'contract_update',
          meta : {
            permission : "update_contract"
          },
          component: ()=> import('@/views/Contract/UpdateContract.vue'),
        },
        {
          path: '/private/user/contract/create/:id',
          name: 'contract_create',
          meta : {
            permission : "create_contract"
          },
          component: ()=> import('@/views/Contract/CreateContract.vue'),
        },
        // demande
        {
          path: '/private/user/demand/list',
          name: 'userDemands',
          component: ()=> import('@/views/Demand/UserDemandsList.vue'),
        },
        {
          path: '/private/user/demand/list',
          name: 'TeamDemands',
          component: ()=> import('@/views/Demand/TeamDemands.vue'),
        },
        {
          path: '/private/user/demand/create',
          name: 'create_demand',
          component: ()=> import('@/views/Demand/CreateDemand.vue'),
        },
        // notification
        {
          path: '/private/notifications',
          name: 'notification',
          component: ()=> import('@/views/Notification/Notification.vue'),
        },
      ]
    },
    {
      path: '/no_permission',
      name: 'no_permmission',
      component: ()=> import('@/views/static/No_permission.vue'),
    },

  ],
})


router.beforeEach(async (to, from, next) => {
  const auth = useAuthStore();
  if (to.name !== "login") {
    await auth.checkauth();
  }

  // Vérification de l'authentification
  if (!auth.user && to.name !== "login") {
    return next({ name: "login" });
  }

  // Gestion des utilisateurs désactivés
  if (auth.user?.status === false && to.name !== "login") {
    utils.errorAlert(" Votre compte a été désactivé. Vous ne pouvez pas accéder à la plateforme. ")
    return next({ name: "login" });
  }

  // Redirection pour le premier login
  if (auth.user?.first_login === true && to.name !== "reset_password") {
    return next({ name: "reset_password" });
  }

  if (to.meta.permission && (!auth?.user.permissions?.includes(to.meta.permission))) {
    return next({ name : "no_permmission"}); 
  }
  

  next();
});



export default router;

