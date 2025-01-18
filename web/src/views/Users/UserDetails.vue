<template>
  <div class="container mx-auto px-4 py-8">
    <!-- Main Grid Container -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
      <!-- Profile Section -->
      <div class="bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center space-x-4 mb-6">
          <div class="bg-gray-100 p-4 rounded-full">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
          </div>
          <div>
            <h2 class="text-2xl font-bold text-gray-800">Profile Details</h2>
            <p class="text-gray-500">Personal Information</p>
          </div>
        </div>
        <div class="grid gap-4">
          <!-- Username -->
          <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
            <span class="font-medium text-gray-600">Username:</span>
            <span class="col-span-2 text-gray-900">{{ userData?.username }}</span>
          </div>

          <!-- Role -->
          <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
            <span class="font-medium text-gray-600">Role:</span>
            <span class="col-span-2 text-gray-900">{{ userData?.role }}</span>
          </div>
          <!-- Manager -->
          <div v-if="authStore.user.role === 'employee'" class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
            <span class="font-medium text-gray-600">Manager:</span>
            <span class="col-span-2 text-gray-900">{{ userData?.manager }}</span>
          </div>
          <!-- Status -->
          <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
            <span class="font-medium text-gray-600">Status:</span>
            <span
              class="col-span-2 px-3 py-1 rounded-full text-sm font-medium w-fit"
              :class="userData?.status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
            >
              {{ userData?.status ? 'Active' : 'Inactive' }}
            </span>
          </div>

          <!-- Permissions -->
          <div class="grid gap-3 p-4 bg-gray-50 rounded-lg">
            <h3 class="font-medium text-gray-600">Permissions:</h3>
            <div class="grid grid-flow-row grid-cols-2 gap-2">
              <span
                v-for="permission in userData?.permission"
                :key="permission"
                class="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm text-center"
              >
                {{ permission }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Contract Section -->
      <div class="bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center space-x-4 mb-6">
          <div class="bg-gray-100 p-4 rounded-full">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
          <div>
            <h2 class="text-2xl font-bold text-gray-800">Contract Information</h2>
            <p class="text-gray-500">Employment Details</p>
          </div>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="text-center py-8">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 mx-auto"></div>
          <p class="mt-4 text-gray-600">Loading profile information...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="text-center py-8">
          <div class="text-red-500 mb-4">{{ error }}</div>
          <button
            @click="fetchUserProfile"
            class="bg-gray-200 text-gray-700 py-2 px-4 rounded-lg hover:bg-gray-300 transition duration-200"
          >
            Retry
          </button>
        </div>

        <!-- Contract Display -->
        <template v-else-if="userData?.contracts?.[0]">
          <div class="grid gap-4">
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">Contract ID:</span>
              <span class="col-span-2 text-gray-900">{{ userData.contracts[0]._id }}</span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">Type:</span>
              <span class="col-span-2 uppercase text-gray-900">{{ userData.contracts[0].type }}</span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">Start Date:</span>
              <span class="col-span-2 text-gray-900">{{ userData.contracts[0].start_date }}</span>
            </div>
            <div v-if="userData.contracts[0].type === 'cdd'" class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">End Date:</span>
              <span class="col-span-2 text-gray-900">{{ userData.contracts[0].end_date }}</span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">Leave Balance:</span>
              <span class="col-span-2 text-gray-900">{{ userData.contracts[0].leave_balance }} days</span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">Salary:</span>
              <span class="col-span-2 text-gray-900">{{ userData.contracts[0].salary }} DH</span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">Status:</span>
              <span
                class="col-span-2 px-3 py-1 rounded-full text-sm font-medium w-fit"
                :class="userData.contracts[0].status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
              >
                {{ userData.contracts[0].status ? 'Active' : 'Inactive' }}
              </span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">Created On:</span>
              <span class="col-span-2 text-gray-900">{{ userData.contracts[0].date_creation }}</span>
            </div>
            <div class="mt-6" v-if="authStore.user.role === 'admin'">
              <button
                @click="navigateToContractUpdate"
                class="w-full bg-black text-white py-2 px-4 rounded-lg hover:bg-gray-800 transition duration-200"
              >
                Update Contract
              </button>
            </div>
          </div>
        </template>

        <!-- No Contract State -->
        <template v-else>
          <div class="text-center py-8">
            <p class="text-gray-500 mb-4">No active contract found</p>
            <div v-if="authStore.user.role === 'admin'">
              <button
                @click="navigateToContractCreate"
                class="w-full bg-black text-white py-2 px-4 rounded-lg hover:bg-gray-800 transition duration-200"
              >
                Create New Contract
              </button>
            </div>
          </div>
        </template>
      </div>


      <div class="md:col-span-2 bg-white rounded-xl shadow-lg p-6">
        <div class="mb-6">
          <div class="bg-gray-100 p-4 rounded-full inline-block">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 7a4 4 0 018 0v1h4V7a4 4 0 118 0v1h1a1 1 0 011 1v13a1 1 0 01-1 1H4a1 1 0 01-1-1V9a1 1 0 011-1h1V7a4 4 0 0112 0v1h4V7a4 4 0 11-8 0z" />
            </svg>
          </div>
          <div>
            <h2 class="text-2xl font-bold text-gray-800">List de Demandes</h2>
            <p class="text-gray-500">Liste des demandes en attente</p>
          </div>
        </div>

        <!-- Demands List -->
        <div class="space-y-4">
          <template v-if="userData?.demands?.length">
            <div v-for="demand in userData.demands" :key="demand._id" class="p-4 bg-gray-50 rounded-lg">
              <div class="grid grid-cols-2 items-center mb-4">
                <span class="font-medium text-gray-900">
                  {{ demand.type.replace(/_/g, ' ').toUpperCase() }}
                </span>
                <div class="justify-self-end flex items-center gap-2">
                  <span class="px-3 py-1 rounded-full text-sm font-medium"
                    :class="{
                      'bg-yellow-100 text-yellow-800': demand.status === 'pending',
                      'bg-green-100 text-green-800': demand.status === 'approved',
                      'bg-red-100 text-red-800': demand.status === 'rejected'
                    }">
                    {{ demand.status.toUpperCase() }}
                  </span>
                  <button
            @click="showDemandDetails(demand._id)"
            class="flex items-center gap-2 px-4 py-2 bg-blue-50 text-gray-500 rounded-lg hover:bg-blue-100 transition-colors duration-200"
          >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M2.036 12.322a1.012 1.012 0 010-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178z" />
              <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>

          </button>
                </div>
              </div>

              <div class="text-sm text-gray-600">
                <div v-if="demand.details" class="grid grid-cols-2 gap-2">
                  <div v-for="(value, key) in demand.details" :key="key">
                    <div class="font-medium">{{ key.replace(/_/g, ' ').toUpperCase() }}:</div>
                    <div>{{ value }}</div>
                  </div>
                </div>
                <div class="mt-2">
                  <span class="font-medium">Created:</span>
                  {{ new Date(demand.created_date).toLocaleDateString() }}
                </div>
              </div>
            </div>
          </template>
          <div v-else class="text-center py-4 text-gray-500">
            Aucune demande trouvée
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAuthStore } from "@/stores/store";
import { useRouter } from 'vue-router';
import utils from "@/shared/utils";
import services from "@/shared/services";

const authStore = useAuthStore();
const router = useRouter();
const loading = ref(false);
const error = ref(null);
const userData = ref(null);


const fetchUserProfile = async () => {
  loading.value = true;
  error.value = null;
  console.log('Starting profile fetch for user:', authStore.user?.id);

  try {
    const response = await utils.fetch_methode(services.user.profile, {
      user_id: authStore.user?.id
    });

    if (response.ok) {
      const responseData = await response.json();
      console.log('Profile data received:', responseData);

      if (responseData.data && responseData.data[0]) {
        userData.value = responseData.data[0];
        console.log('User profile data set:', userData.value);
      } else {
        console.log('No profile data found');
        userData.value = null;
      }
    } else {
      console.error('Failed to fetch profile:', response.status);
      error.value = 'Failed to fetch profile data';
    }
  } catch (err) {
    console.error('Error fetching profile:', err);
    error.value = 'An error occurred while fetching the profile';
  } finally {
    loading.value = false;
  }
};


const navigateToContractCreate = () => {
  router.push('/private/user/contract/create');
};

const navigateToContractUpdate = () => {
  if (userData.value?.contracts?.[0]?._id) {
    router.push(`/private/user/contract/update/${userData.value.contracts[0]._id}`);
  }
};


onMounted(() => {
  if (authStore.user?.id) {
    fetchUserProfile();
  } else {
    error.value = 'No user is currently logged in';
  }
});
</script>
