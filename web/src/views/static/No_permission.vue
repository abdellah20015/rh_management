<template>
    <div class="flex justify-center items-center h-screen bg-gray-100">
      <div class="bg-white p-8 rounded-lg shadow-lg max-w-md w-full">
        <div class="text-center">
          <h1 class="text-4xl font-semibold text-red-700 mb-4">Accès Refusé</h1>
          <p class="text-lg text-gray-700 mb-6">
            Désolé, vous n'avez pas la permission d'accéder à cette page.
          </p>
          <div class="mt-8">
            <router-link 
              :to="{name : 'list_demand'}" 
              class="text-blue-500 hover:underline">Retour à l'accueil</router-link>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "NoPermission",
  };
  </script>
