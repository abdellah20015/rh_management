<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 via-gray-100 to-gray-200 flex items-center justify-center p-6">
    <div class="bg-white rounded-3xl shadow-2xl max-w-lg w-full p-12 relative overflow-hidden">
      <!-- Background Pattern -->
      <div class="absolute top-0 right-0 w-40 h-40 bg-red-50 rounded-full -mr-20 -mt-20"></div>
      <div class="absolute bottom-0 left-0 w-32 h-32 bg-blue-50 rounded-full -ml-16 -mb-16"></div>
      
      <!-- Main Content -->
      <div class="relative">
        <!-- Error Icon -->
        <div class="flex justify-center mb-8">
          <div class="w-28 h-28 rounded-2xl bg-gradient-to-br from-red-50 to-red-100 flex items-center justify-center shadow-lg">
            <svg 
              class="w-14 h-14 text-red-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
              />
            </svg>
          </div>
        </div>

        <!-- Text Content -->
        <div class="text-center space-y-6">
          <h1 class="text-4xl font-extrabold text-gray-800 mb-4">
            Accès Refusé
          </h1>
          
          <p class="text-xl text-gray-600 leading-relaxed max-w-md mx-auto">
            Désolé, vous n'avez pas les permissions nécessaires pour accéder à cette page.
          </p>

          <div class="h-px w-16 bg-gray-200 mx-auto my-8"></div>

          <!-- Action Button -->
          <div class="pt-4">
            <router-link 
              :to="{ name: 'statistics' }"
              class="inline-flex items-center px-8 py-4 text-lg font-medium text-white 
                     bg-gradient-to-r from-blue-600 to-blue-700 rounded-[8px] shadow-lg 
                     hover:from-blue-700 hover:to-blue-800 transition-all duration-200"
            >
              <svg 
                class="w-5 h-5 mr-3"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M10 19l-7-7m0 0l7-7m-7 7h18"
                />
              </svg>
              Retour à l'accueil
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "NoPermission"
};
</script>