<template>
    <div class="flex justify-center">
      <div class="w-11/12">
        <div class="w-full flex justify-center my-7">
          <div class="relative w-11/12 bg-white rounded-[16px] shadow-md p-2">
            <!-- Container principal avec fond blanc et ombre -->
            <div class="flex justify-between items-center gap-2">
              <!-- Bouton Demande congé -->
              <button 
                @click="chooseDemand('demande_conge')"
                class="flex-1 relative group px-6 py-3 rounded-[12px] text-sm font-medium transition-all duration-200 ease-in-out"
                :class="{
                  'bg-[#006aff] text-white shadow-lg shadow-[#006aff]/20': demandType === 'demande_conge',
                  'bg-[#99c4ff]/10 text-[#6c7f93] hover:bg-[#99c4ff]/20': demandType !== 'demande_conge'
                }"
              >
                <div class="flex items-center justify-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span>Demande congé</span>
                </div>
                <div 
                  class="absolute inset-0 rounded-[12px] transition-all duration-200 ease-in-out"
                  :class="{
                    'ring-2 ring-[#006aff] ring-offset-2': demandType === 'demande_conge'
                  }"
                ></div>
              </button>
  
              <!-- Bouton Ordre de mission -->
              <button 
                @click="chooseDemand('ordre_de_mission')"
                class="flex-1 relative group px-6 py-3 rounded-[12px] text-sm font-medium transition-all duration-200 ease-in-out"
                :class="{
                  'bg-[#006aff] text-white shadow-lg shadow-[#006aff]/20': demandType === 'ordre_de_mission',
                  'bg-[#99c4ff]/10 text-[#6c7f93] hover:bg-[#99c4ff]/20': demandType !== 'ordre_de_mission'
                }"
              >
                <div class="flex items-center justify-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                  <span>Ordre de mission</span>
                </div>
                <div 
                  class="absolute inset-0 rounded-[12px] transition-all duration-200 ease-in-out"
                  :class="{
                    'ring-2 ring-[#006aff] ring-offset-2': demandType === 'ordre_de_mission'
                  }"
                ></div>
              </button>
  
              <!-- Bouton Attestation de travail -->
              <button 
                @click="chooseDemand('attestation_de_travail')"
                class="flex-1 relative group px-6 py-3 rounded-[12px] text-sm font-medium transition-all duration-200 ease-in-out"
                :class="{
                  'bg-[#006aff] text-white shadow-lg shadow-[#006aff]/20': demandType === 'attestation_de_travail',
                  'bg-[#99c4ff]/10 text-[#6c7f93] hover:bg-[#99c4ff]/20': demandType !== 'attestation_de_travail'
                }"
              >
                <div class="flex items-center justify-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  <span>Attestation de travail</span>
                </div>
                <div 
                  class="absolute inset-0 rounded-[12px] transition-all duration-200 ease-in-out"
                  :class="{
                    'ring-2 ring-[#006aff] ring-offset-2': demandType === 'attestation_de_travail'
                  }"
                ></div>
              </button>
            </div>
          </div>
        </div>
  
        <!-- Contenu des formulaires -->
        <div v-if="demandType == 'demande_conge'">
          <FormComponent 
            :fields="demandeDeCongefields" 
            :title="title" 
            :btn_text="btn_text"
            @formSubmitted="createDemande"
          />
        </div>
        <div v-else-if="demandType == 'ordre_de_mission'">
          <FormComponent 
            :fields="orderDeMissionfields" 
            :title="title" 
            :btn_text="btn_text"
            @formSubmitted="createDemande"
          />
        </div>
        <div v-else-if="demandType == 'attestation_de_travail'" class="flex justify-center items-center h-full mt-6">
          <button 
            @click="createDemande()" 
            class="px-6 py-2.5 bg-[#006aff] text-white rounded-[5px] font-medium shadow-lg shadow-[#006aff]/20 hover:bg-[#006aff]/90 transition-all duration-200"
          >
            Ajouter une demande
          </button>
        </div>
      </div>
    </div>
  </template>

<script>
import FormComponent from '@/components/FormComponent.vue';
import router from '@/router';
import services from '@/shared/services';
import utils from '@/shared/utils';
import { useAuthStore } from '@/stores/store';
export default {
    name: 'CreateDemand',
    data() {
        return {

            demandType: "demande_conge",

            //demande de conge table structure
            orderDeMissionfields: [
                {
                    type: 'select',
                    name: 'type',
                    label: 'Type de demande',
                    options: [
                        { value: 'ordre_de_mission', label: 'Ordre de mission' },
                    ]
                },
                {
                    type: 'text',
                    name: 'cin',
                    label: 'CIN',
                    placeholder: "Entrez le CIN"
                },
                {
                    type: 'text',
                    name: 'mission_address',
                    label: 'Address de mission',
                    placeholder: "Entrez l'address de mission"
                },
                {
                    type: 'text',
                    name: 'mission_objective',
                    label: "L'objectif de mission",
                    placeholder: "Entrez l'objectif de mission"
                },
                {
                    type: 'text',
                    name: 'transport',
                    label: "Le transport",
                    placeholder: "Entrez le transport"
                },
                {
                    type: 'text',
                    name: 'remuneration',
                    label: "La remuneration",
                    placeholder: "Entrez la remuneration"
                },
                {
                    type: 'date',
                    name: 'start_date',
                    label: "Date de début",
                },
                {
                    type: 'date',
                    name: 'end_date',
                    label: "Date de fin",
                },
            ],

            //demande de conge table structure
            demandeDeCongefields: [
                {
                    type: 'select',
                    name: 'type',
                    label: 'Type de demande',
                    options: [
                        { value: 'demande_conge', label: 'Demande congé' },
                    ]
                },
                {
                    type: 'text',
                    name: 'position',
                    label: 'Position',
                    placeholder: "Entres votre position"
                },
                {
                    type: 'number',
                    name: 'days',
                    label: 'Les jours',
                    placeholder: "Entrez number les jours de congé"
                },
                {
                    type: 'text',
                    name: 'reason',
                    label: 'le raison de congé',
                    placeholder: "Entrez le raison"
                },
                {
                    type: 'date',
                    name: 'start_date',
                    label: "Date de début",
                },
                {
                    type: 'date',
                    name: 'end_date',
                    label: "Date de fin",
                },
            ],

            title: "Crée une demande",
            btn_text: "Crée une demande",
            user: useAuthStore().user
        };
    },
    methods: {
        chooseDemand(type) {
            if (type == "ordre_de_mission") {
                this.demandType = "ordre_de_mission";
            } else if (type == "demande_conge") {
                this.demandType = "demande_conge";
            } else if (type == "attestation_de_travail") {
                this.demandType = "attestation_de_travail"
            }
            console.log(this.demandType);

        },

        //create demande De Conge method
        async createDemande(data) {
            const query = {
                user_id: this.user.id,
                type: this.demandType,
                details: {}
            }

            if (this.demandType == "demande_conge") {
                query.details = {
                    position: data.position,
                    days: data.days,
                    reason: data.reason,
                    start_date: data.start_date,
                    end_date: data.end_date
                }
            } else if (this.demandType == "ordre_de_mission") {
                query.details = {
                    cin: data.cin,
                    mission_address: data.mission_address,
                    mission_objective: data.mission_objective,
                    transport: data.transport,
                    remuneration: data.remuneration,
                    start_date: data.start_date,
                    end_date: data.end_date
                }
            }
            // console.log(query);
            try {
                console.log(query);
                const response = await utils.fetch_methode(services.demand.create, query);
                const data = await response.json();

                console.log(data.message);

                if (data.code  === 400) {
                    utils.errorAlert(data.message);
                    return;
                }

                if (response.status === 201) {
                    utils.successAlert("votre demande a été créée");
                    router.push({ "name": "userDemands" });
                }

            } catch (err) {
                console.error('Error:', err);
                utils.errorAlert("Une erreur inattendue s'est produite");
            }
        }
    },
    mounted() {

    },
    components: {
        FormComponent
    }
};
</script>

<style scoped>
/* Animation pour le hover et les transitions */
.group:hover {
  transform: translateY(-1px);
}

/* Animation pour le focus */
.group:active {
  transform: translateY(1px);
}
</style>
