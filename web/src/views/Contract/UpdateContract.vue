<template>
  <div class="p-6">
    <div v-if="loading" class="text-center py-8">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 mx-auto"></div>
      <p class="mt-4 text-gray-600">Chargement des informations du contrat...</p>
    </div>

    <div v-else-if="error" class="text-center py-8">
      <div class="text-red-500 mb-4">{{ error }}</div>
      <button
        @click="fetchUserProfile"
        class="bg-gray-200 text-gray-700 py-2 px-4 rounded-lg hover:bg-gray-300 transition duration-200"
      >
        Réessayer
      </button>
    </div>

    <div v-else class="bg-white rounded-xl shadow-lg p-6">
      <h2 class="text-2xl font-bold text-gray-800 mb-6">Modification du contrat</h2>

      <form @submit.prevent="handleContractUpdate" class="space-y-6">
        <!-- Type de contrat -->
        <div class="space-y-2">
          <label class="block font-medium text-gray-700">Type de contrat</label>
          <select
            v-model="formData.type"
            class="w-full p-2 border rounded-lg"
            @change="handleTypeChange"
          >
            <option value="cdi">CDI</option>
            <option value="cdd">CDD</option>
          </select>
        </div>

        <!-- Date de début -->
        <div class="space-y-2">
          <label class="block font-medium text-gray-700">Date de début</label>
          <input
            type="date"
            v-model="formData.start_date"
            class="w-full p-2 border rounded-lg"
          >
        </div>

        <!-- Date de fin (pour CDD) -->
        <div v-if="formData.type === 'cdd'" class="space-y-2">
          <label class="block font-medium text-gray-700">Date de fin</label>
          <input
            type="date"
            v-model="formData.end_date"
            class="w-full p-2 border rounded-lg"
          >
        </div>

        <!-- Salaire -->
        <div class="space-y-2">
          <label class="block font-medium text-gray-700">Salaire</label>
          <input
            type="number"
            v-model="formData.salary"
            class="w-full p-2 border rounded-lg"
            placeholder="Entrez le salaire"
          >
        </div>

        <!-- Solde congés -->
        <div class="space-y-2">
          <label class="block font-medium text-gray-700">Solde congés</label>
          <input
            type="number"
            v-model="formData.leave_balance"
            class="w-full p-2 border rounded-lg"
            placeholder="Entrez le solde de congés"
          >
        </div>

        <!-- Statut -->
        <div class="space-y-2">
          <label class="block font-medium text-gray-700">Statut du contrat</label>
          <select
            v-model="formData.status"
            class="w-full p-2 border rounded-lg"
          >
            <option :value="true">Actif</option>
            <option :value="false">Inactif</option>
          </select>
        </div>


        <button
          type="submit"
          class="w-full bg-black text-white py-2 px-4 rounded-lg hover:bg-gray-800 transition duration-200"
        >
          Mettre à jour le contrat
        </button>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import utils from "@/shared/utils"
import services from "@/shared/services"
import { useRouter } from 'vue-router'
import { useAuthStore } from "@/stores/store"

export default {
  name: 'UpdateContract',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const loading = ref(true)
    const error = ref(null)
    const contractData = ref(null)
    const userData = ref(null)

    const formData = ref({
      type: '',
      start_date: '',
      end_date: '',
      salary: 0,
      leave_balance: 0,
      status: true
    })

    const fetchUserProfile = async () => {
      console.log('Starting fetchUserProfile')
      loading.value = true
      error.value = null

      try {
        const response = await utils.fetch_methode(services.user.profile, {
          user_id: authStore.user?.id
        })

        if (response.ok) {
          const result = await response.json()
          console.log('Profile API response:', result)

          if (result.data && result.data[0] && result.data[0].contracts && result.data[0].contracts[0]) {
            userData.value = result.data[0]
            contractData.value = result.data[0].contracts[0]


            formData.value = {
              type: contractData.value.type,
              start_date: utils.convertDate(contractData.value.start_date),
              end_date: contractData.value.end_date ? utils.convertDate(contractData.value.end_date) : '',
              salary: contractData.value.salary,
              leave_balance: contractData.value.leave_balance,
              status: contractData.value.status
            }

            console.log('Form data initialized:', formData.value)
          } else {
            error.value = 'Aucun contrat trouvé pour cet utilisateur'
          }
        } else {
          error.value = 'Erreur lors de la récupération des données du profil'
        }
      } catch (err) {
        console.error('Error in fetchUserProfile:', err)
        error.value = 'Une erreur est survenue lors de la récupération des données'
      } finally {
        loading.value = false
      }
    }

    const handleTypeChange = () => {
      if (formData.value.type !== 'cdd') {
        formData.value.end_date = ''
      }
    }

    const handleContractUpdate = async () => {
      try {
        const updateData = {
          contract_id: contractData.value._id,
          ...formData.value,
          salary: parseFloat(formData.value.salary),
          leave_balance: parseInt(formData.value.leave_balance)
        }

        console.log('Sending update request with data:', updateData)

        const response = await utils.fetch_methode(services.contract.update, updateData)

        if (response.ok) {
          const result = await response.json()
          console.log('Update response:', result)

          if (result.status === 'success') {
            alert('Contrat mis à jour avec succès')
            router.push(`/private/user/profile/${userData.value._id}`)
          } else {
            alert('Erreur lors de la mise à jour du contrat: ' + result.message)
          }
        } else {
          alert('Erreur lors de la mise à jour du contrat')
        }
      } catch (error) {
        console.error('Error in handleContractUpdate:', error)
        alert('Une erreur est survenue lors de la mise à jour du contrat')
      }
    }

    onMounted(() => {
      if (authStore.user?.id) {
        fetchUserProfile()
      } else {
        error.value = 'Utilisateur non connecté'
        loading.value = false
      }
    })

    return {
      loading,
      error,
      formData,
      handleTypeChange,
      handleContractUpdate,
      fetchUserProfile
    }
  }
}
</script>
