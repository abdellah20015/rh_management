<template>
  <div class="container mx-auto px-4 py-8">
    <!-- Main Grid Container -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
      <!-- Profile Section -->
      <div class="bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center space-x-4 mb-6">
          <div class="bg-gray-100 p-4 rounded-full">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-gray-600" fill="none" viewBox="0 0 24 24"
              stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
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
          <div v-if="authStore.user.role === 'employee'"
            class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
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
          <!-- Status -->
          <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
            <span class="font-medium text-gray-600">Status:</span>
            <span class="col-span-2 px-3 py-1 rounded-full text-sm font-medium w-fit"
              :class="userData?.status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
              {{ userData?.status ? 'Active' : 'Inactive' }}
            </span>
          </div>

          <!-- Permissions -->
          <div class="grid gap-3 p-4 bg-gray-50 rounded-lg">
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
      <div class="bg-white rounded-xl shadow-lg p-6">

        <!-- Contract History Modal -->
        <div v-if="showContractHistoryModal"
          class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div class="bg-white rounded-xl shadow-lg w-11/12 max-w-2xl max-h-[80vh] overflow-y-auto">
            <div class="flex justify-between items-center p-6 border-b">
              <h2 class="text-2xl font-bold text-gray-800">Historique des Contrats</h2>
              <button @click="showContractHistoryModal = false" class="text-gray-600 hover:text-gray-900">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24"
                  stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <div v-if="showContractHistoryModal" class="bg-white shadow-md rounded-lg overflow-hidden">
              <table class="w-full">
                <thead class="bg-gray-100 border-b">
                  <tr>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date Début</th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date Fin
                    </th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Statut
                    </th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date de Création</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200">
                  <tr v-for="contract in inactiveContracts" :key="contract._id" class="hover:bg-gray-50 transition">
                    <td class="px-4 py-4 whitespace-nowrap">
                      <span class="uppercase text-sm font-medium text-gray-900">{{ contract.type }}</span>
                    </td>
                    <td class="px-4 py-4 whitespace-nowrap">
                      <span class="text-gray-600">{{ contract.start_date }}</span>
                    </td>
                    <td class="px-4 py-4 whitespace-nowrap">
                      <span v-if="contract.type === 'cdd'" class="text-gray-600">
                        {{ contract.end_date || 'N/A' }}
                      </span>
                      <span v-else class="text-gray-400">-</span>
                    </td>
                    <td class="px-4 py-4 whitespace-nowrap">
                      <span class="px-2 py-1 rounded-full text-xs font-medium"
                        :class="contract.status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
                        {{ contract.status ? 'Actif' : 'Inactif' }}
                      </span>
                    </td>
                    <td class="px-4 py-4 whitespace-nowrap text-sm text-gray-500">
                      {{ contract.date_creation }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div class="flex items-center space-x-4 mb-6">
          <div class="bg-gray-100 p-4 rounded-full">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-gray-600" fill="none" viewBox="0 0 24 24"
              stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
          <div>
            <h2 class="text-2xl font-bold text-gray-800">Contract Information</h2>
            <p class="text-gray-500">Employment Details</p>
          </div>
        </div>


        <div v-if="authStore.user.role === 'admin'" class="mt-4">
          <button @click="showContractHistory"
            class="w-full bg-gray-200 text-gray-700 py-2 px-4 rounded-lg hover:bg-gray-300 transition duration-200">
            Historique des Contrats
          </button>
        </div>
        <br>
        <!-- Loading State -->
        <div v-if="loading" class="text-center py-8">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 mx-auto"></div>
          <p class="mt-4 text-gray-600">Loading profile information...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="text-center py-8">
          <div class="text-red-500 mb-4">{{ error }}</div>
          <button @click="fetchUserProfile"
            class="bg-gray-200 text-gray-700 py-2 px-4 rounded-lg hover:bg-gray-300 transition duration-200">
            Retry
          </button>
        </div>

        <!-- Current Contract Display -->
        <template v-else-if="currentActiveContract">
          <div class="grid gap-4">
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">Type:</span>
              <span class="col-span-2 uppercase text-gray-900">{{ currentActiveContract.type }}</span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">Start Date:</span>
              <span class="col-span-2 text-gray-900">{{ currentActiveContract.start_date }}</span>
            </div>
            <div v-if="currentActiveContract.type === 'cdd'"
              class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">End Date:</span>
              <span class="col-span-2 text-gray-900">{{ currentActiveContract.end_date }}</span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">Leave Balance:</span>
              <span class="col-span-2 text-gray-900">{{ currentActiveContract.leave_balance }} days</span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">Salary:</span>
              <span class="col-span-2 text-gray-900">{{ currentActiveContract.salary }} DH</span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">Status:</span>
              <span class="col-span-2 px-3 py-1 rounded-full text-sm font-medium w-fit"
                :class="currentActiveContract.status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
                {{ currentActiveContract.status ? 'Active' : 'Inactive' }}
              </span>
            </div>
            <div class="grid grid-cols-3 items-center p-3 bg-gray-50 rounded-lg">
              <span class="font-medium text-gray-600">Created On:</span>
              <span class="col-span-2 text-gray-900">{{ currentActiveContract.date_creation }}</span>
            </div>

            <!-- Update Contract Button for Admin (only for active contracts) -->
            <div v-if="authStore.user.role === 'admin' && currentActiveContract.status" class="mt-6">
              <button @click="navigateToContractUpdate"
                class="w-full bg-black text-white py-2 px-4 rounded-lg hover:bg-gray-800 transition duration-200">
                Update Contract
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
              <p class="text-gray-600"><span class="font-bold text-gray-700 italic">{{ userData?.username }}</span>n'a
                pas encore de contrat. Créer son contrat.</p>
            </div>
            <button @click="navigateToContractCreate"
              class="bg-black text-white py-3 px-6 rounded-lg hover:bg-gray-800 transition duration-200 inline-flex items-center justify-center space-x-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v2H7a1 1 0 100 2h2v2a1 1 0 102 0v-2h2a1 1 0 100-2h-2V7z"
                  clip-rule="evenodd" />
              </svg>
              <span>Ajouter un Contrat</span>
            </button>
          </div>
        </template>


        <!-- Expired Contract Warning -->
        <div v-else-if="isContractExpired" class="bg-yellow-50 p-4 rounded-lg">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-yellow-800 font-medium mb-2">Contrat de <span class="font-bold text-yellow-900 italic">{{
                  userData?.username }}</span> est expiré.</p>
              <p class="text-yellow-700 text-sm">Please create a new contract to continue working.</p>
            </div>
            <button @click="navigateToContractCreate"
              class="bg-yellow-500 text-white py-2 px-4 rounded-lg hover:bg-yellow-600 transition duration-200">
              Create New Contract
            </button>
          </div>
        </div>

        <!-- No Contract State -->
        <template v-else>
          <div class="text-center py-8">
            <p class="text-gray-500 mb-4">No active contract found</p>
            <div v-if="authStore.user.role === 'admin'">
              <button @click="navigateToContractCreate"
                class="w-full bg-black text-white py-2 px-4 rounded-lg hover:bg-gray-800 transition duration-200">
                Create New Contract
              </button>
            </div>
          </div>
        </template>
      </div>



      <div class="md:col-span-2 bg-white rounded-xl shadow-lg p-6">
        <div class="mb-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-800">List de Demandes</h2>
            <p class="text-gray-500">Liste des demandes en attente</p>
          </div>
        </div>
        <div class="flex items-center justify-between p-5 ">
          <TableFilterComponent :filterStructure="filterStructure" @filterHandler="filterHandler"
            @searchHandler="searchHandler" @resertFilterHandler="resertFilterHandler">
          </TableFilterComponent>
        </div>
        <div>
          <TableComponent :tableInfo="employeeTableInfo" :pageSize="pageSize" :currentPage="currentPage"
            :totalPages="totalPages"></TableComponent>
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
            button: `<button style='background-color : #38b000; padding : 7px; color : white;border-radius : 2px ; border : none'><img width="20" height="20" src="https://img.icons8.com/external-tal-revivo-bold-tal-revivo/24/FFFFFF/external-select-checkmark-symbol-to-choose-true-answer-basic-bold-tal-revivo.png" alt="external-select-checkmark-symbol-to-choose-true-answer-basic-bold-tal-revivo"/></button>`,
            action: this.approveDemand,
            disabled: (demand) => demand.status === "approved" || useAuthStore().user.id === this.userId
          },
          {
            button: `<button style='background-color : #d90429; padding : 7px; color : white;border-radius : 2px ; border : none'><img width="20" height="20" src="https://img.icons8.com/ios-filled/50/FFFFFF/cancel-2.png" alt="cancel-2"/></button>`,
            action: this.rejecetDemand,
            disabled: (demand) => demand.status === "rejected" || useAuthStore().user.id === this.userId
          },
          {
            button: `<button style='background-color : #023047; padding : 3px; color : white;border-radius : 2px ; border : none'><img width="28" height="28" src="https://img.icons8.com/sf-black-filled/50/FFFFFF/pdf-2.png" alt="pdf-2"/></button>`,
            action: this.downloadDemand,
            disabled: false
          },
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
      contractId: null
    }
  },

  methods: {
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

            //pagination
            this.count = responseData.count;
            this.count += (this.currentPage - 1) * this.pageSize;
            console.log(this.count);
            if (this.count > this.pageSize) {
              this.totalPages = Math.ceil(this.count / this.pageSize);
            }

            //to add fields that will show in the table
            const processedData = this.userData.demands.map((item) => ({
              //create now object from the original objct
              ...item,
              //chaneg type value to new value
              typeTitle: utils.formatString(item.type),
              statusTitle: item.status === "approved" ? "Acceptée" : item.status === "rejected" ? "Rejectée" : item.status === "pending" ? "En attente" : item.status,
            }));

            // console.log(processedData);
            this.employeeTableInfo.data = processedData

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

    //hanlder filter
    filterHandler(filterData) {
      const currentData = this.employeeTableInfo.data;

      if (filterData.length > 0) {
        // Group filters by field name
        const groupedFilters = {};
        filterData.forEach(filter => {
          const fieldName = Object.keys(filter)[0];
          const value = filter[fieldName];

          if (!groupedFilters[fieldName]) {
            groupedFilters[fieldName] = [];
          }
          groupedFilters[fieldName].push(value);
        });

        // Filter data based on grouped filters
        const filteredData = currentData.filter((demande) => {
          return Object.keys(groupedFilters).every((fieldName) => {
            return groupedFilters[fieldName].some(filterValue =>
              String(demande[fieldName]).toLowerCase() === String(filterValue).toLowerCase()
            );
          });
        });

        // update table with filtred data
        this.employeeTableInfo.data = filteredData;
      }
    },


    //search handle
    searchHandler(searchValue) {

      //get demande if value empty
      if (searchValue == "") {
        this.fetchDemands()
      }

      const currentData = this.employeeTableInfo.data;

      if (!searchValue) {
        return currentData;
      }

      const searchedData = currentData.filter((demande) => {
        return Object.values(demande).some(value =>
          String(value).toLowerCase().includes(searchValue.toLowerCase())
        );
      });

      this.employeeTableInfo.data = searchedData;

    },

    //resert filter
    resertFilterHandler() {
      this.fetchUserProfile();
    }

  },

  mounted() {
    this.userId = this.$route.params.id;
    if (this.authStore.user?.id) {
      this.fetchUserProfile();
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
