<template>
  <div class="flex flex-col min-h-screen bg-gray-50">
    <!-- Navbar Modern -->
    <nav class="bg-[#f8fafc] shadow-md border-b border-gray-100">
      <div class="container mx-auto px-6">
        <div class="flex items-center justify-between h-16">
          <!-- Logo à gauche -->
          <div class="flex-shrink-0">
            <div class="text-2xl font-semibold text-[#006aff]">
              <img src="../asset/RH.png" alt="Logo" class="w-auto h-16"/>
            </div>
          </div>

          <!-- Navigation au centre -->
          <div class="flex-1 flex justify-center">
            <div class=" md:flex items-center space-x-8">
              <RouterLink 
                v-if="user.permissions.includes('view_users')" 
                :to="{ name: 'list_user' }"
                class="text-[#6c7f93] hover:text-[#006aff] font-medium transition-all duration-200 text-sm"
              >
                Utilisateurs
              </RouterLink>
              <RouterLink 
                v-if="user.role != 'employee'" 
                :to="{ name: 'TeamDemands' }"
                class="text-[#6c7f93] hover:text-[#006aff] font-medium transition-all duration-200 text-sm"
              >
                Users Demandes
              </RouterLink>
              <RouterLink 
                 v-if="user.role != 'admin'"
                :to="{ name: 'userDemands' }"
                class="text-[#6c7f93] hover:text-[#006aff] font-medium transition-all duration-200 text-sm"
              >
                Demandes
              </RouterLink>
            </div>
          </div>

          <!-- Notifications et profil à droite -->
          <div class="flex items-center space-x-4">
            <p class="text-[#006aff] bg-[#c6dffb] py-1 px-2 rounded-md font-semibold text-sm">{{authStore.user.username}}</p>
            <!-- Centre de notifications -->
            <div class="relative">
              <button 
                @click="toggleNotificationDropdown"
                class="p-1.5 rounded-full hover:bg-[#99c4ff]/20 transition-colors duration-200 relative"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5  text-[#6c7f93]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 10-12 0v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                </svg>
                <span 
                  v-if="notifications.length"
                  class="absolute -top-1 -right-1 bg-[#006aff] text-white text-xs rounded-full w-4 h-4 flex items-center justify-center"
                >
                  {{ notifications.length }}
                </span>
              </button>

              <!-- Dropdown Notifications -->
              <transition name="fade-scale">
                <div 
                  v-if="isNotificationDropdownOpen"
                  class="absolute right-0 mt-2 w-96 bg-white rounded-lg shadow-xl border border-gray-100 z-50"
                >
                  <div class="p-3 border-b border-gray-100 flex justify-between items-center">
                    <h3 class="text-sm font-semibold text-[#6c7f93]">Notifications</h3>
                    <button 
                      @click="markAllAsRead"
                      class="text-xs text-[#006aff] hover:text-[#99c4ff] transition-colors"
                    >
                      Tout marquer comme lu
                    </button>
                  </div>

                  <div class="max-h-80 overflow-y-auto">
                    <div 
                      v-if="notifications.length === 0" 
                      class="p-4 text-center text-[#6c7f93]"
                    >
                      Aucune nouvelle notification
                    </div>
                    <div 
                      v-for="notification in notifications" 
                      :key="notification._id"
                      @click="handleNotification(notification)"
                      class="px-4 py-3 hover:bg-[#99c4ff]/10 cursor-pointer transition-colors border-b border-gray-50"
                      :class="{ 'bg-[#99c4ff]/5': !notification.is_read }"
                    >
                      <div class="flex items-start space-x-3">
                        <div class="flex-shrink-0">
                          <div 
                            class="w-8 h-8 rounded-full flex items-center justify-center"
                            :class="notification.is_read ? 'bg-gray-100' : 'bg-[#99c4ff]/20'"
                          >
                            <svg 
                              xmlns="http://www.w3.org/2000/svg" 
                              class="h-4 w-4" 
                              :class="notification.is_read ? 'text-[#6c7f93]' : 'text-[#006aff]'"
                              viewBox="0 0 20 20" 
                              fill="currentColor"
                            >
                              <path d="M10 2a6 6 0 00-6 6v3.586l-.707.707A1 1 0 004 14h12a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6zM10 18a3 3 0 01-3-3h6a3 3 0 01-3 3z" />
                            </svg>
                          </div>
                        </div>
                        <div class="flex-1">
                          <p class="text-sm font-medium text-gray-900">
                            {{ notification.message }}
                          </p>
                          <p class="text-xs text-[#6c7f93] mt-1">
                            {{ convertDate(notification.created_date) }}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </transition>
            </div>

            <!-- Profil utilisateur -->
            <div class="relative">
              <button 
                @click="toggleProfileDropdown"
                class="flex items-center space-x-3 p-1.5 rounded-full hover:bg-[#99c4ff]/20 transition-colors duration-200"
              >
                <img 
                src="https://img.icons8.com/office/40/test-account.png"  
                  alt="Profile"
                  class="h-8 w-8 rounded-full object-cover border-2 border-[#99c4ff]"
                />
                <span class="hidden md:block text-sm font-medium text-[#6c7f93]">
                  {{ user.name }}
                </span>
              </button>

              <!-- Dropdown Profil -->
              <transition name="fade-scale">
                <div 
                  v-if="isProfileDropdownOpen"
                  class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-xl border border-gray-100 z-50"
                >
                  <div class="px-4 py-3 border-b border-gray-100">
                    <p class="text-sm font-semibold text-gray-900">{{ user.name }}</p>
                    <p class="text-xs text-[#6c7f93]">{{ user.email }}</p>
                  </div>
                  <div class="py-1">
                    <button 
                      @click="profile"
                      class="w-full text-left px-4 py-2 text-sm text-[#6c7f93] hover:bg-[#99c4ff]/10 hover:text-[#006aff] transition-colors"
                    >
                    <div class="flex gap-3">
                      <img width="20" height="20" src="https://img.icons8.com/offices/30/gender-neutral-user.png" alt="gender-neutral-user"/>
                      Profile
                    </div>
                    </button>
                    <button 
                      @click="logout"
                      class="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
                    >
                    <div class="flex gap-3">
                      <img width="20" height="20" src="https://img.icons8.com/ios-filled/50/FA5252/logout-rounded-left.png" alt="logout-rounded-left"/>
                      Déconnexion
                    </div>
                    </button>
                  </div>
                </div>
              </transition>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- Le reste du contenu reste inchangé -->
    <main class="flex-grow container mx-auto px-4 py-6" @click="closeDropdowns">
      <RouterView />
    </main>

    <footer class="bg-white border-t border-gray-100 py-6">
      <div class="container mx-auto px-4 text-center">
        <p class="text-sm text-[#6c7f93]">
          &copy; 2025 Enterprise Resource Management. Tous droits réservés.
        </p>
      </div>
    </footer>
  </div>
</template>

<script>
import { RouterLink, RouterView } from 'vue-router';
import services from '@/shared/services';
import utils from '@/shared/utils';
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
      connection: null
    };
  },
  computed: {
    authStore() {
      return useAuthStore();
    },
  },
  methods: {
    toggleProfileDropdown() {
      this.isProfileDropdownOpen = !this.isProfileDropdownOpen;
      this.isNotificationDropdownOpen = false;
      this.getNotifications();
    },
    toggleNotificationDropdown() {
      this.isNotificationDropdownOpen = !this.isNotificationDropdownOpen;
      this.isProfileDropdownOpen = false;
      this.getNotifications();
    },
    async getNotifications() {
      const response = await utils.fetch_methode(services.notification.list, { 
        query: { "user_id": this.user.id }, 
        options: { "page": this.currentPage, "limit": this.pageSize } 
      });
      const data = await response.json();
      this.notifications = data.data;
    },
    async updateNotificationStatus(notification) {
      const query = { notification_id: notification._id };
      try {
        const response = await utils.fetch_methode(services.notification.updateStatus, query);
        if (response.ok) {
          this.getNotifications();
          router.push({ "name": "list_demand" });
        }
      } catch (err) {
        console.error(err);
      }
    },
    async loadMoreNotifications() {
      this.pageSize += 10;
      await this.getNotifications();
    },
    convertDate(date) {
      return utils.convertDate(date);
    },
    async logout() {
      const response = await utils.fetch_methode(services.logout);
      if (response.ok) {
        localStorage.clear();
        router.push({ "name": "login" });
      }
    },
    closeDropdowns() {
      this.isProfileDropdownOpen = false;
      this.isNotificationDropdownOpen = false;
    },
    profile() {
      this.$router.push({ 
        name: "profile_user", 
        params: { id: this.authStore.user.id } 
      }).then(() => {
        window.location.reload();
      });
    },
  },
  created() {
    this.connection = new WebSocket("ws://localhost:8888/notification");
    
    this.connection.onopen = (event) => {
      console.log("Successfully connected to the websocket server...");
    };

    this.connection.onmessage = (event) => {
      console.log(event.data);
    };

    this.connection.onclose = () => {
      console.log("WebSocket connection closed. Reconnecting...");
      setTimeout(() => this.wsConnection(), 1000);
    };
  },
  mounted() {
    this.getNotifications();
  }
};
</script>

<style scoped>
.fade-scale-enter-active,
.fade-scale-leave-active {
  transition: all 0.2s ease-in-out;
}

.fade-scale-enter-from,
.fade-scale-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

.fade-scale-enter-to,
.fade-scale-leave-from {
  opacity: 1;
  transform: scale(1);
}
</style>