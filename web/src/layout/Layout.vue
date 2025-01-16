<template>
  <!-- Navbar -->
  <nav class="bg-black p-3">
    <div class="container mx-auto flex justify-between items-center">
      <div class="text-white text-2xl font-bold">RH</div>
      <div class="flex justify-evenly w-1/3">
        <a href="./users.html" class="text-white hover:text-gray-300">Lien 1</a>
        <a href="#" class="text-white hover:text-gray-300">Lien 2</a>
        <a href="#" class="text-white hover:text-gray-300">Lien 3</a>
      </div>
      <div class="flex items-center space-x-4">

        <!-- Notification Dropdown -->
        <div class="relative">
          <button class="text-white hover:text-gray-300 focus:outline-none" @click="toggleNotificationDropdown">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24"
              stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 10-12 0v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
            </svg>
          </button>
          <transition name="fade-slide">
            <div v-if="isNotificationDropdownOpen"
              class="absolute right-0 mt-2 w-96 bg-white rounded-md shadow-lg py-2 z-20">
              <p v-if="notifications.length === 0" class="px-4 py-2 text-gray-700">No notifications</p>
              <div v-else>
                <a v-for="(notification, index) in notifications" :key="index" href="#"
                  class="block px-4 py-3 text-gray-700 hover:bg-gray-100">
                  <p>{{ notification.message }}</p>
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
              class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-2 z-20">
              <a href="#" class="block px-4 py-2 text-gray-700 hover:bg-gray-100">Profile</a>
              <a href="#" class="block px-4 py-2 text-gray-700 hover:bg-gray-100">Logout</a>
            </div>
          </transition>
        </div>
      </div>
    </div>
  </nav>

  <!-- Main Content -->
  <main class="bg-gray-50 h-screen">
    <RouterView />
  </main>

  <!-- Footer -->
  <footer class="bg-black text-white py-3 mt-4">
    <div class="container mx-auto text-center">
      <p>&copy; 2025 RH. All Rights Reserved.</p>
    </div>
  </footer>
</div>
</template>

<script>
import services from '@/shared/services';
import fetch_methode from '@/shared/utils';
import { RouterView } from 'vue-router';
import { useAuthStore } from '@/stores/store';

export default {
  data() {
    return {
      isProfileDropdownOpen: false,
      isNotificationDropdownOpen: false,
      notifications: [],
      pageSize: 10,
      currentPage: 1,
      totalPages: 1,
    };
  },
  methods: {
    toggleProfileDropdown() {
      this.isProfileDropdownOpen = !this.isProfileDropdownOpen;
      this.getNotifications();
    },
    toggleNotificationDropdown() {
      this.isNotificationDropdownOpen = !this.isNotificationDropdownOpen;
      this.getNotifications();
    },

    async getNotifications() {
      await fetch_methode(services.notification.list, { query: { "user_id": "6788d778b1b810614aee49ea" }, options: { "page": this.currentPage, "limit": this.pageSize } })
        .then((data) => {
          this.notifications = data.data;
          console.log(data);
        });
    }
  },
  mounted() {
    this.getNotifications();
  }
};
</script>

<style scoped>
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
