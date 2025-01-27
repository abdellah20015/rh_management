<template>
    <div class="flex justify-center">
        <div class="w-11/12">
            <div class="mx-10">
                <div class="mx-5 my-5">
                    <RouterLink :to="{ name: 'userDemands' }"
                        class="w-20 flex items-center justify-center bg-black text-center text-white rounded py-1">
                        <img width="30" height="30"
                            src="https://img.icons8.com/ios-filled/50/FFFFFF/long-arrow-left.png"
                            alt="long-arrow-left" />
                    </RouterLink>
                </div>
            </div>
            <div class="w-full flex justify-center mb-10">
                <div
                    class="flex justify-evenly bg-gray-200 border-2 border-gray-300 w-11/12 p-3 rounded-md h-12 items-center">
                    <a href="#" @click="chooseDemand('demande_conge')"
                        class="w-80 text-center py-1 items-center rounded-md hover:bg-black hover:text-white transition-all delay-75 ease-in-out"
                        :class="{ 'bg-black text-white border border-gray-500 ': demandType === 'demande_conge' }">
                        <p>Demande congé</p>
                    </a>
                    <a href="#" @click="chooseDemand('ordre_de_mission')"
                        class="w-80 text-center py-1 items-center rounded-md hover:bg-black hover:text-white transition-all delay-75 ease-in-out"
                        :class="{ 'bg-black text-white border border-gray-500 ': demandType === 'ordre_de_mission' }">
                        <p>Ordre de mission</p>
                    </a>
                    <a href="#" @click="chooseDemand('attestation_de_travail')"
                        class="w-80 text-center py-1 items-center rounded-md hover:bg-black hover:text-white transition-all delay-75 ease-in-out"
                        :class="{ 'bg-black text-white border border-gray-500 ': demandType === 'attestation_de_travail' }">
                        <p>Attestation de travail</p>
                    </a>
                </div>
            </div>
            <div v-if="demandType == 'demande_conge'">
                <FormComponent :fields="demandeDeCongefields" :title="title" :btn_text="btn_text"
                    @formSubmitted="createDemande"></FormComponent>
            </div>
            <div v-else-if="demandType == 'ordre_de_mission'">
                <FormComponent :fields="orderDeMissionfields" :title="title" :btn_text="btn_text"
                    @formSubmitted="createDemande"></FormComponent>
            </div>
            <div v-else-if="demandType == 'attestation_de_travail'" class="flex justify-center">
                <button @click="createDemande()" class="w-48 bg-black text-center text-white rounded p-2">Ajouter un
                    demande</button>
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

<style></style>
