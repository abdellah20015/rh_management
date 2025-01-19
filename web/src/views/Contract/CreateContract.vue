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
import { ref, computed } from 'vue'
import FormComponent from '@/components/FormComponent.vue'
import utils from "@/shared/utils"
import services from "@/shared/services"
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'CreateContract',
  components: { FormComponent },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const selectedType = ref('')
    const userId = computed(() => route.params.userId)

    const allFields = ref([
      {
        type: 'select',
        name: 'type',
        label: 'Type de contrat',
        options: [
          { value: 'cdi', label: 'CDI' },
          { value: 'cdd', label: 'CDD' }
        ]
      },
      {
        type: 'date',
        name: 'start_date',
        label: 'Date de début'
      },
      {
        type: 'number',
        name: 'salary',
        label: 'Salaire',
        placeholder: 'Entrez le salaire'
      },
      {
        type: 'number',
        name: 'leave_balance',
        label: 'Solde congés',
        placeholder: 'Entrez le solde de congés'
      }
    ])

    const endDateField = {
      type: 'date',
      name: 'end_date',
      label: 'Date de fin (Pour CDD)'
    }

    const fields = computed(() => {
      if (selectedType.value === 'cdd') {
        return [
          ...allFields.value.slice(0, 2),
          endDateField,
          ...allFields.value.slice(2)
        ]
      }
      return allFields.value
    })

    const handleFieldChange = ({ name, value }) => {
      if (name === 'type') {
        selectedType.value = value
      }
    }

    const validateFormData = (data) => {
      const requiredFields = ['type', 'start_date', 'salary', 'leave_balance']
      if (data.type === 'cdd') {
        requiredFields.push('end_date')
      }

      return requiredFields.every(field => {
        const value = data[field]
        return value !== undefined && value !== null && value !== ''
      })
    }

    const handleContractSubmission = async (formData) => {
      try {
        if (!validateFormData(formData)) {
          alert('Veuillez remplir tous les champs obligatoires')
          return
        }

        const contractData = {
          ...formData,
          user_id:"",
          salary: parseFloat(formData.salary),
          leave_balance: parseInt(formData.leave_balance),
          status: true
        }

        const response = await utils.fetch_methode(services.contract.create, contractData)

        if (response.ok) {
          const result = await response.json()
          if (result.status === 'success') {
            alert('Contrat créé avec succès')
            router.push(`/private/user/profile/${userId.value}`)
          } else {
            alert('Erreur lors de la création du contrat: ' + result.message)
          }
        } else {
          alert('Erreur lors de la création du contrat')
        }
      } catch (error) {
        console.error('Erreur lors de la création du contrat:', error)
        alert('Une erreur est survenue lors de la création du contrat')
      }
    }

    return {
      fields,
      handleFieldChange,
      handleContractSubmission
    }
  }
}
</script>
