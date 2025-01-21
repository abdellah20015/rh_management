<template>
  <div class="p-2 w-10/12 mx-auto">
    <div class="my-5">
      <RouterLink :to="{ name: 'list_user' }"
        class="w-20 flex items-center justify-center bg-black text-center text-white rounded py-1">
        <img width="30" height="30" src="https://img.icons8.com/ios-filled/50/FFFFFF/long-arrow-left.png"
          alt="long-arrow-left" />
      </RouterLink>
    </div>
    <div class="w-full flex justify-center mb-10">
      <div class="flex justify-evenly bg-gray-200 border-2 border-gray-300 w-full p-3 rounded-md h-12 items-center">
        <a href="#" @click="chooseDemand('form')"
          class="w-80 text-center py-1 items-center rounded-md hover:bg-black hover:text-white transition-all delay-75 ease-in-out"
          :class="{ 'bg-black text-white border border-gray-500 ': activeTab === 'form' }">
          <p>Création manuelle</p>
        </a>
        <a href="#" @click="chooseDemand('import')"
          class="w-80 text-center py-1 items-center rounded-md hover:bg-black hover:text-white transition-all delay-75 ease-in-out"
          :class="{ 'bg-black text-white border border-gray-500 ': activeTab === 'import' }">
          <p>Import Excel</p>
        </a>
      </div>
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
          <div class="w-full flex justify-center">
            <div class="w-[1100px] ">
              <TableFilterComponent :filterStructure="filterStructure" @searchHandler="searchHandler"
                @filterHandler="handleFilter" @resertFilterHandler="handleResetFilter" />
            </div>
          </div>
          <div class="w-full">
            <TableComponent :tableInfo="tableInfo" :currentPage="currentPage" :totalPages="totalPages"
              :pageSize="itemsPerPage" @page-changed="changePage" />
          </div>
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
      itemsPerPage: 5,
      count : 0,
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
          utils.errorAlert('Impossible de charger la liste des managers');
        }
      } catch (error) {
        console.error('Erreur:', error);
        utils.errorAlert('Erreur lors du chargement des managers');
      }
    },


    async handleFormSubmit(formData) {
      try {
        const response = await utils.fetch_methode(services.user.create, formData);
        if (response.ok) {
          utils.successAlert('Utilisateur créé avec succès');
        } else {
          const error = await response.json();
          utils.errorAlert(error.message || 'Erreur lors de la création de l\'utilisateur');
        }
      } catch (error) {
        console.error('Erreur:', error);
        utils.errorAlert('Erreur lors de la création de l\'utilisateur');
      }
    },
    handleFileChange(event) {
      this.selectedFile = event.target.files[0];
    },
    async handleFileSubmit() {
      if (!this.selectedFile) {
        utils.errorAlert('Veuillez sélectionner un fichier avant de continuer');
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
          utils.successAlert('Fichier importé avec succès');
          await this.loadFileHistory();
          this.selectedFile = null;
          const fileInput = document.querySelector('input[type="file"]');
          if (fileInput) fileInput.value = '';
        } else {
          const error = await response.text();
          utils.errorAlert(error || 'Erreur lors de l\'import du fichier');
        }
      } catch (error) {
        console.error('Erreur:', error);
        utils.errorAlert('Erreur lors de l\'import du fichier');
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
          console.log(data);
          

          //pagination
          this.count = data.files.count;
          this.count += (this.currentPage - 1) * this.itemsPerPage;
          console.log(data.files.count);

          if (this.count > this.pageSize) {
            this.totalPages = Math.ceil(this.count / this.itemsPerPage);
          }

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
        utils.errorAlert('Impossible de charger l\'historique des fichiers');
      }
    },


    searchHandler(searchValue) {
      if (searchValue == "") {
        this.loadFileHistory()
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
    },

    chooseDemand(type) {
      if (type == "form") {
        this.activeTab = "form";
      } else if (type == "import") {
        this.activeTab = "import";
      }
      console.log(this.demandType);

    },

  },

  mounted() {
    this.loadFileHistory();
    this.loadManagers();
  }
};
</script>
