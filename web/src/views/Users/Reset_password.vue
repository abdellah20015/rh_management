<template>
  <div >
    <div class="container mx-auto">
      <div class="flex justify-center items-center h-[80.5vh] ">
        <div class="w-full max-w-md ">
          <FormComponent
            :fields="formFields"
            @formSubmitted="reset_password"
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
import services from "@/shared/services";
import utils from "@/shared/utils";
import { useAuthStore } from "@/stores/store";
export default {
  components: {
    FormComponent,
  },
  data() {
    return {
      formFields: [
        {
          name: "old_password",
          type: "password",
          label: "Ancien mot de passe",
          placeholder: "Entrez votre ancien mot de passe",
        },
        {
          name: "new_password",
          type: "password",
          label: "Nouveau mot de passe",
          placeholder: "Nouveau mot de passe :",
        },
      ],
      btn_text: "Réinitialiser le mot de passe",
      title: "Réinitialisation du mot de passe",
      auth: useAuthStore(),
    };
  },
  computed: {
    user() {
      return this.auth.user;
    },
  },
  methods: {
    async reset_password(formdata) {
      const { old_password, new_password } = formdata;
      console.log(typeof(old_password) , old_password , new_password)
      try {
        const response = await utils.fetch_methode(services.user.resetPassword, {
          old_password,
          new_password,
        });
        console.log(response)
        if (response.ok) {
          utils.successAlert("Success: Password has been reset successfully.");
          if (["admin", "manager"].includes(this.user.role)) {
            this.$router.push({ name: "list_user" });
          } else {
            this.$router.push({ name: "userDemands" });
          }
        } else {
          utils.errorAlert("Error: Unable to reset the password. Please try again.");
          const data = await response.json();
          console.log(data);
        }
      } catch (error) {
        utils.errorAlert("Error: Unable to reset the password. Please try again.");
        console.log(error)
      }
    },
  },
};
</script>
