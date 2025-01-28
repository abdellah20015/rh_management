<template>
  <!-- <div class="p-2 w-10/12 mx-auto"> -->
  <!-- <div class="my-5">
      <RouterLink :to="{ name: 'list_user' }"
        class="w-20 flex items-center justify-center bg-black text-center text-white rounded py-1">
        <img width="30" height="30" src="https://img.icons8.com/ios-filled/50/FFFFFF/long-arrow-left.png"
          alt="long-arrow-left" />
      </RouterLink>
    </div> -->
  <div class="w-full">
    <div class="w-full flex justify-center my-7">
      <div class="relative w-11/12 bg-white rounded-[16px]  shadow-md p-2">
        <div class="flex justify-between items-center gap-2">
          <button @click="chooseDemand('form')"
            class="flex-1 relative group px-6 py-3 rounded-[12px]  text-sm font-medium transition-all duration-200 ease-in-out"
            :class="{
              'bg-[#006aff] text-white shadow-lg shadow-[#006aff]/20': activeTab === 'form',
              'bg-[#99c4ff]/10 text-[#6c7f93] hover:bg-[#99c4ff]/20': activeTab !== 'form'
            }">
            <div class="flex items-center justify-center gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24"
                stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              <span>Création manuelle</span>
            </div>
            <div class="absolute inset-0 rounded-[12px] transition-all duration-200 ease-in-out" :class="{
              'ring-2 ring-[#006aff] ring-offset-2': activeTab === 'form'
            }"></div>
          </button>

          <!-- Bouton Ordre de mission -->
          <button @click="chooseDemand('import')"
            class="flex-1 relative group px-6 py-3 rounded-[12px] text-sm font-medium transition-all duration-200 ease-in-out"
            :class="{
              'bg-[#006aff] text-white shadow-lg shadow-[#006aff]/20': activeTab === 'import',
              'bg-[#99c4ff]/10 text-[#6c7f93] hover:bg-[#99c4ff]/20': activeTab !== 'import'
            }">
            <div class="flex items-center justify-center gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24"
                stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
              <span>Importation Excel</span>
            </div>
            <div class="absolute inset-0 rounded-[12px] transition-all duration-200 ease-in-out" :class="{
              'ring-2 ring-[#006aff] ring-offset-2': activeTab === 'import'
            }"></div>
          </button>
        </div>
      </div>
    </div>

    <!-- Form Creation -->
    <div v-if="activeTab === 'form'" class="mb-8">
      <FormComponent :fields="formFields" :btn_text="'Créer utilisateur'" :title="'Création d\'utilisateur'"
        @formSubmitted="handleFormSubmit" />
    </div>

    <!-- File Import -->
    <div v-if="activeTab === 'import'" class="space-y-6">
      <div class="bg-white p-8 rounded-[5px] shadow-lg">
        <h2 class="text-2xl font-bold text-gray-800 text-center mb-6">Importer des utilisateurs</h2>

        <!-- Upload Zone -->
        <div
          class="border-2 border-dashed border-[#3B82F6] p-6 rounded-[5px] text-center cursor-pointer hover:bg-[#99CAFF]/20 transition"
          @click="$refs.fileInput.click()">
          <input type="file" ref="fileInput" @change="handleFileChange" accept=".xlsx,.xls" class="hidden" />
          <div class="flex flex-col items-center">
            <svg class="w-12 h-12 text-[#3B82F6]" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round"
                d="M12 4v12m0 0l-3-3m3 3l3-3M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
            </svg>
            <p class="mt-3 text-gray-700">Glissez et déposez votre fichier ici ou cliquez pour parcourir</p>
            <button type="button"
              class="mt-2 bg-[#3B82F6] text-white px-6 py-2 rounded-[5px] hover:bg-[#2563EB] transition">
              Sélectionner un fichier Excel
            </button>
          </div>
        </div>

        <!-- Selected File Preview -->
        <div v-if="selectedFile" class="mt-4 p-3 bg-[#99CAFF]/30 rounded-[5px] flex items-center justify-between">
          <span class="text-gray-800">{{ selectedFile.name }}</span>
          <button @click="selectedFile = null" class="text-red-500 hover:text-red-700">Supprimer</button>
        </div>

        <!-- Import Button -->
        <div class="mt-4 text-center">
          <button @click="handleFileSubmit" :disabled="!selectedFile"
            class="bg-[#3B82F6] text-white px-6 py-3 rounded-[5px] hover:bg-[#2563EB] transition disabled:opacity-50 disabled:cursor-not-allowed">
            Importer
          </button>
        </div>

        <!-- File History -->
        <div class="mt-8">
          <h3 class="text-xl font-semibold text-gray-800 mb-4">Historique des importations</h3>
          <div class="w-full flex justify-center">
            <div class="w-full  p-4 rounded-[5px]">
              <TableComponent :tableInfo="tableInfo" :currentPage="currentPage" :totalPages="totalPages"
                :pageSize="itemsPerPage" @page-changed="changePage" />
            </div>
          </div>
        </div>

      </div>
    </div>

  </div>
</template>

<script>

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
      count: 0,
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
          name: 'phone',
          label: 'phone',
          type: 'text',
          placeholder: 'phone numbre'
        },
        {
          name: 'firstName',
          label: 'first Name',
          type: 'text',
          placeholder: 'first Name'
        },
        {
          name: 'lastName',
          label: 'lastName',
          type: 'text',
          placeholder: 'lastName'
        },
        {
          name: 'email',
          label: 'email',
          type: 'text',
          placeholder: 'email'
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
          this.$router.push({ name: "list_user" })
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
