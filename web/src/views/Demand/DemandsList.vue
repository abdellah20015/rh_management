<template>
    <div class="flex justify-center">
        <div class="w-11/12">
            <div class="flex items-center justify-between p-5 my-3">
                <p class="text-2xl font-semibold">Listes des demands</p>
                <RouterLink :to="{ name: 'create_demand' }" v-if="this.user.role != 'admin'"
                    class="w-48 bg-black text-center text-white rounded p-2">
                    Ajouter un demande</RouterLink>
            </div>
            <div class="flex items-center justify-between p-5 ">
                <TableFilterComponent :filterStructure="filterStructure" @filterHandler="filterHandler"
                    @searchHandler="searchHandler" @resertFilterHandler="resertFilterHandler">
                </TableFilterComponent>
            </div>
            <div v-if="user.permissions.includes('update_demand')">
                <TableComponent :tableInfo="managerTableInfo" :pageSize="pageSize" :currentPage="currentPage"
                    :totalPages="totalPages" @page-changed="handlePageChange"></TableComponent>
            </div>
            <div v-if="!user.permissions.includes('update_demand')">
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
            //manager table infos
            managerTableInfo: {
                headers: [
                    { title: "Type de demand", key: "typeTitle" },
                    { title: "Username", key: "username" },
                    { title: "Status", key: "statusTitle" },
                    { title: "Created date", key: "created_date" },
                    { title: "Actions", key: "actions" }
                ],
                data: [],
                buttons: [
                    {
                        button: `<button style='background-color : #38b000; padding : 7px; color : white;border-radius : 2px ; border : none'><img width="20" height="20" src="https://img.icons8.com/external-tal-revivo-bold-tal-revivo/24/FFFFFF/external-select-checkmark-symbol-to-choose-true-answer-basic-bold-tal-revivo.png" alt="external-select-checkmark-symbol-to-choose-true-answer-basic-bold-tal-revivo"/></button>`,
                        action: this.approveDemand,
                        disabled: (demand) => demand.status === "approved"
                    },
                    {
                        button: `<button style='background-color : #d90429; padding : 7px; color : white;border-radius : 2px ; border : none'><img width="20" height="20" src="https://img.icons8.com/ios-filled/50/FFFFFF/cancel-2.png" alt="cancel-2"/></button>`,
                        action: this.rejecetDemand,
                        disabled: (demand) => demand.status === "rejected"
                    },
                    {
                        button: `<button style='background-color : #023047; padding : 3px; color : white;border-radius : 2px ; border : none'><img width="28" height="28" src="https://img.icons8.com/sf-black-filled/50/FFFFFF/pdf-2.png" alt="pdf-2"/></button>`,
                        action: this.downloadDemand,
                         disabled: (demand) => demand.status === "rejected"
                    }
                ],
            },

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
        };
    },
    methods: {
        //fetch demand list
        async fetchDemands() {
            try {
                const response = await utils.fetch_methode(services.demand.list, { query: {}, options: { "page": this.currentPage, "limit": this.pageSize } })
                const data = await response.json();
                if (response.ok) {

                    //pagination
                    this.count = data.count;
                    this.count += (this.currentPage - 1) * this.pageSize;
                    console.log(this.count);

                    if (this.count > this.pageSize) {
                        this.totalPages = Math.ceil(this.count / this.pageSize);
                    }

                    console.log("count" + this.count);
                    console.log("pageSize" + this.pageSize);
                    console.log("totalPages" + this.totalPages);

                    const processedData = data.data.map((item) => ({
                        //create new object from the original objct
                        ...item,
                        //chaneg type value to new value
                        typeTitle: utils.formatString(item.type),
                        statusTitle: item.status === "approved" ? "Acceptée" : item.status === "rejected" ? "Rejectée" : item.status === "pending" ? "En attente" : item.status,
                    }));

                    if (this.user.role == 'manager' || this.user.role == "admin") {
                        this.managerTableInfo.data = processedData.reverse();
                    } else if (this.user.role == 'employee') {
                        this.employeeTableInfo.data = processedData.reverse();
                    }
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
            console.log(query)
            try {
                const response = await utils.fetch_methode(services.file.download, query)
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.style.display = 'none';
                a.href = url;
                const filename = demand.file_path.split('/').pop();
                a.download = filename;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
                if (response.ok) {
                    console.log(response);
                } else {
                    console.log(response);
                }
            } catch (err) {
                console.log(err);
            }
        },

        //hanlder filter
        filterHandler(filterData) {
            const currentData = this.user.role === "manager"
                ? this.managerTableInfo.data
                : this.employeeTableInfo.data;

            if (filterData.length > 0) {
                // Group filters by field name
                const groupedFilters = {};
                filterData.forEach(filter => {
                    const fieldName = Object.keys(filter)[0];
                    const value = filter[fieldName];

                    if (!groupedFilters[fieldName]) {
                        groupedFilters[fieldName] = [];
                    }
                    groupedFilters[fieldName].push(value);
                });

                // Filter data based on grouped filters
                const filteredData = currentData.filter((demande) => {
                    return Object.keys(groupedFilters).every((fieldName) => {
                        return groupedFilters[fieldName].some(filterValue =>
                            String(demande[fieldName]).toLowerCase() === String(filterValue).toLowerCase()
                        );
                    });
                });

                // update table with filtred data
                if (this.user.role === "manager") {
                    this.managerTableInfo.data = filteredData;
                } else {
                    this.employeeTableInfo.data = filteredData;
                }
            }
        },


        //search handle
        searchHandler(searchValue) {

            //get demande if value empty
            if (searchValue == "") {
                this.fetchDemands()
            }

            const currentData = this.user.role === "manager"
                ? this.managerTableInfo.data
                : this.employeeTableInfo.data;

            if (!searchValue) {
                return currentData;
            }

            const searchedData = currentData.filter((demande) => {
                return Object.values(demande).some(value =>
                    String(value).toLowerCase().includes(searchValue.toLowerCase())
                );
            });

            if (this.user.role === "manager") {
                this.managerTableInfo.data = searchedData;
            } else {
                this.employeeTableInfo.data = searchedData;
            }
        },

        //resert filter
        resertFilterHandler() {
            this.fetchDemands();
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
