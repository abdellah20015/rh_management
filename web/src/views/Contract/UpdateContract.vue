<template>
  <div class="p-6">
    <FormComponent
      :fields="fields"
      :btn_text="'Mettre à jour le contrat'"
      :title="'Mise à jour du contrat'"
      :initialData="contractData"
      @formSubmitted="updateContract"
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
  name: "UpdateContract",
  components: { FormComponent },
  data() {
    return {
      selectedType: "",
      contractId: this.$route.params.contractId, // Récupération de l'ID du contrat à mettre à jour
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
      auth: useAuthStore(),
      contractData: {},
      contract_id: null,
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
    async fetchUserProfile() {
      try {
        const response = await utils.fetch_methode(services.user.profile, {
          user_id: this.auth.user_id,
        });
        const data = await response.json();

        if (response.ok) {
          const contract = data.data[0].contracts[0];
          this.contract_id = contract._id;
          console.log(this.contract_id);
          this.contractData = {
            type: contract.type,
            salary: parseFloat(contract.salary),
            start_date: contract.start_date,
            end_date: contract.end_date,
            leave_balance: parseInt(contract.leave_balance),
          };
          console.log("Contract data: ", this.contractData);
        } else {
          console.log(data);
        }
      } catch (error) {
        console.log(error);
      }
    },

    async updateContract(formdata) {
      try {
        const salary = parseFloat(formdata.salary);
        const leave_balance = parseInt(formdata.leave_balance);
        const payload = {
          contract_id: this.contract_id,
          ...formdata,
          salary :  salary,
          leave_balance: leave_balance
        };
        console.log(payload)
        const response = await utils.fetch_methode(
          services.contract.update,
          payload
        );
        const data = await response.json();
        console.log(data)
        if (response.ok) {
          alert("success");
          this.$router.push({ name: "details_user" });
        } else {
          console.log(data);
        }
      } catch (error) {
        console.log(error);
      }
    },
  },
  mounted() {
    this.fetchUserProfile();
  },
};
</script>
