<template>
  <div class="flex justify-center">
    <div class="w-11/12">
      <div class="flex items-center justify-between p-5 my-3">
        <p class="text-2xl font-semibold ">Listes des utilisateurs</p>
        <router-link v-if="authStore.user.permissions.includes('create_user')" :to="{ name: 'create_user' }"
          class="w-48 bg-black text-white text-center rounded p-2">
          Ajouter un utilisateur
        </router-link>
      </div>
      <div class="flex items-center justify-between p-5">
        <div class="w-full flex justify-between">
          <div class="w-1/3">
            <input type="text" placeholder="Search" v-model="searchValue"
              class="p-2 border border-gray-300 w-2/3 mr-2 rounded ">
            <button @click="search" class="w-1/4 p-2 rounded bg-black text-white">Search</button>
          </div>
          <div class="w-36">
            <button @click="toggleFilterDropdown"
              class="w-full p-2 rounded bg-black text-white flex justify-evenly items-center">
              <p>Filter</p>
              <img width="25" height="25" src="https://img.icons8.com/sf-black/64/FFFFFF/expand-arrow.png"
                alt="expand-arrow" />
            </button>
          </div>
        </div>
        <div class="w-1/2 relative justify-end">
          <!-- Filter Dropdown with Transition -->
          <transition name="dropdown">
            <div v-if="showFilterDropdown"
              class="absolute top-full left-0 bg-white rounded-md border border-gray-300 w-full p-3 mt-2 z-10 shadow-lg">
              <div v-for="(filter, index) in filterStructure" :key="index">
                <p class="text-sm font-semibold text-gray-600 p-2">{{ filter.name }}</p>
                <div class="flex justify-evenly p-3">
                  <a href="#" @click.prevent="selectFilter(value)" v-for="(value, index) in filter.values" :key="index"
                    class="rounded-full py-2 px-5 hover:bg-[#99CAFF] hover:text-white transition-all ease-in-out"
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
          { title: "Type de contrat", key: "contractTypeTitle" },
          { title: "Status", key: "userStatusTitle" },
          { title: "Role", key: "role" },
          { title: "Actions", key: "actions" },
        ],
        data: [],
        buttons: [
          {
            button: `<button style='background-color: #3588B4; padding: 7px; color: white; border-radius: 2px; border: none;'><img  width="20" height="20"  src="https://img.icons8.com/ios-filled/50/FFFFFF/visible.png" alt="View Icon" />
</button>`,
            action: this.viewUser,
            disabled: false,
          },
          {
            button: `<button style='background-color: #C1121F; padding: 7px; color: white; border-radius: 2px; border: none;'><img  width="20" height="20"  src="https://img.icons8.com/ios-filled/50/FFFFFF/trash.png" alt="Delete Icon" /></button>`,
            action: this.deleteUser,
            disabled: () =>
              !this.authStore.user.permissions.includes("delete_user"),
          },
          {
            button: `<button style='background-color: #FFBE0B; padding: 7px; color: white; border-radius: 2px; border: none;'>
                          <img width="20" height="20" src="https://img.icons8.com/ios-filled/50/FFFFFF/pencil--v1.png" alt="Pencil Icon" />
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
            userStatusTitle: user.status == true ? "Active" : "Désactivé"
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