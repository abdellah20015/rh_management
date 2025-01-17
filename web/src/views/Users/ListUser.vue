<template>
  <div class="flex justify-center">
    <div class="w-11/12">
      <div class="flex items-center justify-between p-5">
        <p class="text-2xl font-semibold">Listes des utilisateurs</p>
        <button class="w-48 bg-black text-white rounded p-2">
          Ajouter un utilisateur
        </button>
      </div>
      <div>
        <TableComponent />
      </div>
    </div>
  </div>
</template>
<script>
import TableComponent from "@/components/TableComponent.vue";
import services from "@/shared/services";
import fetch_methode from "@/shared/utils";

export default {
  name: "ListUser",
  data() {
    return {
      tableInfo: {
        headers: [
          { title: "User Name", key: "username" },
          { title: "type de contrat", key: "type" },
          { title: "Status", key: "status" },

        ],
      },
    };
  },
};
</script>
