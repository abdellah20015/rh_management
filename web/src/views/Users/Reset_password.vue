<template>
  <div class="bg-gray-100">
    <div class="container mx-auto">
      <div class="flex justify-center items-center h-[80.5vh]">
        <div class="w-full max-w-md">
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
          label: "old password",
          placeholder: "Entrez votre ancien mot de passe",
        },
        {
          name: "new_password",
          type: "password",
          label: "new password",
          placeholder: "Nouveau mot de passe :",
        },
      ],
      btn_text: "Reset password",
      title: "Reset password Form",
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
      try {
        const response = await utils.fetch_methode(services.user.resetPassword, {
          old_password,
          new_password,
        });
        
        if (response.ok) {
          if (["admin", "manager"].includes(this.user.role)) {
            this.$router.push({ name: "list_user" });
          } else {
            this.$router.push({ name: "list_demand" });
          }
        } else {
          const data = await response.json();
          console.log(data);
        }
      } catch (error) {
        alert(error);
      }
    },
  },
};
</script>
