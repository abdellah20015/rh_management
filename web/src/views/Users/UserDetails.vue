<template>
  <div class="container mx-auto px-4 py-8">
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
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

        <div class="space-y-4">
          <div class="flex items-center p-3 bg-gray-50 rounded-lg">
            <span class="font-medium text-gray-600 w-1/3">Username:</span>
            <span class="text-gray-900">{{ userProfile?.username }}</span>
          </div>

          <div class="flex items-center p-3 bg-gray-50 rounded-lg">
            <span class="font-medium text-gray-600 w-1/3">Role:</span>
            <span class="text-gray-900">{{ userProfile?.role }}</span>
          </div>

          <div class="flex items-center p-3 bg-gray-50 rounded-lg">
            <span class="font-medium text-gray-600 w-1/3">Status:</span>
            <span
              class="px-3 py-1 rounded-full text-sm font-medium"
              :class="userProfile?.status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
            >
              {{ userProfile?.status ? 'Active' : 'Inactive' }}
            </span>
          </div>

          <div class="bg-gray-50 p-4 rounded-lg">
            <h3 class="font-medium text-gray-600 mb-3">Permissions:</h3>
            <div class="flex flex-wrap gap-2">
              <span
                v-for="permission in userProfile?.permissions"
                :key="permission"
                class="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm"
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

        <template v-if="userProfile?.contracts?.[0]">
          <div class="space-y-4">
            <div class="flex items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600 w-1/3">Contract ID:</span>
              <span class="text-gray-900">{{ userProfile.contracts[0]._id }}</span>
            </div>
            <div class="flex items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600 w-1/3">Type:</span>
              <span class="uppercase text-gray-900">{{ userProfile.contracts[0].type }}</span>
            </div>
            <div class="flex items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600 w-1/3">Start Date:</span>
              <span class="text-gray-900">{{ userProfile.contracts[0].start_date }}</span>
            </div>
            <div v-if="userProfile.contracts[0].type === 'cdd'" class="flex items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600 w-1/3">End Date:</span>
              <span class="text-gray-900">{{ userProfile.contracts[0].end_date }}</span>
            </div>
            <div class="flex items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600 w-1/3">Salary:</span>
              <span class="text-gray-900">{{ userProfile.contracts[0].salary }} DH</span>
            </div>
            <div class="mt-6">
              <button
                @click="navigateToContractUpdate"
                class="w-full bg-black text-white py-2 px-4 rounded-lg hover:bg-gray-800 transition duration-200"
              >
                Update Contract
              </button>
            </div>
          </div>
        </template>

        <template v-else>
          <div class="text-center py-8">
            <p class="text-gray-500 mb-4">No active contract found</p>
            <button
              @click="navigateToContractCreate"
              class="w-full bg-black text-white py-2 px-4 rounded-lg hover:bg-gray-800 transition duration-200"
            >
              Create New Contract
            </button>
          </div>
        </template>
      </div>

      <!-- Demands Section -->
      <div class="lg:col-span-2 bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center space-x-4 mb-6">
          <div class="bg-gray-100 p-4 rounded-full">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <div>
            <h2 class="text-2xl font-bold text-gray-800">Demands History</h2>
            <p class="text-gray-500">List of All Demands</p>
          </div>
        </div>

        <template v-if="userProfile?.demands?.length">
          <div class="overflow-x-auto">
            <table class="w-full border-collapse">
              <thead>
                <tr>
                  <th class="p-2 border text-left">Type</th>
                  <th class="p-2 border text-left">Status</th>
                  <th class="p-2 border text-left">Start Date</th>
                  <th class="p-2 border text-left">End Date</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="demand in userProfile.demands" :key="demand._id">
                  <td class="p-2 border">{{ demand.type }}</td>
                  <td class="p-2 border">
                    <span :class="{
                      'px-2 py-1 rounded-full text-sm': true,
                      'bg-green-100 text-green-800': demand.status === 'approved',
                      'bg-yellow-100 text-yellow-800': demand.status === 'pending',
                      'bg-red-100 text-red-800': demand.status === 'rejected'
                    }">
                      {{ demand.status }}
                    </span>
                  </td>
                  <td class="p-2 border">{{ demand.start_date }}</td>
                  <td class="p-2 border">{{ demand.end_date }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </template>
        <template v-else>
          <div class="text-center py-8">
            <p class="text-gray-500">No demands found</p>
          </div>
        </template>
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
const userProfile = ref(null);

const fetchUserProfile = async () => {
  try {
    const response = await fetch_methode(services.user.profile, {
      user_id: authStore.user?._id
    });
    const data = await response.json();
    if (response.ok) {
      userProfile.value = data[0];
    }
  } catch (error) {
    console.error('Error fetching user profile:', error);
  }
};

const navigateToContractCreate = () => {
  router.push('/private/user/contract/create');
};

const navigateToContractUpdate = () => {
  router.push(`/private/user/contract/update/${userProfile.value?.contracts[0]?._id}`);
};

onMounted(() => {
  fetchUserProfile();
});
</script>
