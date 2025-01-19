<template>
  <div class="bg-gray-100">
    <div class="container mx-auto">
      <div class="flex justify-center items-center h-screen">
        <div class="w-full max-w-md">
          <FormComponent
            :fields="formFields"
            @formSubmitted="updateUser"
            :btn_text="btn_text"
            :title="title"
            :initialData="userData"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import FormComponent from "@/components/FormComponent.vue";
import { useAuthStore } from "@/stores/store";
import services from "@/shared/services";
import utils from "@/shared/utils";

export default {
  components: {
    FormComponent,
  },
  data() {
    return {
      formFields: [
        {
          name: "username",
          type: "text",
          label: "Nom d'utilisateur :",
          placeholder: "Entrez votre nom",
        },
        {
          name: "role",
          type: "select",
          label: "role",
          options: [
            { label: "Admin", value: "admin" },
            { label: "Manager", value: "manager" },
            { label: "Employee", value: "employee" },
          ],
        },
        {
          name: "manager",
          type: "select",
          label: "manager",
          options: [],
        },
        {
          name: "permissions",
          type: "select",
          label: "permissions",
          multiple: true,
          options: [
            { label: "create user", value: "create_user" },
            { label: "update user", value: "update_user" },
            { label: "delete user", value: "delete_user" },
            { label: "view users", value: "view_users" },
            { label: "import users", value: "import_user" },
            { label: "update demand", value: "update_demand" },
            { label: "update contract", value: "update_contract" },
            { label: "create contract", value: "create_contract" },
          ],
        },
      ],
      btn_text: "Update user",
      title: "Update user Form",
      userData: {},
    };
  },
  methods: {
    async updateUser(formdata) {
      try {
        const payload = {
          user_id: this.userId,
          update: {
            ...formdata,
          },
        };

        const response = await utils.fetch_methode(
          services.user.update,
          payload
        );
        const data = await response.json();
        if (response.ok) {
          alert("success");
          this.$router.push({ name: "list_user" });
        } else {
          console.log(data);
        }
      } catch (error) {
        console.log(error);
      }
    },
    async getUser() {
      try {
        const payload = { user_id: this.userId };
        const response = await utils.fetch_methode(
          services.user.profile,
          payload
        );
        const data = await response.json();

        if (response.ok) {
          const user = data.data[0];

          this.userData = {
            username: user.username,
            role: user.role,
            manager: user.manager_id,
            permissions: user.permissions || [],
          };

        } else {
          console.error("No user found or incorrect data format:", data);
        }
      } catch (error) {
        console.error("Error retrieving user data:", error);
      }
    },

    async getManager() {
      try {
        const response = await utils.fetch_methode(services.user.manager);
        const data = await response.json();
        if (response.ok) {
          const managers = data.data.map((manager) => ({
            label: manager.username,
            value: manager._id,
          }));
          const managerField = this.formFields.find(
            (field) => field.name === "manager"
          );
          if (managerField) {
            managerField.options = managers;
          }
          console.log("Managers loaded:", managers);
        } else {
          console.error("Failed to fetch managers:", data);
        }
      } catch (error) {
        console.error("Error fetching managers:", error);
      }
    },
  },
  computed: {
    userId() {
      const authStore = useAuthStore();
      return authStore.user_id;
    },
  },
  mounted() {
    this.getUser();
    this.getManager()
  },
};
</script>
