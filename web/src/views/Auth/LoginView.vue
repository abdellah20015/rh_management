<template>
  <div class="bg-gray-100">
    <div class="container mx-auto">
      <div class="flex justify-center items-center h-screen">
        <div class="w-full max-w-md">
          <FormComponent
            :fields="formFields"
            @formSubmitted="login"
            :btn_text="btn_text"
            :title="title"
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
          name: "password",
          type: "password",
          label: "Mot de passe",
          placeholder: "Entrez votre mot de passe",
        },
      ],
      btn_text: "Login",
      title: "Login Form",
    };
  },
  methods: {
    async login(formdata) {
      const authStore = useAuthStore();
      const { username, password } = formdata;

      try {
        const response = await utils.fetch_methode(services.login, {
          username,
          password,
        });
        const data = await response.json();
        if (response.ok) {
           authStore.auth(data);
          if (["admin", "manager"].includes(data.user.role)) {
            this.$router.push({ name: "list_user" })
          } else {
            this.$router.push({ name: "list_demand" })
          }
        } else {
          console.log(data);
        }
      } catch (error) {
        console.error("Erreur lors de la connexion:", error);
        alert(error);
      }
    },
  },
};
</script>
