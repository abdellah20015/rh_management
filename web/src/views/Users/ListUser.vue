<template>
  <div class="flex justify-center">
    <div class="w-11/12">
      <div class="flex items-center justify-between p-5 my-3">
        <p class="text-2xl font-semibold ">Listes des utilisateurs</p>
        <router-link :to="{ name: 'create_user' }" class="w-48 bg-black text-white text-center rounded p-2">
          Ajouter un utilisateur
        </router-link>
      </div>
      <div class="flex items-center justify-between p-5">
        <TableFilterComponent :filterStructure="filterStructure" @filterHandler="filterHandler"
          @searchHandler="searchHandler" @resertFilterHandler="resertFilterHandler">
        </TableFilterComponent>
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
              value: "ADMIN",
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
              value: "CDD",
              key: "contractTypeTitle",
              selected: false,
            },
            {
              title: "CDI",
              value: "CDI",
              key: "contractTypeTitle",
              selected: false,
            },
            {
              title: "Pas de contrat",
              value: "Pas de contrat",
              key: "contractTypeTitle",
              selected: false,
            },
          ],
        },
      ],
      pageSize: 10,
      currentPage: 1,
      count: 0,
      totalPages: 1,
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
            contractTypeTitle: user?.contracts[0]?.type == "cdd" ? "CDD" : user?.contracts[0]?.type == "cdi" ? "CDI" : "Pas de contrat",
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
      this.$router.push({ name: "details_user" , params : {id : user._id} });
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

    filterHandler(filterData) {
      const currentData = this.tableInfo.data;
      if (filterData.length > 0) {
        const groupedFilters = {};
        filterData.forEach((filter) => {
          const fieldName = Object.keys(filter)[0];
          const value = filter[fieldName];
          if (!groupedFilters[fieldName]) {
            groupedFilters[fieldName] = [];
          }
          groupedFilters[fieldName].push(value);
        });

        const filteredData = currentData.filter((user) => {
          return Object.keys(groupedFilters).every((fieldName) => {
            return groupedFilters[fieldName].some(
              (filterValue) =>
                String(user[fieldName]).toLowerCase() ===
                String(filterValue).toLowerCase()
            );
          });
        });

        this.tableInfo.data = filteredData;
      }
    },

    searchHandler(searchValue) {
      if (!searchValue) {
        this.fetchUsers();
        return;
      }

      const searchedData = this.tableInfo.data.filter((user) => {
        return Object.values(user).some((value) =>
          String(value).toLowerCase().includes(searchValue.toLowerCase())
        );
      });

      this.tableInfo.data = searchedData;
    },

    resertFilterHandler() {
      this.fetchUsers();
    },
  },
  mounted() {
    this.fetchUsers();
  },
};
</script>
