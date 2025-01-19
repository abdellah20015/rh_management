<template>
    <div class="flex justify-center">
      <div class="w-11/12">
        <div class="flex items-center justify-between p-5">
          <p class="text-2xl font-semibold">Listes des utilisateurs</p>
          <router-link  :to="{ name: 'create_user' }" class="w-48 bg-black text-white rounded p-2">
            Ajouter un utilisateur
          </router-link>
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
  import utils from "@/shared/utils";

  
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
            { title: "Type de contrat", key: "contractTypeTitle" },
            { title: "Status", key: "userStatusTitle" },
            { title: "Role", key: "role" },
            { title: "Actions", key: "actions" },
          ],
          data: [],
          buttons: [
            {
              button: `<button style='background-color: #3498db; padding: 7px; color: white; border-radius: 2px; border: none;'><img  width="20" height="20"  src="https://img.icons8.com/ios-filled/50/FFFFFF/visible.png" alt="View Icon" />
</button>`,
              action: this.viewUser,
              disabled: false,
            },
            {
              button: `<button style='background-color: #e74c3c; padding: 7px; color: white; border-radius: 2px; border: none;'><img  width="20" height="20"  src="https://img.icons8.com/ios-filled/50/FFFFFF/trash.png" alt="Delete Icon" /></button>`,
              action: this.deleteUser,
              disabled: () => !this.authStore.user.permissions.includes("delete_user"),
            },
            {
              button: `<button style='background-color: #f39c12; padding: 7px; color: white; border-radius: 2px; border: none;'><img  width="20" height="20"  src="https://img.icons8.com/ios-filled/50/FFFFFF/available-updates.png" alt="Update Icon" /></button>`,
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
  
          const response = await utils.fetch_methode(services.user.list, payload);
          const data = await response.json();
  
          if (response.ok) {
            console.log(data.data);
            
            this.tableInfo.data = data.data.map((user) => ({
              ...user,
              contractTypeTitle : user?.contracts[0]?.type == "cdd" ? "CDD" : user?.contracts[0]?.type == "cdi" ? "CDI" : "Pas de contrat",
              userStatusTitle : user.status == true ? "Active" : "Désactivé"
              
            }));
            console.log(this.tableInfo.data);
            
          } else {
            console.error(data);
          }
        } catch (error) {
          console.error("Erreur lors de la récupération des utilisateurs:", error);
        }
      },
      viewUser(user) {
        localStorage.setItem("id" ,user._id)
        this.authStore.setUserId(user._id)
        this.$router.push({ name: "details_user" });
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
        this.$router.push({ name: "update_user" });
      },
    },
    mounted() {
      this.fetchUsers();
    },
  };
  </script>
  