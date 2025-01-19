<template>
  <div class="p-6 max-w-4xl mx-auto">
    <!-- Toggle Switch -->
    <div class="flex justify-center mb-8">
      <button
        @click="activeTab = 'form'"
        :class="`px-4 py-2 ${activeTab === 'form' ? 'bg-black text-white' : 'bg-white text-black border border-black'}`"
      >
        Création manuelle
      </button>
      <button
        @click="activeTab = 'import'"
        :class="`px-4 py-2 ${activeTab === 'import' ? 'bg-black text-white' : 'bg-white text-black border border-black'}`"
      >
        Import Excel
      </button>
    </div>

    <!-- Form Creation -->
    <div v-if="activeTab === 'form'" class="mb-8">
      <FormComponent
        :fields="formFields"
        :btn_text="'Créer utilisateur'"
        :title="'Création d\'utilisateur'"
        @formSubmitted="handleFormSubmit"
      />
    </div>

    <!-- File Import -->
    <div v-if="activeTab === 'import'" class="space-y-6">
      <div class="bg-white p-8 rounded shadow-lg">
        <h2 class="text-2xl font-bold text-gray-800 text-center mb-4">Import d'utilisateurs</h2>

        <!-- Upload Zone -->
        <form @submit.prevent="handleFileSubmit" enctype="multipart/form-data">
          <div class="border-2 border-dashed border-black p-8 rounded-lg text-center">
            <input
              type="file"
              ref="fileInput"
              @change="handleFileChange"
              accept=".xlsx,.xls"
              class="hidden"
              name="file"
            />
            <button
              type="button"
              @click="$refs.fileInput.click()"
              class="bg-black text-white px-6 py-3 rounded hover:bg-gray-800"
            >
              Sélectionner un fichier Excel
            </button>
            <p v-if="selectedFile" class="mt-4 text-gray-700">
              Fichier sélectionné: {{ selectedFile.name }}
            </p>
          </div>
          <div class="mt-4 text-center">
            <button
              type="submit"
              :disabled="!selectedFile"
              class="bg-black text-white px-6 py-3 rounded hover:bg-gray-800 disabled:opacity-50"
            >
              Importer
            </button>
          </div>
        </form>

        <!-- File History -->
        <div class="mt-8">
          <h3 class="text-xl font-semibold mb-4">Historique des imports</h3>
          <div class="overflow-x-auto">
            <table class="w-full border-collapse">
              <thead>
                <tr class="bg-gray-100">
                  <th class="border p-2 text-left">Nom du fichier</th>
                  <th class="border p-2 text-left">Date</th>
                  <th class="border p-2 text-left">Total lignes</th>
                  <th class="border p-2 text-left">Succès</th>
                  <th class="border p-2 text-left">Erreurs</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="file in fileHistory" :key="file._id" class="hover:bg-gray-50">
                  <td class="border p-2">{{ file.file_name }}</td>
                  <td class="border p-2">{{ file.date_creation }}</td>
                  <td class="border p-2">{{ file.total_lines }}</td>
                  <td class="border p-2">{{ file.successful_lines }}</td>
                  <td class="border p-2">{{ file.error_lines }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Pagination -->
          <div class="flex justify-between items-center mt-4">
            <div>
              Page {{ currentPage }} sur {{ totalPages }}
            </div>
            <div class="space-x-2">
              <button
                @click="changePage(currentPage - 1)"
                :disabled="currentPage === 1"
                class="px-3 py-1 border border-black rounded disabled:opacity-50"
              >
                Précédent
              </button>
              <button
                @click="changePage(currentPage + 1)"
                :disabled="currentPage === totalPages"
                class="px-3 py-1 border border-black rounded disabled:opacity-50"
              >
                Suivant
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import FormComponent from '@/components/FormComponent.vue'
import utils from '@/shared/utils'
import services from '@/shared/services'

export default {
  name: 'CreateUsers',
  components: {
    FormComponent
  },
  setup() {
    const activeTab = ref('form')
    const selectedFile = ref(null)
    const fileHistory = ref([])
    const currentPage = ref(1)
    const totalPages = ref(1)
    const itemsPerPage = 10

    const formFields = [
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

    const handleFormSubmit = async (formData) => {
      try {
        const response = await utils.fetch_methode(services.user.create, formData)
        if (response.ok) {
          alert('Utilisateur créé avec succès')
        } else {
          const error = await response.json()
          alert(error.message || 'Erreur lors de la création de l\'utilisateur')
        }
      } catch (error) {
        console.error('Erreur:', error)
        alert('Erreur lors de la création de l\'utilisateur')
      }
    }

    const handleFileChange = (event) => {
      const file = event.target.files[0]
      if (file) {
        selectedFile.value = file
      }
    }

    const handleFileSubmit = async () => {
      if (!this.selectedFile) return;

      const formData = new FormData();
      formData.append('file', this.selectedFile);

      try {
        const response = await utils.fetch_methode(services.file.upload, formData);

        if (response.ok) {
          alert('Fichier importé avec succès');
          this.loadFileHistory();
        } else {
          let errorMessage = 'Erreur lors de l\'import du fichier';
          try {
            const errorData = await response.json();
            errorMessage = errorData.error || errorData.message || errorMessage;
          } catch (e) {
            errorMessage = response.statusText || errorMessage;
          }
          alert(errorMessage);
        }
      } catch (error) {
        console.error('Erreur:', error)
        alert('Erreur lors de l\'import du fichier: ' + error.message)
      }

      // Reset file input
      selectedFile.value = null
      const fileInput = document.querySelector('input[type="file"]')
      if (fileInput) fileInput.value = ''
    }

    const loadFileHistory = async () => {
      try {
        const response = await utils.fetch_methode(services.file.list, {
          query: {
            page: currentPage.value,
            limit: itemsPerPage
          }

        })
        console.log('response', response)

        if (response.ok) {
          const data = await response.json()
          fileHistory.value = data.files || []
          totalPages.value = Math.ceil(data.total / itemsPerPage) || 1
        } else {
          console.error('Erreur lors du chargement de l\'historique:', response.statusText)
        }
      } catch (error) {
        console.error('Erreur lors du chargement de l\'historique:', error)
      }
    }

    const changePage = (newPage) => {
      if (newPage >= 1 && newPage <= totalPages.value) {
        currentPage.value = newPage
        loadFileHistory()
      }
    }

    onMounted(() => {
      loadFileHistory()
    })

    return {
      activeTab,
      formFields,
      selectedFile,
      fileHistory,
      currentPage,
      totalPages,
      handleFormSubmit,
      handleFileChange,
      handleFileSubmit,
      changePage
    }
  }
}
</script>
