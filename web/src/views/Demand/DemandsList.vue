<template>
    <div class="flex justify-center">
        <div class="w-11/12">
            <div class="flex items-center justify-between p-5 my-3">
                <p class="text-2xl font-semibold">Listes des demands</p>
                <button class="w-48 bg-black text-white rounded p-2">Ajouter un demande</button>
            </div>
            <div v-if="user.role == 'manager'">
                <TableComponent :tableInfo="managerTableInfo" :pageSize="pageSize" :currentPage="currentPage" :totalPages="totalPages"></TableComponent>
            </div>
            <div v-if="user.role == 'employee'">
                <TableComponent :tableInfo="employeeTableInfo" :pageSize="pageSize" :currentPage="currentPage" :totalPages="totalPages"></TableComponent>
            </div>
        </div>
    </div>
</template>

<script>
import TableComponent from '@/components/TableComponent.vue';
import services from '@/shared/services';
import utils from "@/shared/utils";
import { useAuthStore } from '@/stores/store';

export default {
    name: 'DemandsList',
    data() {
        return {
            //manager table infos
            managerTableInfo: {
                headers: [
                    { title: "Type de demand", key: "type" },
                    { title: "Username", key: "username" },
                    { title: "Status", key: "status" },
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
                        button: `<button style='background-color : #495057; padding : 7px; color : white;border-radius : 2px ; border : none'><img width="20" height="20" src="https://img.icons8.com/ios-filled/50/FFFFFF/print.png" alt="print"/></button>`,
                        action: "",
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
                        action: "",
                        disabled: false
                    }
                ],
            },
            pageSize: 10,
            currentPage: 1,
            totalPages: 1,
            user : useAuthStore().user,
        };
    },
    methods: {
        //fetch demand list
        async fetchDemands() {
            try {
                const response = await utils.fetch_methode(services.demand.list, { query: {}, options: { "page": this.currentPage, "limit": this.pageSize } })
                const data = await response.json();
                if (response.ok) {
                    if(this.user.role == "manager"){
                        this.managerTableInfo.data = data.data.reverse()
                    }else if(this.user.role == "employee") {
                        this.employeeTableInfo.data = data.data.reverse()
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
                    const response = await fetch_methode(services.demand.update, { demand_id: demand._id, status: "rejected" });
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
