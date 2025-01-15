<script setup lang="ts">
import { RouterView } from 'vue-router';
</script>

<template>
  <!-- Navbar -->
  <nav class="bg-black p-4 mb-4">
    <div class="container mx-auto flex justify-between items-center">
      <div class="text-white text-2xl font-bold">RH</div>
      <div class="flex space-x-4">
        <a href="./users.html" class="text-white hover:text-gray-300">Lien 1</a>
        <a href="#" class="text-white hover:text-gray-300">Lien 2</a>
        <a href="#" class="text-white hover:text-gray-300">Lien 3</a>
      </div>
      <div class="flex items-center space-x-4">
        <button class="text-white hover:text-gray-300">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 10-12 0v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
        </button>
        <div class="relative">
          <button class="text-white hover:text-gray-300">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5.121 17.804A4.992 4.992 0 0112 15c1.657 0 3.156.672 4.121 1.804M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14v7m0 0H9m3 0h3" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </nav>

  <!-- Main Content -->
  <main class="bg-gray-50 h-[100%]">
    <RouterView />
  </main>

  <!-- Footer -->
  <footer class="bg-black text-white py-6 mt-4">
    <div class="container mx-auto text-center">
      <p>&copy; 2025 RH. All Rights Reserved.</p>
    </div>
  </footer>
</template>
