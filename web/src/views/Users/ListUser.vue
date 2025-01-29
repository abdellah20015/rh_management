<template>
  <div class="flex justify-center">
    <div class="w-11/12">
      <div class="flex items-center justify-between mt-5 mb-14">
        <div class="flex  gap-3">
          <p class="text-3xl font-semibold">Listes des utilisateurs</p>
          <p class="text-[#006aff] bg-[#c6dffb] py-1 px-3 rounded-[6px] font-semibold text-base">
            {{ count }}</p>
        </div>
        <router-link v-if="authStore.user.permissions.includes('create_user')" :to="{ name: 'create_user' }"
          class="w-48 bg-[#006AFF] hover:bg-[#006AFF]/80 transition-all ease-in-out  text-white text-center rounded p-2">
          Ajouter un utilisateur
        </router-link>
      </div>
      <div class="flex flex-col justify-between w-full">
        <div class="w-full flex justify-between">
          <div class="w-1/3">
            <div class="relative flex items-center">
              <input type="text" placeholder="Search" v-model="searchValue"
                class="w-full rounded-[5px] p-2.5 pl-4 pr-32 border border-[#99C4FF] focus:outline-none focus:ring-2 focus:ring-[#006AFF]/50 focus:border-[#006AFF] transition-all duration-200">
              <button @click="search"
                class="absolute rounded-r-[5px] right-0 h-full px-4 bg-[#006AFF] text-white hover:bg-[#006AFF]/90 transition-all duration-200 flex items-center justify-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24"
                  stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
                <span>Rechercher</span>
              </button>
            </div>
          </div>
          <div class="w-36">
            <button @click="toggleFilterDropdown"
              class="w-full rounded-[5px] p-2.5 bg-[#006AFF] text-white hover:bg-[#006AFF]/90 transition-all duration-200 flex items-center justify-center gap-2">
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
      </div>
      <div>
        <TableComponent :tableInfo="tableInfo" :pageSize="pageSize" :currentPage="currentPage" :totalPages="totalPages"
          @page-changed="handlePageChange" />
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from "@/stores/store";
import TableComponent from "@/components/TableComponent.vue";
import services from "@/shared/services";
import utils from "@/shared/utils";
import TableFilterComponent from "@/components/TableFilterComponent.vue";

export default {
  name: "ListUser",
  components: {
    TableComponent,
    TableFilterComponent,
  },
  data() {
    return {
      tableInfo: {
        headers: [
          { title: "User Name", key: "username" },
          { title: "Nom et Prenom", key: "fullname" },
          { title: "Type de contrat", key: "contractTypeTitle" },
          { title: "Status", key: "userStatusTitle" },
          { title: "Role", key: "role" },
          { title: "Actions", key: "actions" },
        ],
        data: [],
        buttons: [
          {
            button: `<button style='background-color: #006AFF; padding: 8px; border-radius: 2px; border: none;'><img  width="17" height="20"  src="https://img.icons8.com/ios-filled/50/FFFFFF/visible.png" alt="View Icon" /></button>`,
            action: this.viewUser,
            disabled: false,
          },
          {
            button: `<button style='background-color: #C1121F; padding: 8px; color: white; border-radius: 2px; border: none;'><img  width="17" height="20"  src="https://img.icons8.com/ios-filled/50/FFFFFF/trash.png" alt="Delete Icon" /></button>`,
            action: this.deleteUser,
            disabled: () =>
              !this.authStore.user.permissions.includes("delete_user"),
          },
          {
            button: `<button style='background-color: #FFBE0B; padding: 8px; color: white; border-radius: 2px; border: none;'>
                          <img width="17" height="20" src="https://img.icons8.com/ios-filled/50/FFFFFF/pencil--v1.png" alt="Pencil Icon" />
                    </button>`,
            action: this.updateUser,
            disabled: () =>
              !this.authStore.user.permissions.includes("update_user"),
          },
        ],
      },

      filterStructure: [
        {
          name: "Role",
          values: [
            {
              title: "Manager",
              value: "manager",
              key: "role",
              selected: false,
            },
            {
              title: "Employee",
              value: "employee",
              key: "role",
              selected: false,
            },
            {
              title: "Admin",
              value: "admin",
              key: "role",
              selected: false,
            },
          ],
        },
        {
          name: "Type de contrat",
          values: [
            {
              title: "CDD",
              value: "cdd",
              key: "type",
              selected: false,
            },
            {
              title: "CDI",
              value: "cdi",
              key: "type",
              selected: false,
            }
          ],
        },
      ],
      pageSize: 10,
      currentPage: 1,
      count: 0,
      totalPages: 1,
      searchValue: null,
      filterData: {
        role: [],
        type: [],
      },
      showFilterDropdown: false
    };
  },
  computed: {
    authStore() {
      return useAuthStore();
    },
  },
  methods: {
    async fetchUsers() {
      try {
        const payload = {
          query: {
            filter: {
              role: this.filterData.role,
              type: this.filterData.type,
            },
            search: this.searchValue,
            page: this.currentPage,
            limit: this.pageSize,
          },
        };

        const response = await utils.fetch_methode(services.user.list, payload);
        const data = await response.json();
        console.log(data);

        if (response.ok) {

          //pagination
          this.count = data.count;
          this.count += (this.currentPage - 1) * this.pageSize;
          console.log(this.count);

          if (this.count > this.pageSize) {
            this.totalPages = Math.ceil(this.count / this.pageSize);
          }

          this.tableInfo.data = data.data.map((user) => ({
            ...user,
            contractTypeTitle: user?.contracts?.type == "cdd" ? "CDD" : user?.contracts?.type == "cdi" ? "CDI" : "Pas de contrat",
            userStatusTitle: user.status == true ? "Active" : "Désactivé",
            fullname : user.firstName != null && user.lastName != null ? user.firstName + " " + user.lastName : "-",
          }));

        } else {
          console.error(data);
        }
      } catch (error) {
        console.error(
          "Erreur lors de la récupération des utilisateurs:",
          error
        );
      }
    },
    handlePageChange(newPage) {
      this.currentPage = newPage;
      this.fetchUsers();
    },
    viewUser(user) {
      this.$router.push({ name: "details_user", params: { id: user._id } });
    },
    async deleteUser(user) {
      const response = await utils.fetch_methode(services.user.delete, {
        user_id: user._id,
      });
      const res = await response.json()
      if (response.ok) {
        utils.successAlert("User successfully deleted.");
        this.fetchUsers();
      } else {
        utils.errorAlert("Error: Failed to delete user. Please try again.");
        console.log(res);
      }
    },
    updateUser(user) {
      this.$router.push({ name: "update_user", params: { id: user._id } });
    },


    toggleFilterDropdown() {
      this.showFilterDropdown = !this.showFilterDropdown;
    },

    //filter handler
    filter() {
      this.fetchUsers()
    },

    //select filter data to filter with
    selectFilter(selectedValue) {
      console.log(selectedValue);

      selectedValue.selected = !selectedValue.selected

      const value = selectedValue.value
      const key = selectedValue.key

      if (key == "role") {
        this.filterData.role.push(value)
      }

      if (key == "type") {
        this.filterData.type.push(value)
      }

      console.log(this.filterData);

    },


    //search handle
    search() {
      this.fetchUsers();
    },

    //resert filter
    resertFilter() {
      this.filterData.role = [];
      this.filterData.type = [];
      this.filterStructure.forEach(filter => {
        filter.values.forEach(value => {
          value.selected = false
        });
      });
      this.fetchUsers()
    },

    handlePageChange(newPage) {
      this.currentPage = newPage;
      this.fetchUsers();
    },

  },
  mounted() {
    this.fetchUsers();
  },
};
</script>

<style scoped>
/* Transition for dropdown */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>