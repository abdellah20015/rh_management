<template>
  <div class="container mx-auto px-4 py-8">
    <!-- Main Grid Container -->
    <!-- Profile Section - Pour Admin -->
    <div v-if="userData?.role === 'admin'" class="grid grid-cols-1 gap-8">
      <div class="bg-white rounded-xl shadow-lg p-6">
        <!-- En-tête -->
        <div class="flex items-center space-x-4 mb-8">
          <div class="bg-[#dbeaff] p-3 rounded-full">
            <img src="https://img.icons8.com/plasticine/100/contract-job.png" alt="Test Account"
              class="h-16 w-16 text-gray-600" />
          </div>
          <div>
            <h2 class="text-2xl font-bold text-gray-800">Détail de profil</h2>
            <p class="text-gray-500">Information personnel</p>
          </div>
        </div>

        <!-- Contenu en deux colonnes -->
        <div class="grid grid-cols-2 gap-8">
          <!-- Colonne gauche - Informations Personnelles -->
          <div class="space-y-6">
            <div>
              <h3 class="text-lg font-semibold text-gray-700 mb-4">Informations Personnelles</h3>
              <div class="space-y-3">
                <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
                  <span class="font-medium text-gray-600">Nom d'utilisateur:</span>
                  <span class="col-span-2 text-gray-900">{{ userData?.username }}</span>
                </div>
                <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
                  <span class="font-medium text-gray-600">Prénom:</span>
                  <span class="col-span-2 text-gray-900">{{ userData?.firstname }}</span>
                </div>
                <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
                  <span class="font-medium text-gray-600">Nom:</span>
                  <span class="col-span-2 text-gray-900">{{ userData?.lastname }}</span>
                </div>
                <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
                  <span class="font-medium text-gray-600">Adresse:</span>
                  <span class="col-span-2 text-gray-900">{{ userData?.address }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Colonne droite - Contact & Système -->
          <div class="space-y-6">
            <div>
              <h3 class="text-lg font-semibold text-gray-700 mb-4">Contact & Système</h3>
              <div class="space-y-3">
                <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
                  <span class="font-medium text-gray-600">Email:</span>
                  <span class="col-span-2 text-gray-900">{{ userData?.email }}</span>
                </div>
                <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
                  <span class="font-medium text-gray-600">Téléphone:</span>
                  <span class="col-span-2 text-gray-900">{{ userData?.phone }}</span>
                </div>
                <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
                  <span class="font-medium text-gray-600">Role:</span>
                  <span class="col-span-2 text-gray-900">{{ userData?.role }}</span>
                </div>
                <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
                  <span class="font-medium text-gray-600">Status:</span>
                  <span class="col-span-2">
                    <span class="px-3 py-1 rounded-full text-sm font-medium"
                      :class="userData?.status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
                      {{ userData?.status ? 'Active' : 'Inactive' }}
                    </span>
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Section Permissions - Pleine largeur -->
        <div class="mt-8">
          <h3 class="text-lg font-semibold text-gray-700 mb-4">Permissions</h3>
          <div class="bg-gray-50 rounded-lg p-4">
            <div class="grid grid-cols-4 gap-3">
              <span v-for="permission in userData?.permissions" :key="permission"
                class="px-3 py-2 bg-blue-100 text-blue-700 rounded-[5px] text-sm text-center font-medium">
                {{ permission }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
      <!-- Profile Section -->
      <div v-if="userData?.role !== 'admin'" class="bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center space-x-4 mb-6">
          <div class="bg-[#dbeaff] p-3 rounded-full">
            <img src="https://img.icons8.com/plasticine/100/contract-job.png" alt="Test Account"
              class="h-16 w-16 text-gray-600" />
          </div>
          <div>
            <h2 class="text-2xl font-bold text-gray-800">Détail de profil</h2>
            <p class="text-gray-500">Information personnel</p>
          </div>
        </div>
        <div class="grid gap-4">
          <!-- Username -->
          <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
            <span class="font-medium text-gray-600">Nom d'utilisateur:</span>
            <span class="col-span-2 text-gray-900">{{ userData?.username }}</span>
          </div>
          <!--Firstname-->
          <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
            <span class="font-medium text-gray-600">Prénom:</span>
            <span class="col-span-2 text-gray-900">{{ userData?.firstName }}</span>
          </div>
          <!-- Lastname -->
          <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
            <span class="font-medium text-gray-600">Nom:</span>
            <span class="col-span-2 text-gray-900">{{ userData?.lastName }}</span>
          </div>
          <!--Adresse-->
          <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
            <span class="font-medium text-gray-600">Adresse:</span>
            <span class="col-span-2 text-gray-900">{{ userData?.address }}</span>
          </div>

          <!-- Role -->
          <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
            <span class="font-medium text-gray-600">Role:</span>
            <span class="col-span-2 text-gray-900">{{ userData?.role }}</span>
          </div>
          <!-- Manager -->
          <div v-if="authStore.user.role === 'employee'"
            class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
            <span class="font-medium text-gray-600">Manager:</span>
            <span class="col-span-2 text-gray-900">
              <template v-if="loading">
                <span class="text-gray-400">Loading...</span>
              </template>
              <template v-else-if="managerUsername">
                {{ managerUsername }}
              </template>
              <template v-else>
                <span class="text-gray-400">Not assigned</span>
              </template>
            </span>
          </div>
          <!--Email-->
          <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
            <span class="font-medium text-gray-600">Email:</span>
            <span class="col-span-2 text-gray-900">{{ userData?.email }}</span>
          </div>
          <!-- Phone -->
          <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
            <span class="font-medium text-gray-600">Téléphone:</span>
            <span class="col-span-2 text-gray-900">{{ userData?.phone }}</span>
          </div>
          <!-- Status -->
          <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
            <span class="font-medium text-gray-600">Status:</span>
            <span class="col-span-2 px-3 py-1 rounded-full text-sm font-medium w-fit"
              :class="userData?.status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
              {{ userData?.status ? 'Active' : 'Inactive' }}
            </span>
          </div>

          <!-- Permissions -->
          <div class="grid gap-3 p-4 bg-gray-50 rounded-[5px]">
            <h3 class="font-medium text-gray-600">Permissions:</h3>
            <div class="grid grid-flow-row grid-cols-2 gap-2">
              <span v-for="permission in userData?.permissions" :key="permission"
                class="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm text-center">
                {{ permission }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Contract Section -->
      <div v-if="userData?.role !== 'admin'" class="bg-white rounded-xl shadow-lg p-6">

        <!-- Contract History Modal -->
        <transition name="modal" enter-active-class="transition duration-300 ease-out"
          enter-from-class="transform scale-95 opacity-0" enter-to-class="transform scale-100 opacity-100"
          leave-active-class="transition duration-200 ease-in" leave-from-class="transform scale-100 opacity-100"
          leave-to-class="transform scale-95 opacity-0">
          <div v-if="showContractHistoryModal" class="fixed inset-0 z-50 overflow-y-auto" aria-labelledby="modal-title"
            role="dialog" aria-modal="true">
            <!-- Backdrop -->
            <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"></div>

            <div class="flex min-h-screen items-end justify-center p-4 text-center sm:items-center sm:p-0">
              <div
                class="relative transform overflow-hidden rounded-lg bg-white text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-4xl">
                <!-- Header -->
                <div class="bg-white px-6 py-4 border-b border-gray-200">
                  <div class="flex items-center justify-between">
                    <div>
                      <h3 class="text-xl font-semibold text-gray-900">Historique des contrats</h3>
                      <p class="mt-1 text-sm text-gray-500">Liste complète des contrats précédents</p>
                    </div>
                    <button @click="showContractHistoryModal = false"
                      class="rounded-md bg-white text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2">
                      <span class="sr-only">Fermer</span>
                      <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                </div>

                <!-- Table -->
                <div class="bg-white px-6 py-4">
                  <div class="overflow-hidden rounded-lg border border-gray-200">
                    <table v-if="inactiveContracts.length > 0" class="min-w-full divide-y divide-gray-200">
                      <thead class="bg-gray-50">
                        <tr>
                          <th scope="col"
                            class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type
                          </th>
                          <th scope="col"
                            class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date
                            Début
                          </th>
                          <th scope="col"
                            class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date
                            Fin
                          </th>
                          <th scope="col"
                            class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Statut</th>
                          <th scope="col"
                            class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date
                            de
                            Création</th>
                        </tr>
                      </thead>
                      <tbody class="bg-white divide-y divide-gray-200">
                        <tr v-for="contract in inactiveContracts" :key="contract._id"
                          class="hover:bg-gray-50 transition-colors duration-200">
                          <td class="px-6 py-4 whitespace-nowrap">
                            <span class="inline-flex text-sm font-medium text-gray-900 uppercase">{{ contract.type
                              }}</span>
                          </td>
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ contract.start_date }}</td>
                          <td class="px-6 py-4 whitespace-nowrap text-sm">
                            <span v-if="contract.type === 'cdd'" class="text-gray-600">
                              {{ contract.end_date || 'N/A' }}
                            </span>
                            <span v-else class="text-gray-400">-</span>
                          </td>
                          <td class="px-6 py-4 whitespace-nowrap">
                            <span class="inline-flex px-2.5 py-1 rounded-full text-xs font-medium"
                              :class="contract.status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
                              {{ contract.status ? 'Actif' : 'Inactif' }}
                            </span>
                          </td>
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ contract.date_creation }}
                          </td>
                        </tr>
                      </tbody>
                    </table>
                    <div v-else class="flex flex-col items-center justify-center py-12 px-4">
                      <div class="bg-gray-50 rounded-full p-4 mb-4">
                        <svg class="h-12 w-12 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none"
                          viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                      </div>
                      <h3 class="text-lg font-medium text-gray-900 mb-2">Aucun historique de contrat</h3>
                      <p class="text-gray-500 text-center max-w-sm">
                        Il n'y a actuellement aucun contrat inactif dans l'historique de cet employé.
                      </p>
                    </div>

                  </div>
                </div>

              </div>
            </div>
          </div>
        </transition>

        <div class="flex items-center space-x-4 mb-6">
          <div class="bg-[#dbeaff] p-3 rounded-full">
            <img src="https://img.icons8.com/plasticine/100/agreement.png" alt="Contract"
              class="h-16 w-16 text-gray-600" />
          </div>
          <div>
            <h2 class="text-2xl font-bold text-gray-800">Information de contrat</h2>
            <p class="text-gray-500">Détails de l'emploi</p>
          </div>
        </div>


        <div v-if="authStore.user.role === 'admin'" class="mt-4">
          <button @click="showContractHistory"
            class="w-full bg-white border-2 border-gray-200 text-gray-700 py-3 px-4 rounded-[5px] hover:bg-gray-50 hover:border-gray-300 transition-all duration-200 flex items-center justify-center gap-2 font-medium">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500" viewBox="0 0 20 20"
              fill="currentColor">
              <path fill-rule="evenodd"
                d="M6 2a2 2 0 00-2 2v12a2 2 0 002 2h8a2 2 0 002-2V7.414A2 2 0 0015.414 6L12 2.586A2 2 0 0010.586 2H6zm2 10a1 1 0 100 2h4a1 1 0 100-2H8zm5-4a1 1 0 100 2H8a1 1 0 100-2h5z"
                clip-rule="evenodd" />
            </svg>
            <span>Historique des contrats</span>
          </button>
        </div>
        <br>
        <!-- Loading State -->
        <div v-if="loading" class="text-center py-8">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 mx-auto"></div>
          <p class="mt-4 text-gray-600">Chargement des informations sur le profil...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="text-center py-8">
          <div class="text-red-500 mb-4">{{ error }}</div>
          <button @click="fetchUserProfile"
            class="bg-gray-200 text-gray-700 py-2 px-4 rounded-[5px] hover:bg-gray-300 transition duration-200">
            Réessayer
          </button>
        </div>

        <!-- Current Contract Display -->
        <template v-else-if="currentActiveContract">
          <div class="grid gap-4">
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
              <span class="font-medium text-gray-600">Type:</span>
              <span class="col-span-2 uppercase text-gray-900">{{ currentActiveContract.type }}</span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
              <span class="font-medium text-gray-600">Date de début:</span>
              <span class="col-span-2 text-gray-900">{{ currentActiveContract.start_date }}</span>
            </div>
            <div v-if="currentActiveContract.type === 'cdd'"
              class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
              <span class="font-medium text-gray-600">Date fin:</span>
              <span class="col-span-2 text-gray-900">{{ currentActiveContract.end_date }}</span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
              <span class="font-medium text-gray-600">Solde de congés:</span>
              <span class="col-span-2 text-gray-900">{{ currentActiveContract.leave_balance }} days</span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
              <span class="font-medium text-gray-600">Salaire:</span>
              <span class="col-span-2 text-gray-900">{{ currentActiveContract.salary }} DH</span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
              <span class="font-medium text-gray-600">Status:</span>
              <span class="col-span-2 px-3 py-1 rounded-full text-sm font-medium w-fit"
                :class="currentActiveContract.status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
                {{ currentActiveContract.status ? 'Active' : 'Inactive' }}
              </span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-[5px]">
              <span class="font-medium text-gray-600">Crée le:</span>
              <span class="col-span-2 text-gray-900">{{ currentActiveContract.date_creation }}</span>
            </div>

            <!-- Update Contract Button for Admin (only for active contracts) -->
            <div v-if="authStore.user.role === 'admin' && currentActiveContract.status" class="mt-6">
              <button @click="navigateToContractUpdate"
                class="w-full bg-[#006AFF] text-white py-2.5 px-4 rounded-[5px] hover:bg-[#006AFF]/90 transition-all duration-200">
                Modifier le contrat
              </button>
            </div>
          </div>
        </template>

        <!-- No Contract State for First-Time Users -->
        <template v-else-if="isFirstTimeUser">
          <div class="text-center py-8">
            <div class="mb-6">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 mx-auto text-gray-400 mb-4" fill="none"
                viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <h3 class="text-xl font-semibold text-gray-800 mb-2">Bienvenue!</h3>
              <p class="text-gray-600"><span class="font-bold text-gray-700 italic">{{ userData?.username }}</span> n'a
                pas encore de contrat. Créer son contrat.</p>
            </div>
            <button @click="navigateToContractCreate"
              class="bg-[#006AFF] text-white py-3 px-6 rounded-[5px] hover:bg-[#006AFF]/90 transition-all duration-200 inline-flex items-center justify-center space-x-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v2H7a1 1 0 100 2h2v2a1 1 0 102 0v-2h2a1 1 0 100-2h-2V7z"
                  clip-rule="evenodd" />
              </svg>
              <span>Ajouter un contrat</span>
            </button>
          </div>
        </template>


        <!-- Expired Contract Warning -->
        <div v-else-if="isContractExpired" class="bg-yellow-50 p-4 rounded-[5px]">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-yellow-800 font-medium mb-2">Contrat de <span class="font-bold text-yellow-900 italic">{{
                userData?.username }}</span> est expiré.</p>
              <p class="text-yellow-700 text-sm">Veuillez créer un nouveau contrat pour continuer à travailler.</p>
            </div>
            <button @click="navigateToContractCreate"
              class="bg-yellow-500 text-white py-2 px-4 rounded-[5px] hover:bg-yellow-600 transition duration-200">
              Créer nouveau contrat
            </button>
          </div>
        </div>

        <!-- No Contract State -->
        <template v-else>
          <div class="text-center py-8">
            <p class="text-gray-500 mb-4">Aucun contrat actif trouvé</p>
            <div v-if="authStore.user.role === 'admin'">
              <button @click="navigateToContractCreate"
                class="w-full bg-[#006AFF] text-white py-2.5 px-4 rounded-[5px] hover:bg-[#006AFF]/90 transition-all duration-200">
                Créer nouveau contrat
              </button>
            </div>
          </div>
        </template>
      </div>


      <!-- Demand Section -->
      <div v-if="this.authStore.user.id != this.$route.params.id"
        class="md:col-span-2 bg-white rounded-[8px] shadow-lg p-6">


        <div class=" w-full">
          <div class="flex flex-col justify-between w-full">
            <div class="w-full flex justify-between">
              <div class="w-1/3">
                <div class="relative flex items-center ">
                  <input type="text" placeholder="Search" v-model="searchValue"
                    class="w-full p-2.5 rounded-[5px] pl-4 pr-32 border border-[#99C4FF]  focus:outline-none focus:ring-2 focus:ring-[#006AFF]/50 focus:border-[#006AFF] transition-all duration-200">
                  <button @click="search"
                    class="absolute right-0  rounded-r-[5px] h-full px-4  bg-[#006AFF] text-white hover:bg-[#006AFF]/90 transition-all duration-200 flex items-center justify-center gap-2">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24"
                      stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                    </svg>
                  </button>
                </div>
              </div>
<<<<<<< HEAD
              <div class="w-36">
                <button @click="toggleFilterDropdown"
                  class="w-full p-2.5 rounded-[5px]  bg-[#006AFF] text-white hover:bg-[#006AFF]/90 transition-all duration-200 flex items-center justify-center gap-2">
                  <span>Filter</span>
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24"
                    stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                  </svg>
                </button>
              </div>
            </div>
            <div class="w-full flex justify-end">
              <div class="w-1/3 relative justify-end">
                <!-- Filter Dropdown with Transition -->
                <transition name="dropdown">
                  <div v-if="showFilterDropdown"
                    class="absolute top-full left-0 bg-white rounded-md border border-gray-300 w-full p-3 mt-2 z-10 shadow-lg">
                    <div v-for="(filter, index) in filterStructure" :key="index">
                      <p class="text-sm font-semibold text-gray-600 p-1.5">{{ filter.name }}</p>
                      <div class="flex justify-evenly p-3">
                        <a href="#" @click.prevent="selectFilter(value)" v-for="(value, index) in filter.values"
                          :key="index"
                          class="rounded-full py-1.5 px-5 hover:bg-[#99CAFF] hover:text-white transition-all ease-in-out"
                          :class="{ 'bg-[#3b82f6] text-white': value.selected === true }">
                          <p class="text-sm">{{ value.title }}</p>
                        </a>
                      </div>
                    </div>
                    <div class="flex gap-5 mt-3">
                      <button @click="filter" class="w-1/2 p-2 rounded bg-[#3b82f6] text-white">Filtre</button>
                      <button @click="resertFilter" class="w-1/2 p-2 rounded bg-gray-300">Réinitialiser le
                        filtre</button>
                    </div>
                  </div>
                </transition>
              </div>
            </div>
            <div class="w-full">
              <TableComponent :tableInfo="managerTableInfo" :pageSize="pageSize" :currentPage="currentPage"
                :totalPages="totalPages"></TableComponent>
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from "@/stores/store";
import utils from "@/shared/utils";
import services from "@/shared/services";
import TableComponent from '@/components/TableComponent.vue';
import TableFilterComponent from "@/components/TableFilterComponent.vue";

export default {
  name: 'UserProfile',

  components: {
    TableComponent,
    TableFilterComponent
  },

  data() {
    return {
      loading: false,
      error: null,
      userData: null,
      managerUsername: null,
      pageSize: 10,
      currentPage: 1,
      totalPages: 1,
      count: 0,
      authStore: useAuthStore(),
      showContractHistoryModal: false,
      inactiveContracts: [],
      employeeTableInfo: {
        headers: [
          { title: "Type de demand", key: "typeTitle" },
          { title: "Status", key: "statusTitle" },
          { title: "created_date", key: "created_date" },
          { title: "Actions", key: "actions" }
        ],
        data: [],
        buttons: [
          {
            button: `<button style='background-color : #023047; padding : 3px; color : white;border-radius : 2px ; border : none'><img width="28" height="28" src="https://img.icons8.com/sf-black-filled/50/FFFFFF/pdf-2.png" alt="pdf-2"/></button>`,
            action: this.downloadDemand,
            disabled: false
          },
        ],
      },

      managerTableInfo: {
        headers: [
          { title: "Type de demand", key: "typeTitle" },
          { title: "Username", key: "username" },
          { title: "Status", key: "statusTitle" },
          { title: "Created date", key: "created_date" },
          { title: "Actions", key: "actions" }
        ],
        data: [],
        buttons: [
          {
            button: `<button style='background-color : #38b000; padding : 9px; color : white;border-radius : 2px ; border : none'><img width="18" height="20" src="https://img.icons8.com/external-tal-revivo-bold-tal-revivo/24/FFFFFF/external-select-checkmark-symbol-to-choose-true-answer-basic-bold-tal-revivo.png" alt="external-select-checkmark-symbol-to-choose-true-answer-basic-bold-tal-revivo"/></button>`,
            action: this.approveDemand,
            disabled: (demand) => demand.status === "approved"
          },
          {
            button: `<button style='background-color : #d90429; padding : 9px; color : white;border-radius : 2px ; border : none'><img width="18" height="20" src="https://img.icons8.com/ios-filled/50/FFFFFF/cancel-2.png" alt="cancel-2"/></button>`,
            action: this.rejecetDemand,
            disabled: (demand) => demand.status === "rejected"
          },
          {
            button: `<button style='background-color : #023047; padding : 5px; color : white;border-radius : 2px ; border : none'><img width="27" height="28" src="https://img.icons8.com/sf-black-filled/50/FFFFFF/pdf-2.png" alt="pdf-2"/></button>`,
            action: this.downloadDemand,
            disabled: false
          }
        ],
      },

      //filter structure
      filterStructure: [
        {
          name: "Type",
          values: [
            {
              title: "Demande Conge",
              value: "demande_conge",
              key: "type",
              selected: false
            },
            {
              title: "Ordre De Mission",
              value: "ordre_de_mission",
              key: "type",
              selected: false
            },
            {
              title: "Attestation De Travail",
              value: "attestation_de_travail",
              key: "type",
              selected: false
            },
          ]
        },
        {
          name: "Status",
          values: [
            {
              title: "Acceptée",
              value: "approved",
              key: "status",
              selected: false
            },
            {
              title: "Rejectée",
              value: "rejected",
              key: "status",
              selected: false
            },
            {
              title: "En attende",
              value: "pending",
              key: "status",
              selected: false
            },
          ]
        },
      ],
      userId: null,
      contractId: null,
      showFilterDropdown: false,
      searchValue: null,
      filterData: {
        type: [],
        status: [],
      },
    }
  },

  methods: {

    toggleFilterDropdown() {
      this.showFilterDropdown = !this.showFilterDropdown;
    },


    async fetchManagerDetails(managerId) {
      try {
        const response = await utils.fetch_methode(services.user.profile, {
          user_id: managerId
        });

        if (response.ok) {
          const responseData = await response.json();
          if (responseData.data && responseData.data[0]) {
            this.managerUsername = responseData.data[0].username;
            console.log('Manager username fetched:', this.managerUsername);
          }
        } else {
          console.error('Failed to fetch manager details:', response.status);
        }
      } catch (err) {
        console.error('Error fetching manager details:', err);
      }
    },

    async fetchUserProfile() {
      this.loading = true;
      this.error = null;
      console.log('Starting profile fetch for user:', this.authStore.user?.id);

      try {
        const response = await utils.fetch_methode(services.user.profile, {
          user_id: this.userId
        });
        console.log(response)
        if (response.ok) {
          const responseData = await response.json();
          console.log('Profile data received:', responseData);

          if (responseData.data && responseData.data[0]) {
            this.userData = responseData.data[0];
            this.contractId = this.userData.contracts[0]?._id;
            console.log('User profile data set:', this.userData);

            if (this.userData.manager_id) {
              await this.fetchManagerDetails(this.userData.manager_id);
            }
          } else {
            console.log('No profile data found');
            this.userData = null;
          }
        } else {
          console.error('Failed to fetch profile:', response.status);
          this.error = 'Failed to fetch profile data';
        }
      } catch (err) {
        console.error('Error fetching profile:', err);
        this.error = 'An error occurred while fetching the profile';
      } finally {
        this.loading = false;
      }
    },

    //fetch demand list
    async fetchDemands() {
      try {
        const response = await utils.fetch_methode(services.demand.listByUser, {

          query: {
            filter: {
              type: this.filterData.type,
              status: this.filterData.status,
            },
            search: this.searchValue,

            user_id: this.userId

          },
          options: { "page": this.currentPage, "limit": this.pageSize }
        })
        const data = await response.json();

        if (response.ok) {

          //pagination
          this.count = data.count;
          this.count += (this.currentPage - 1) * this.pageSize;

          if (this.count > this.pageSize) {
            this.totalPages = Math.ceil(this.count / this.pageSize);
          }

          const processedData = data.data.map((item) => ({
            //create new object from the original objct
            ...item,
            //chaneg type value to new value
            typeTitle: utils.formatString(item.type),
            statusTitle: item.status === "approved" ? "Acceptée" : item.status === "rejected" ? "Rejectée" : item.status === "pending" ? "En attente" : item.status,
          }));

          if (this.authStore.user.id == this.$route.params.id) {
            this.employeeTableInfo.data = processedData;
          } else {
            this.managerTableInfo.data = processedData;
          }
        } else {
          console.log(response);
        }
      } catch (err) {
        console.log(err);
      }
    },

    async downloadDemand(demand) {
      const query = { filepath: demand.file_path }
      try {
        const response = await utils.fetch_methode(services.file.download, query);


        if (demand.file_path.toLowerCase().endsWith('.pdf')) {

          window.open(URL.createObjectURL(await response.blob()), '_blank');
        } else {

          const blob = await response.blob();
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = demand.file_path.split('/').pop();
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          window.URL.revokeObjectURL(url);
        }
      } catch (err) {
        console.error("Erreur de téléchargement:", err);
        utils.errorAlert("Impossible de visualiser ou télécharger le fichier");

      }
    },

    //approve demand by manager
    async approveDemand(demand) {
      try {
        if (demand.status !== "approved") {
          const response = await utils.fetch_methode(services.demand.update, { demand_id: demand._id, status: "approved" });
          const data = await response.json();

          if (response.ok) {
            console.log(data);
            utils.successAlert("La demande a été approuvée")
            this.fetchUserProfile()
          } else {
            console.log(response);
          }
        }
      } catch (err) {
        console.log(err);
      }
    },

    showContractHistory() {
      // Filter out inactive contracts from the user's contracts
      this.inactiveContracts = this.userData.contracts
        ? this.userData.contracts.filter(contract => !contract.status)
        : [];
      this.showContractHistoryModal = true;
    },

    //reject demand by manager
    async rejecetDemand(demand) {
      try {
        if (demand.status !== "rejected") {
          const response = await utils.fetch_methode(services.demand.update, { demand_id: demand._id, status: "rejected" });
          const data = await response.json();

          if (response.ok) {
            console.log(data);
            utils.successAlert("La demande a été rejetée")
            this.fetchUserProfile()
          } else {
            console.log(response);
            console.log(JSON.stringify(this.user));

          }
        }
      } catch (err) {
        console.log(err);
      }
    },

    navigateToContractCreate() {
      this.$router.push({ name: "contract_create", params: { id: this.userId } });
    },

    navigateToContractUpdate() {
      this.$router.push({ name: "contract_update", params: { id: this.userId } });
    },

    filter() {
      this.fetchDemands()
    },

    selectFilter(selectedValue) {
      selectedValue.selected = !selectedValue.selected

      const value = selectedValue.value
      const key = selectedValue.key

      if (key == "type") {
        this.filterData.type.push(value)
      }

      if (key == "status") {
        this.filterData.status.push(value)
      }

      console.log(this.filterData);

    },


    //search handle
    search() {
      this.fetchDemands();
    },

    //resert filter
    resertFilter() {
      this.filterData.status = [];
      this.filterData.type = [];
      this.filterStructure.forEach(filter => {
        filter.values.forEach(value => {
          value.selected = false
        });
      });
      this.fetchDemands()
    },

  },

  mounted() {
    this.userId = this.$route.params.id;
    if (this.authStore.user?.id) {
      this.fetchUserProfile();
      this.fetchDemands();
    } else {
      this.error = 'No user is currently logged in';
    }
  },

  computed: {
    isFirstTimeUser() {
      return !this.userData ||
        !this.userData.contracts ||
        this.userData.contracts.length === 0;
    },

    isContractExpired() {
      if (!this.userData || !this.userData.contracts || this.userData.contracts.length === 0) {
        return false;
      }

      const currentContract = this.userData.contracts[0];
      const currentDate = new Date().toISOString().split('T')[0];

      return (
        (currentContract.type === 'cdd' &&
          currentContract.end_date &&
          currentContract.end_date < currentDate) ||
        !currentContract.status
      );
    },

    currentActiveContract() {
      if (!this.userData || !this.userData.contracts) return null;


      return this.userData.contracts.find(contract => contract.status === true);
    }
  }
}
</script>
