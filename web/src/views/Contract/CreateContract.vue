<template>
  <div class="p-6">
    <FormComponent
      :fields="fields"
      :btn_text="'Créer le contrat'"
      :title="'Création d\'un contrat'"
      @formSubmitted="handleContractSubmission"
      @fieldChange="handleFieldChange"
    />
  </div>
</template>

<script>
import FormComponent from "@/components/FormComponent.vue";
import utils from "@/shared/utils";
import services from "@/shared/services";
import { useAuthStore } from "@/stores/store";


export default {
  name: "CreateContract",
  components: { FormComponent },
  data() {
    return {
      selectedType: "",
      allFields: [
        {
          type: "select",
          name: "type",
          label: "Type de contrat",
          options: [
            { value: "cdi", label: "CDI" },
            { value: "cdd", label: "CDD" },
          ],
        },
        {
          type: "date",
          name: "start_date",
          label: "Date de début",
        },
        {
          type: "text",
          name: "salary",
          label: "Salaire",
          placeholder: "Entrez le salaire",
        },
        {
          type: "number",
          name: "leave_balance",
          label: "Solde congés",
          placeholder: "Entrez le solde de congés",
        },
      ],
      endDateField: {
        type: "date",
        name: "end_date",
        label: "Date de fin (Pour CDD)",
      },
      auth : useAuthStore()
    };
  },
  computed: {
    fields() {
      if (this.selectedType === "cdd") {
        return [
          ...this.allFields.slice(0, 2),
          this.endDateField,
          ...this.allFields.slice(2),
        ];
      }
      return this.allFields;
    },
  },
  methods: {
    handleFieldChange({ name, value }) {
      if (name === "type") {
        this.selectedType = value;
      }
    },
    validateFormData(data) {
      const requiredFields = ["type", "start_date", "salary", "leave_balance"];
      if (data.type === "cdd") {
        requiredFields.push("end_date");
      }
      return requiredFields.every((field) => {
        const value = data[field];
        return value !== undefined && value !== null && value !== "";
      });
    },
    async handleContractSubmission(formData) {
      try {
        if (!this.validateFormData(formData)) {
          alert("Veuillez remplir tous les champs obligatoires");
          return;
        }

        const salary = parseFloat(formData.salary)

        const contractData = {
          ...formData,
          user_id: this.auth.user_id,
          salary,
          leave_balance: parseInt(formData.leave_balance),
          status: true,
        };

        const response = await utils.fetch_methode(
          services.contract.create,
          contractData
        );

        const result = await response.json();
        if (response.ok) {
          utils.successAlert("contract created successful");
          this.$router.push({name : "details_user"})
        } else {
         utils.errorAlert("Error: Contract creation failed.");
          console.log("Erreur lors de la création du contrat" + result.message);
        }
      } catch (error) {
       utils.errorAlert("Error: Contract creation failed. Please try again.");
        console.error("Erreur lors de la création du contrat:", error);
      
      }
    },
  },
};
</script>
