<template>
  <div class="flex flex-col min-h-screen">
    <!-- Navbar -->
    <nav class="bg-black p-3">
      <div class="container mx-auto flex justify-between items-center">
        <div class="text-white text-2xl font-bold">
          <RouterLink :to="{ name : 'list_demand' }"> RH</RouterLink>
        </div>
        <div class="flex justify-evenly w-1/3">
          <RouterLink :to="{ name : 'list_user' }" v-if="user.permissions.includes('view_users')" href="./users.html" class="text-white hover:text-gray-300">Les Utilisateurs</RouterLink>
          <RouterLink :to="{ name : 'list_demand' }"  href="#" class="text-white hover:text-gray-300">Les Demandes</RouterLink>
        </div>
        <div class="flex items-center space-x-4">
          <!-- Notification Dropdown -->
          <div class="relative">
            <button class="text-white hover:text-gray-300 focus:outline-none" @click="toggleNotificationDropdown">
              <div>
                <img v-if="notifications.filter((notification) => notification.is_read == false).length > 0" class="-mb-2" width="10" height="10" src="https://img.icons8.com/ios-filled/50/FA5252/filled-circle.png" alt="filled-circle"/>
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24"
                  stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 10-12 0v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                </svg>
              </div>
            </button>
            <transition name="fade-slide">
              <div v-if="isNotificationDropdownOpen"
                class="absolute right-0 mt-2 w-96 bg-white rounded-md shadow-lg py-2 z-20 h-96 overflow-y-auto">
                <p v-if="notifications.length === 0" class="px-4 py-2 text-gray-700">No notifications</p>
                <div v-else>
                  <div class="mb-3 mt-1 mx-4">
                    <p class="text-sm font-semibold">notifications non lues : ( {{ notifications.filter(notification => notification.is_read == false ).length }} )</p>
                  </div>
                  <a @click="updateNotificationStatus(notification)" v-for="(notification, index) in notifications" :key="index"
                    href="#" class="block border-t border-gray-300 text-gray-700  hover:bg-gray-100">
                    <div v-if="notification.is_read" class="p-4 flex items-center gap-5">
                      <div>
                        <img width="20" height="20"
                          src="https://img.icons8.com/external-vitaliy-gorbachev-fill-vitaly-gorbachev/60/1A1A1A/external-mail-business-vitaliy-gorbachev-fill-vitaly-gorbachev.png"
                          alt="external-mail-business-vitaliy-gorbachev-fill-vitaly-gorbachev" />
                      </div>
                      <div class="w-full" :class="{ 'flex items-center justify-between' : user.role == 'employee' }">
                        <p class="text-sm font-semibold">{{ notification.message }}</p>
                        <p class="text-xs font-bold" v-if="user && user.role == 'manager'">par {{
                          notification.user_username }}</p>
                        <p class="text-xs float-end">{{ convertDate(notification.created_date) }}</p>
                      </div>
                    </div>
                    <div v-else class="bg-slate-200 p-4 flex items-center gap-5">
                      <div>
                        <img width="20" height="20"
                          src="https://img.icons8.com/external-creatype-glyph-colourcreatype/64/1A1A1A/external-app-web-application-v1-creatype-glyph-colourcreatype-31.png"
                          alt="external-app-web-application-v1-creatype-glyph-colourcreatype-31" />
                      </div>
                      <div class="w-full" :class="{ 'flex items-center justify-between' : user.role == 'employee' }">
                        <p class="text-sm font-bold">{{ notification.message }}</p>
                        <p class="text-xs font-bold" v-if="user && user.role == 'manager'">par {{
                          notification.user_username }}</p>
                        <p class="text-xs float-end">{{ convertDate(notification.created_date) }}</p>
                      </div>
                    </div>
                  </a>
                </div>
              </div>
            </transition>
          </div>

          <!-- Profile Dropdown -->
          <div class="relative">
            <button class="text-white hover:text-gray-300 focus:outline-none" @click="toggleProfileDropdown">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24"
                stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M5.121 17.804A4.992 4.992 0 0112 15c1.657 0 3.156.672 4.121 1.804M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14v7m0 0H9m3 0h3" />
              </svg>
            </button>
            <transition name="fade-slide">
              <div v-if="isProfileDropdownOpen"
                class="absolute right-0 mt-2 w-48 bg-white border border-gray-400 rounded-md shadow-lg py-2 z-20">
                <a @click="profile()" class="block px-4 py-2 text-gray-700 hover:bg-gray-100">Profile</a>
                <a @click="logout()" href="#" class="block border-t px-4 py-2 text-gray-700 hover:bg-gray-100  ">Logout</a>
              </div>
            </transition>
          </div>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="bg-gray-50 flex-grow" @click="closeDropDowns()">
      <RouterView />
    </main>

    <!-- Footer -->
    <footer class="bg-black text-white py-3 mt-4">
      <div class="container mx-auto text-center">
        <p class="text-sm font-bold">&copy;2025 RH. All Rights Reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script>
import services from '@/shared/services';
import utils from '@/shared/utils';
import { RouterLink, RouterView } from 'vue-router';
import { useAuthStore } from '@/stores/store';
import router from '@/router';

export default {
  data() {
    return {
      isProfileDropdownOpen: false,
      isNotificationDropdownOpen: false,
      notifications: [],
      user: useAuthStore().user,
      pageSize: 10,
      currentPage: 1,
      totalPages: 1,
    };
  },
  computed: {
      authStore() {
        return useAuthStore();
      },
    },
  methods: {
    //handler profile dropdown
    toggleProfileDropdown() {
      this.isProfileDropdownOpen = !this.isProfileDropdownOpen;
      this.isNotificationDropdownOpen = false
      this.getNotifications();
    },

    //handler notifications dropdown
    toggleNotificationDropdown() {
      this.isNotificationDropdownOpen = !this.isNotificationDropdownOpen;
      this.isProfileDropdownOpen = false
      this.getNotifications();
    },

    //fetch notifications by logged user
    async getNotifications() {
      const response = await utils.fetch_methode(services.notification.list, { query: { "user_id": this.user.id }, options: { "page": this.currentPage, "limit": this.pageSize } });
      const data = await response.json();
      this.notifications = data.data.reverse();
    },

    //update notification is_read statsu to true
    async updateNotificationStatus(notification) {
      const query = { notification_id : notification._id }
      try {
        const response = await utils.fetch_methode(services.notification.updateStatus, query);
        if(response.ok) {
          console.log(response);
          this.getNotifications()
          router.push({ "name": "list_demand" })
        }else {
          console.log(response);
        }
      }catch(err){
        console.log(err);
      }
    },

    convertDate(date) {
      return utils.convertDate(date)
    },

    async logout() {
      const response = await utils.fetch_methode(services.logout)
      if (response.ok) {
        localStorage.clear();
        router.push({ "name": "login" })
      }
    },

    //close navBar dropDowns
    closeDropDowns() {
      this.isProfileDropdownOpen = false
      this.isNotificationDropdownOpen = false
    },

    profile() {
      this.$router.push({ name: "profile_user" }).then(() => {
        window.location.reload();
      });
      this.authStore.setUserId(this.user.id)
      localStorage.setItem("id" , this.user.id)
      console.log(this.user.id);
    }
  },
  mounted() {
    this.getNotifications();
    console.log(this.user.permissions);
  }
};
</script>

<style scoped>
/* Transition classes for fade and slide animations */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.3s ease-in-out, transform 0.3s ease-in-out;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.fade-slide-enter-to {
  opacity: 1;
  transform: translateY(0);
}

.fade-slide-leave-from {
  opacity: 1;
  transform: translateY(0);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
