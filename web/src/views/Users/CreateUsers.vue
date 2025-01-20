<template>
  <div class="p-6 max-w-4xl mx-auto">

    <div class="flex justify-center mb-8">
      <button @click="activeTab = 'form'"
        :class="`px-4 py-2 ${activeTab === 'form' ? 'bg-black text-white' : 'bg-white text-black border border-black'}`">
        Création manuelle
      </button>
      <button @click="activeTab = 'import'"
        :class="`px-4 py-2 ${activeTab === 'import' ? 'bg-black text-white' : 'bg-white text-black border border-black'}`">
        Import Excel
      </button>
    </div>

    <!-- Form Creation -->
    <div v-if="activeTab === 'form'" class="mb-8">
      <FormComponent :fields="formFields" :btn_text="'Créer utilisateur'" :title="'Création d\'utilisateur'"
        @formSubmitted="handleFormSubmit" />
    </div>

    <!-- File Import -->
    <div v-if="activeTab === 'import'" class="space-y-6">
      <div class="bg-white p-8 rounded shadow-lg">
        <h2 class="text-2xl font-bold text-gray-800 text-center mb-4">Import d'utilisateurs</h2>

        <!-- Upload Zone -->
        <div class="border-2 border-dashed border-black p-8 rounded-lg text-center">
          <input type="file" ref="fileInput" @change="handleFileChange" accept=".xlsx,.xls" class="hidden" />
          <button type="button" @click="$refs.fileInput.click()"
            class="bg-black text-white px-6 py-3 rounded hover:bg-gray-800">
            Sélectionner un fichier Excel
          </button>
          <p v-if="selectedFile" class="mt-4 text-gray-700">
            Fichier sélectionné: {{ selectedFile.name }}
          </p>
        </div>
        <div class="mt-4 text-center">
          <button @click="handleFileSubmit" :disabled="!selectedFile"
            class="bg-black text-white px-6 py-3 rounded hover:bg-gray-800 disabled:opacity-50">
            Importer
          </button>
        </div>

        <!-- File History -->
        <div class="mt-8">
          <h3 class="text-xl font-semibold mb-4">Historique des imports</h3>
          <TableFilterComponent :filterStructure="filterStructure" @searchHandler="searchHandler"
            @filterHandler="handleFilter" @resertFilterHandler="handleResetFilter" />
          <TableComponent :tableInfo="tableInfo" :currentPage="currentPage" :totalPages="totalPages"
            :pageSize="itemsPerPage" @page-changed="changePage" />
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import Swal from 'sweetalert2';
import FormComponent from '@/components/FormComponent.vue';
import TableComponent from '@/components/TableComponent.vue';
import TableFilterComponent from '@/components/TableFilterComponent.vue';
import utils from '@/shared/utils';
import services from '@/shared/services';
import index from '@/shared/index';

export default {
  name: 'CreateUsers',
  components: {
    FormComponent,
    TableComponent,
    TableFilterComponent
  },
  data() {
    return {
      activeTab: 'form',
      selectedFile: null,
      fileHistory: [],
      currentPage: 1,
      totalPages: 1,
      itemsPerPage: 10,
      tableInfo: {
        headers: [
          { title: 'Nom du fichier', key: 'file_name' },
          { title: 'Date', key: 'created_date' },
          { title: 'Total lignes', key: 'total_lines' },
          { title: 'Succès', key: 'successful_lines' },
          { title: 'Erreurs', key: 'error_lines' }
        ],
        data: [],
      },
      filterStructure: [],
      formFields: [
        {
          name: 'username',
          label: 'Nom d\'utilisateur',
          type: 'text',
          placeholder: 'Entrez le nom d\'utilisateur'
        },
        {
          name: 'password',
          label: 'Mot de passe',
          type: 'password',
          placeholder: 'Entrez le mot de passe'
        },
        {
          name: 'manager_id',
          label: 'Manager',
          type: 'select',
          options: [],
          placeholder: 'Sélectionnez un manager'
        },
        {
          name: 'role',
          label: 'Rôle',
          type: 'select',
          options: [
            { value: 'employee', label: 'Employé' },
            { value: 'manager', label: 'Manager' },
            { value: 'admin', label: 'Administrateur' }
          ]
        }
      ]
    };
  },
  methods: {
    async loadManagers() {
      try {
        const response = await utils.fetch_methode(services.user.manager);
        const jsonData = await response.json();

        if (response.ok && jsonData.status === 'success') {
          const managers = jsonData.data;

          const managerFieldIndex = this.formFields.findIndex(field => field.name === 'manager_id');

          if (managerFieldIndex !== -1 && managers && Array.isArray(managers)) {
            const updatedFormFields = [...this.formFields];
            updatedFormFields[managerFieldIndex] = {
              ...updatedFormFields[managerFieldIndex],
              options: managers.map(manager => ({
                value: manager._id,
                label: manager.username
              }))
            };
            this.formFields = updatedFormFields;
          }
        } else {
          console.error('Erreur lors du chargement des managers:', jsonData);
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: 'Impossible de charger la liste des managers'
          });
        }
      } catch (error) {
        console.error('Erreur:', error);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Erreur lors du chargement des managers'
        });
      }
    },


    async handleFormSubmit(formData) {
      try {
        const response = await utils.fetch_methode(services.user.create, formData);
        if (response.ok) {
          Swal.fire({
            icon: 'success',
            title: 'Succès',
            text: 'Utilisateur créé avec succès',
            timer: 3000,
            timerProgressBar: true
          });
        } else {
          const error = await response.json();
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: error.message || 'Erreur lors de la création de l\'utilisateur'
          });
        }
      } catch (error) {
        console.error('Erreur:', error);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Erreur lors de la création de l\'utilisateur'
        });
      }
    },
    handleFileChange(event) {
      this.selectedFile = event.target.files[0];
    },
    async handleFileSubmit() {
      if (!this.selectedFile) {
        Swal.fire({
          icon: 'warning',
          title: 'Attention',
          text: 'Veuillez sélectionner un fichier avant de continuer'
        });
        return;
      }

      const formData = new FormData();
      formData.append('file', this.selectedFile);

      try {
        const response = await fetch(index.server_adress + services.file.upload, {
          method: 'POST',
          body: formData,
          credentials: 'include'
        });

        if (response.ok) {
          Swal.fire({
            icon: 'success',
            title: 'Succès',
            text: 'Fichier importé avec succès',
            timer: 3000,
            timerProgressBar: true
          });
          await this.loadFileHistory();
          this.selectedFile = null;
          const fileInput = document.querySelector('input[type="file"]');
          if (fileInput) fileInput.value = '';
        } else {
          const error = await response.text();
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: error || 'Erreur lors de l\'import du fichier'
          });
        }
      } catch (error) {
        console.error('Erreur:', error);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Erreur lors de l\'import du fichier'
        });
      }
    },
    async loadFileHistory() {
      try {
        const params = {
          options: {
            page: this.currentPage,
            limit: this.itemsPerPage
          }
        };

        const response = await utils.fetch_methode(services.file.list, params);

        if (response.ok) {
          const data = await response.json();

          if (data && data.files && data.files.data && Array.isArray(data.files.data)) {
            this.tableInfo.data = data.files.data.map(file => ({
              file_name: file.name,
              created_date: file.created_date,
              total_lines: Number(file.total_lines),
              successful_lines: Number(file.successful_lines),
              error_lines: Number(file.error_lines)
            }));

            this.totalPages = data.totalPages || 1;
            this.currentPage = data.page || 1;
          }
        }
      } catch (error) {
        console.error('Erreur lors du chargement de l\'historique:', error);
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de charger l\'historique des fichiers'
        });
      }
    },


    searchHandler(searchValue) {
     if (searchValue == "") {
        this.fetchDemands()
      }
     const currentData = this.tableInfo.data;

      if (!searchValue) {
        return currentData;
      }

      const searchedData = currentData.filter((demande) => {
        return Object.values(demande).some(value =>
          String(value).toLowerCase().includes(searchValue.toLowerCase())
        );
      });

        this.tableInfo.data = searchedData;
    },

    changePage(newPage) {
      this.currentPage = newPage;
      this.loadFileHistory();
    }

  },

  mounted() {
    this.loadFileHistory();
    this.loadManagers();
  }
};
</script>
