<template>
  <div class="flex flex-col min-h-screen bg-gray-50">
    <!-- Enterprise Navbar -->
    <nav class="bg-white shadow-md border-b border-gray-200">
      <div class="container mx-auto px-4 py-3 flex justify-between items-center">
        <div class="flex items-center space-x-4">
          <!-- <img src="/logo.png" alt="Company Logo" class="h-8 w-auto"/> -->
          <div class="hidden md:flex space-x-4">
            <RouterLink v-if="user.permissions.includes('view_users')" :to="{ name: 'list_user' }"
              class="text-gray-700 hover:text-blue-600 font-medium transition-colors">
              Utilisateurs
            </RouterLink>
            <RouterLink v-if="user.role != 'employee'"  :to="{ name: 'TeamDemands' }"
              class="text-gray-700 hover:text-blue-600 font-medium transition-colors">
              Users Demandes
            </RouterLink>
            <RouterLink :to="{ name: 'userDemands' }"
              class="text-gray-700 hover:text-blue-600 font-medium transition-colors">
              Demandes
            </RouterLink>
          </div>
        </div>

        <div class="flex items-center space-x-4">
          <!-- Notification Center -->
          <div class="relative">
            <button @click="toggleNotificationDropdown"
              class="text-gray-500 hover:text-blue-600 focus:outline-none relative">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24"
                stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 10-12 0v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
              </svg>
              <span v-if="unreadNotificationsCount"
                class="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full px-1.5 py-0.5">
                {{ unreadNotificationsCount }}
              </span>
            </button>

            <transition name="dropdown">
              <div v-if="isNotificationDropdownOpen"
                class="absolute right-0 mt-2 w-96 bg-white rounded-lg shadow-xl border border-gray-200 z-50">
                <div class="p-4 border-b border-gray-200 flex justify-between items-center">
                  <h3 class="text-sm font-semibold text-gray-700">Notifications</h3>
                  <button @click="markAllAsRead" class="text-xs text-blue-600 hover:underline">
                    Mark all as read
                  </button>
                </div>

                <div class="max-h-80 overflow-y-auto">
                  <div v-if="notifications.length === 0" class="p-4 text-center text-gray-500">
                    No new notifications
                  </div>
                  <div v-for="notification in notifications" :key="notification._id"
                    @click="handleNotification(notification)"
                    class="px-4 py-3 border-b border-gray-100 hover:bg-gray-50 cursor-pointer transition-colors"
                    :class="{ 'bg-blue-50': !notification.is_read }">
                    <div class="flex items-start space-x-3">
                      <div class="flex-shrink-0">
                        <div class="w-10 h-10 rounded-full flex items-center justify-center"
                          :class="notification.is_read ? 'bg-gray-200' : 'bg-blue-100'">
                          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20"
                            fill="currentColor" :class="notification.is_read ? 'text-gray-500' : 'text-blue-600'">
                            <path
                              d="M10 2a6 6 0 00-6 6v3.586l-.707.707A1 1 0 004 14h12a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6zM10 18a3 3 0 01-3-3h6a3 3 0 01-3 3z" />
                          </svg>
                        </div>
                      </div>
                      <div class="flex-1">
                        <p class="text-sm font-medium text-gray-900">
                          {{ notification.message }}
                        </p>
                        <p class="text-xs text-gray-500 mt-1">
                          {{ convertDate(notification.created_date) }}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>

                <div v-if="notifications.length > 0" class="p-2 border-t border-gray-200 text-center">
                  <button @click="loadMoreNotifications" class="text-sm text-blue-600 hover:underline">
                    Load more notifications
                  </button>
                </div>
              </div>
            </transition>
          </div>

          <!-- User Profile Dropdown -->
          <div class="relative">
            <button @click="toggleProfileDropdown"
              class="flex items-center space-x-2 text-gray-700 hover:bg-gray-100 p-1 rounded-full transition-colors">
              <img :src="user.avatar || '/default-avatar.png'" alt="Profile"
                class="h-8 w-8 rounded-full object-cover" />
              <span class="hidden md:block text-sm font-medium">
                {{ user.name }}
              </span>
            </button>

            <transition name="dropdown">
              <div v-if="isProfileDropdownOpen"
                class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-xl border border-gray-200 z-50">
                <div class="px-4 py-3 border-b border-gray-200">
                  <p class="text-sm font-semibold text-gray-900">{{ user.name }}</p>
                  <p class="text-xs text-gray-500">{{ user.email }}</p>
                </div>
                <div class="py-1">
                  <button @click="profile"
                    class="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors">
                    Profile
                  </button>
                  <button @click="logout"
                    class="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors">
                    Sign out
                  </button>
                </div>
              </div>
            </transition>
          </div>
        </div>
      </div>
    </nav>

    <!-- Main Content Area -->
    <main class="flex-grow container mx-auto px-4 py-6" @click="closeDropdowns">
      <RouterView />
    </main>

    <!-- Enterprise Footer -->
    <footer class="bg-white border-t border-gray-200 py-6">
      <div class="container mx-auto px-4 text-center">
        <p class="text-sm text-gray-600">
          &copy; 2025 Enterprise Resource Management. All Rights Reserved.
        </p>
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
      connection: null
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
      this.notifications = data.data;
    },

    //update notification is_read statsu to true
    async updateNotificationStatus(notification) {
      const query = { notification_id: notification._id }
      try {
        const response = await utils.fetch_methode(services.notification.updateStatus, query);
        if (response.ok) {
          console.log(response);
          this.getNotifications()
          router.push({ "name": "list_demand" })
        } else {
          console.log(response);
        }
      } catch (err) {
        console.log(err);
      }
    },

    async addPagesize() {
      this.pageSize += this.pageSize
      console.log(this.pageSize);
      this.getNotifications()
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
      this.$router.push({ name: "profile_user", params: { id: this.authStore.user.id } }).then(() => {
        window.location.reload();
      });
    },

  },
  created() {
    this.connection = new WebSocket("ws://localhost:8888/notification");

    this.connection.onopen = (event) => {
      console.log(event);
      console.log("Successfully connected to the echo websocket server...")
    }

    this.connection.onmessage = (event) => {
      const notification = JSON.parse(event.data);
      console.log(notification);
      utils.notification(notification.message)
    }

    this.connection.onclose = () => {
      console.log("WebSocket connection closed. Reconnecting...");
      setTimeout(() => this.wsConnection(), 1000); // Attempt to reconnect after 1 second
    };

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
