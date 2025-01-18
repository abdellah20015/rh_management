<template>
    <div class="flex justify-center">
      <div class="w-11/12">
        <div class="flex items-center justify-between p-5">
          <p class="text-2xl font-semibold">Listes des utilisateurs</p>
          <router_Link  :to="{ name: 'create_user' }" class="w-48 bg-black text-white rounded p-2">
            Ajouter un utilisateur
          </router_Link>
        </div>
        <div>
          <TableComponent :tableInfo="tableInfo" :pageSize="pageSize" :currentPage="currentPage" :totalPages="totalPages" />
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { useAuthStore } from "@/stores/store";
  import TableComponent from "@/components/TableComponent.vue";
  import services from "@/shared/services";
  import fetch_methode from "@/shared/utils";
import { RouterLink } from "vue-router";
  
  export default {
    name: "ListUser",
    components: {
      TableComponent,
    },
    data() {
      return {
        tableInfo: {
          headers: [
            { title: "User Name", key: "username" },
            { title: "Type de contrat", key: "contractType" },
            { title: "Status", key: "status" },
            { title: "Role", key: "role" },
            { title: "Actions", key: "actions" },
          ],
          data: [],
          buttons: [
            {
              button: `<button style='background-color: #3498db; padding: 7px; color: white; border-radius: 2px; border: none;'>View</button>`,
              action: this.viewUser,
              disabled: false,
            },
            {
              button: `<button style='background-color: #e74c3c; padding: 7px; color: white; border-radius: 2px; border: none;'>Delete</button>`,
              action: this.deleteUser,
              disabled: () => !this.authStore.user.permissions.includes("delete_user"),
            },
            {
              button: `<button style='background-color: #f39c12; padding: 7px; color: white; border-radius: 2px; border: none;'>Update</button>`,
              action: this.updateUser,
              disabled: () => !this.authStore.user.permissions.includes("update_user"),
            },
          ],
        },
        pageSize: 10,
        currentPage: 1,
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
  
          const response = await fetch_methode(services.user.list, payload);
          const data = await response.json();
  
          if (response.ok) {
            this.tableInfo.data = data.data.map((user) => ({
              ...user,
              contractType: user.contracts.length > 0 ? user.contracts[0].type || "Pas de contrat" : "Pas de contrat",
            }));
          } else {
            console.error(data);
          }
        } catch (error) {
          console.error("Erreur lors de la récupération des utilisateurs:", error);
        }
      },
      viewUser(user) {
        console.log("View user:", user);
      },
      deleteUser(user) {
        const response = fetch_methode(services.user.delete , {user_id : user._id})
        if (response.ok) {
            alert("delete success")
            this.fetchUsers();
        } else {
            console.log(response)
        }
      },
      updateUser(user) {
        this.authStore.setUserId(user._id)
        console.log(user._id + "from list user")
        this.$router.push({ name: "update_user" });
      },
    },
    mounted() {
      this.fetchUsers();
    },
  };
  </script>
  