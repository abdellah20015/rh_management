<template>
    <div class="flex justify-center">
        <div class="w-11/12">
            <div class="flex items-center justify-between p-5 my-3">
                <p class="text-2xl font-semibold">Listes des demands</p>
                <RouterLink :to="{ name: 'create_demand' }" class="w-48 bg-black text-center text-white rounded p-2">
                    Ajouter un demande</RouterLink>
            </div>
            <div v-if="user.role == 'manager'">
                <TableComponent :tableInfo="managerTableInfo" :pageSize="pageSize" :currentPage="currentPage"
                    :totalPages="totalPages"></TableComponent>
            </div>
            <div v-if="user.role == 'employee'">
                <TableComponent :tableInfo="employeeTableInfo" :pageSize="pageSize" :currentPage="currentPage"
                    :totalPages="totalPages"></TableComponent>
            </div>
        </div>
    </div>
</template>

<script>
import TableComponent from '@/components/TableComponent.vue';
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
                        button: `<button style='background-color : #495057; padding : 3px; color : white;border-radius : 2px ; border : none'><img width="28" height="28" src="https://img.icons8.com/sf-black-filled/50/FFFFFF/pdf-2.png" alt="pdf-2"/></button>`,
                        action: this.downloadDemand,
                        disabled: false
                    }
                ],
            },

            //employee table infos
            employeeTableInfo: {
                headers: [
                    { title: "Type de demand", key: "type" },
                    { title: "Status", key: "status" },
                    { title: "created_date", key: "created_date" },
                    { title: "Actions", key: "actions" }
                ],
                data: [],
                buttons: [
                    {
                        button: `<button style='background-color : #495057; padding : 7px; color : white;border-radius : 2px ; border : none'><img width="20" height="20" src="https://img.icons8.com/ios-filled/50/FFFFFF/print.png" alt="print"/></button>`,
                        action: this.downloadDemand,
                        disabled: false
                    }
                ],
            },
            pageSize: 10,
            currentPage: 1,
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
                    const processedData = data.data.map((item) => ({
                        //create now object from the original objct
                        ...item,
                        //chaneg type value to new value
                        typeTitle: utils.formatString(item.type),
                        statusTitle: item.status === "approved" ? "Acceptée" : item.status === "rejected" ? "Rejectée" : item.status === "pending" ? "En attente" : item.status,
                    }));

                    if (this.user.role == 'manager') {
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
                const response = await utils.fetch_methode(services.file.download, query)
                if (response.ok) {
                    console.log(response);
                } else {
                    console.log(response);
                }
            } catch (err) {
                console.log(err);
            }
        }

    },
    mounted() {
        this.fetchDemands();
    },
    components: {
        TableComponent
    }
};
</script>
