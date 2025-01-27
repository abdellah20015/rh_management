<template>
    <div class="flex justify-center">
        <div class="w-11/12">
            <div class="flex items-center justify-between p-5 my-3">
                <p class="text-2xl font-semibold">Listes des demands</p>
                <RouterLink :to="{ name: 'create_demand' }" v-if="this.user.role != 'admin'"
                    class="w-48 bg-black text-center text-white rounded p-2">
                    Ajouter un demande</RouterLink>
            </div>
            <div class="flex flex-col justify-between w-full">
                <div class="w-full flex justify-between">
                    <div class="w-1/3">
                        <input type="text" placeholder="Search" v-model="searchValue"
                            class="p-2 border border-gray-300 w-2/3 mr-2 rounded ">
                        <button @click="search" class="w-1/4 p-2 rounded bg-black text-white">Search</button>
                    </div>
                    <div class="w-36">
                        <button @click="toggleFilterDropdown"
                            class="w-full p-2 rounded bg-black text-white flex justify-evenly items-center">
                            <p>Filter</p>
                            <img width="25" height="25" src="https://img.icons8.com/sf-black/64/FFFFFF/expand-arrow.png"
                                alt="expand-arrow" />
                        </button>
                    </div>
                </div>
                <div class="w-full flex justify-end">
                    <div class="w-1/2 relative justify-end">
                        <!-- Filter Dropdown with Transition -->
                        <transition name="dropdown">
                            <div v-if="showFilterDropdown"
                                class="absolute top-full left-0 bg-white rounded-md border border-gray-300 w-full p-3 mt-2 z-10 shadow-lg">
                                <div v-for="(filter, index) in filterStructure" :key="index">
                                    <p class="text-sm font-semibold">{{ filter.name }} :</p>
                                    <div class="flex justify-evenly p-3">
                                        <a href="#" @click.prevent="selectFilter(value)"
                                            v-for="(value, index) in filter.values" :key="index"
                                            class="rounded-full py-1 px-3 hover:bg-black hover:text-white transition-all ease-in-out"
                                            :class="{ 'bg-black text-white': value.selected === true }">
                                            <p class="text-sm">{{ value.title }}</p>
                                        </a>
                                    </div>
                                </div>
                                <div class="flex gap-5 mt-3">
                                    <button @click="filter"
                                        class="w-1/2 p-2 rounded bg-black text-white">Filtre</button>
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
                    // {
                    //     button: `<button style='background-color : #d90429; padding : 7px; color : white;border-radius : 2px ; border : none'><img width="20" height="20" src="https://img.icons8.com/ios-filled/50/FFFFFF/cancel-2.png" alt="cancel-2"/></button>`,
                    //     action: this.rejecetDemand,
                    //     disabled: (demand) => demand.status === "rejected"
                    // },
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

        //approve demand by manager
        async approveDemand(demand) {
            try {
                if (demand.status !== "approved") {
                    const response = await utils.fetch_methode(services.demand.update, { demand_id: demand._id, status: "approved" });
                    const data = await response.json();

                    if (response.ok) {
                        console.log(data);
                        utils.successAlert("Demand has been approved")
                        this.fetchDemands()
                    } else {
                        console.log(response);
                    }
                }
            } catch (err) {
                console.log(err);
            }
        },

        //reject demand by manager
        async rejecetDemand(demand) {
            try {
                if (demand.status !== "rejected") {
                    const response = await utils.fetch_methode(services.demand.update, { demand_id: demand._id, status: "rejected" });
                    const data = await response.json();

                    if (response.ok) {
                        console.log(data);
                        utils.successAlert("Demand has been rejected")
                        this.fetchDemands()
                    } else {
                        console.log(response);
                        console.log(JSON.stringify(this.user));

                    }
                }
            } catch (err) {
                console.log(err);
            }
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
