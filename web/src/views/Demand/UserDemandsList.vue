<template>
    <div class="flex justify-center">
        <div class="w-11/12">
            <div class="flex items-center justify-between  mt-5 mb-14">
                <div class="flex  gap-3">
                    <p class="text-3xl font-semibold">Listes des demands</p>
                    <p class="text-[#006aff] bg-[#c6dffb] py-1 px-3 rounded-[6px] font-semibold text-base">
                        {{ count }}</p>
                </div>
                <RouterLink :to="{ name: 'create_demand' }" v-if="this.user.role != 'admin'"
                    class="w-48 bg-[#006AFF] hover:bg-[#006AFF]/80 transition-all ease-in-out text-center text-white rounded p-2">
                    Ajouter un demande</RouterLink>
            </div>
            <div class="flex flex-col justify-between w-full">
                <div class="w-full flex justify-between">
                    <div class="w-1/3">
                        <div class="relative flex items-center ">
                            <input type="text" placeholder="Search" v-model="searchValue"
                                class="w-full p-2.5 rounded-[5px] pl-4 pr-32 border border-[#99C4FF]  focus:outline-none focus:ring-2 focus:ring-[#006AFF]/50 focus:border-[#006AFF] transition-all duration-200">
                            <button @click="search"
                                class="absolute rounded-r-[5px] right-0 h-full px-4 bg-[#006AFF] text-white hover:bg-[#006AFF]/90 transition-all duration-200 flex items-center justify-center gap-2">
                                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24"
                                    stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                                </svg>
                                <span>Rechercher</span>
                            </button>
                        </div>
                    </div>
                    <div class="w-36">
                        <button @click="toggleFilterDropdown"
                            class="w-full p-2.5 rounded-[5px]  bg-[#006AFF] text-white hover:bg-[#006AFF]/90 transition-all duration-200 flex items-center justify-center gap-2">
                            <span>Filter</span>
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24"
                                stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                    d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                            </svg>
                        </button>
                    </div>
                </div>
                <div class="w-full flex justify-end">
                    <div class="w-1/3 relative justify-end">
                        <!-- Filter Dropdown with Transition -->
                        <transition name="dropdown">
                            <div v-if="showFilterDropdown"
                                class="absolute top-full left-0 bg-white rounded-md border border-gray-300 w-full p-3 mt-2 z-10 shadow-lg">
                                <div v-for="(filter, index) in filterStructure" :key="index">
                                    <p class="text-sm font-semibold text-gray-600 p-1.5">{{ filter.name }}</p>
                                    <div class="flex justify-evenly p-3">
                                        <a href="#" @click.prevent="selectFilter(value)"
                                            v-for="(value, index) in filter.values" :key="index"
                                            class="rounded-full py-1.5 px-5 hover:bg-[#99CAFF] hover:text-white transition-all ease-in-out"
                                            :class="{ 'bg-[#3b82f6] text-white': value.selected === true }">
                                            <p class="text-sm">{{ value.title }}</p>
                                        </a>
                                    </div>
                                </div>
                                <div class="flex gap-5 mt-3">
                                    <button @click="filter"
                                        class="w-1/2 p-2 rounded bg-[#3b82f6] text-white">Filtre</button>
                                    <button @click="resertFilter" class="w-1/2 p-2 rounded bg-gray-300">Réinitialiser le
                                        filtre</button>
                                </div>
                            </div>
                        </transition>
                    </div>
                </div>
            </div>
            <div>
                <TableComponent :tableInfo="employeeTableInfo" :pageSize="pageSize" :currentPage="currentPage"
                    :totalPages="totalPages" @page-changed="handlePageChange"></TableComponent>
            </div>
        </div>
    </div>
</template>

<script>
import TableComponent from '@/components/TableComponent.vue';
import TableFilterComponent from '@/components/TableFilterComponent.vue';
import services from '@/shared/services';
import utils from "@/shared/utils";
import { useAuthStore } from '@/stores/store';
import Swal from 'sweetalert2';
import { RouterLink } from 'vue-router';

export default {
    name: 'DemandsList',
    data() {
        return {

            //employee table infos
            employeeTableInfo: {
                headers: [
                    { title: "Type de demand", key: "typeTitle" },
                    { title: "Status", key: "statusTitle" },
                    { title: "created_date", key: "created_date" },
                    { title: "Actions", key: "actions" }
                ],
                data: [],
                buttons: [
                    // {
                    //     button: `<button style='background-color : #38b000; padding : 7px; color : white;border-radius : 2px ; border : none'><img width="20" height="20" src="https://img.icons8.com/external-tal-revivo-bold-tal-revivo/24/FFFFFF/external-select-checkmark-symbol-to-choose-true-answer-basic-bold-tal-revivo.png" alt="external-select-checkmark-symbol-to-choose-true-answer-basic-bold-tal-revivo"/></button>`,
                    //     action: this.approveDemand,
                    //     disabled: (demand) => demand.status === "approved"
                    // },
                    {
                        button: `<button style='background-color : #eeba0b; padding : 7px; color : white;border-radius : 2px ; border : none'><img width="20" height="20" src="https://img.icons8.com/sf-black-filled/64/FFFFFF/chat-message.png" alt="chat-message"/></button>`,
                        action: this.showReason,
                        disabled: (demand) => demand.status !== "rejected"
                    },
                    {
                        button: `<button style='background-color : #023047; padding : 3px; color : white;border-radius : 2px ; border : none'><img width="28" height="28" src="https://img.icons8.com/sf-black-filled/50/FFFFFF/pdf-2.png" alt="pdf-2"/></button>`,
                        action: this.downloadDemand,
                        disabled: false
                    }
                ],
            },

            //filter structure
            filterStructure: [
                {
                    name: "Type",
                    values: [
                        {
                            title: "Demande Conge",
                            value: "demande_conge",
                            key: "type",
                            selected: false
                        },
                        {
                            title: "Ordre De Mission",
                            value: "ordre_de_mission",
                            key: "type",
                            selected: false
                        },
                        {
                            title: "Attestation De Travail",
                            value: "attestation_de_travail",
                            key: "type",
                            selected: false
                        },
                    ]
                },
                {
                    name: "Status",
                    values: [
                        {
                            title: "Acceptée",
                            value: "approved",
                            key: "status",
                            selected: false
                        },
                        {
                            title: "Rejectée",
                            value: "rejected",
                            key: "status",
                            selected: false
                        },
                        {
                            title: "En attende",
                            value: "pending",
                            key: "status",
                            selected: false
                        },
                    ]
                },
            ],

            pageSize: 10,
            currentPage: 1,
            count: 0,
            totalPages: 1,
            user: useAuthStore().user,
            searchValue: null,
            filterData: {
                type: [],
                status: [],
            },
            showFilterDropdown: false
        };
    },
    methods: {

        toggleFilterDropdown() {
            this.showFilterDropdown = !this.showFilterDropdown;
        },

        //fetch demand list
        async fetchDemands() {
            try {
                const response = await utils.fetch_methode(services.demand.listByUser, {
                    query: {
                        filter: {
                            type: this.filterData.type,
                            status: this.filterData.status,
                        },
                        search: this.searchValue
                    },
                    options: { "page": this.currentPage, "limit": this.pageSize }
                })
                const data = await response.json();

                if (response.ok) {

                    //pagination
                    this.count = data.count;
                    this.count += (this.currentPage - 1) * this.pageSize;

                    if (this.count > this.pageSize) {
                        this.totalPages = Math.ceil(this.count / this.pageSize);
                    }

                    const processedData = data.data.map((item) => ({
                        //create new object from the original objct
                        ...item,
                        //chaneg type value to new value
                        typeTitle: utils.formatString(item.type),
                        statusTitle: item.status === "approved" ? "Acceptée" : item.status === "rejected" ? "Rejectée" : item.status === "pending" ? "En attente" : item.status,
                    }));

                    this.employeeTableInfo.data = processedData;

                } else {
                    console.log(response);
                }
            } catch (err) {
                console.log(err);
            }
        },

        showReason(demand) {
            console.log(demand);
            const title = "Raison de rejet";

            utils.showText(title, demand.reason)
        },
        //download demande pdf
        async downloadDemand(demand) {

            const query = { filepath: demand.file_path }
            try {
                const response = await utils.fetch_methode(services.file.download, query);


                if (demand.file_path.toLowerCase().endsWith('.pdf')) {

                    window.open(URL.createObjectURL(await response.blob()), '_blank');
                } else {

                    const blob = await response.blob();
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = demand.file_path.split('/').pop();
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                    window.URL.revokeObjectURL(url);
                }
            } catch (err) {
                console.error("Erreur de téléchargement:", err);
                utils.errorAlert("Impossible de visualiser ou télécharger le fichier");

            }
        },



        filter() {
            this.fetchDemands()
        },

        selectFilter(selectedValue) {
            selectedValue.selected = !selectedValue.selected

            const value = selectedValue.value
            const key = selectedValue.key

            if (key == "type") {
                this.filterData.type.push(value)
            }

            if (key == "status") {
                this.filterData.status.push(value)
            }

            console.log(this.filterData);

        },


        //search handle
        search() {
            this.fetchDemands();
        },

        //resert filter
        resertFilter() {
            this.filterData.status = [];
            this.filterData.type = [];
            this.filterStructure.forEach(filter => {
                filter.values.forEach(value => {
                    value.selected = false
                });
            });
            this.fetchDemands()
        },

        handlePageChange(newPage) {
            this.currentPage = newPage;
            this.fetchDemands();
        },

    },

    mounted() {
        this.fetchDemands();
    },
    components: {
        TableComponent,
        TableFilterComponent
    }
};
</script>

<style scoped>
/* Transition for dropdown */
.dropdown-enter-active,
.dropdown-leave-active {
    transition: opacity 0.3s ease, transform 0.3s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
    opacity: 0;
    transform: translateY(-10px);
}
</style>
