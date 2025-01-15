<template>
  <div class="bg-gray-100">
    <div class="container mx-auto">
      <div class="flex justify-center items-center h-screen">
        <div class="w-full max-w-md">
          <FormComponent
            :fields="formFields"
            @formSubmitted="login"
            :btn_text
            :title
          />
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import FormComponent from "@/components/FormComponent.vue";

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
      btn_text: "login",
      title: "Login Form",
    };
  },
};
</script>
