<template>
    <div class="flex justify-center">
        <div class="w-11/12">
            <div class="flex items-center justify-between p-5">
                <p class="text-2xl font-semibold">Listes des demands</p>
                <button class="w-48 bg-black text-white rounded p-2">Ajouter un demande</button>
            </div>
            <div>
                <TableComponent :tableInfo :pageSize :currentPage :totalPages></TableComponent>
            </div>
        </div>
    </div>
</template>

<script>
import TableComponent from '@/components/TableComponent.vue';
import services from '@/shared/services';
import fetch_methode from "@/shared/utils"
export default {
    name: 'DemandsList',
    data() {
        return {
            tableInfo: {
                headers: [
                    { title: "Type de demand", key: "type" },
                    { title: "Username", key: "username" },
                    { title: "Status", key: "status" },
                    { title: "created_date", key: "created_date" },
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
            pageSize: 10,
            currentPage: 1,
            totalPages: 1,
        };
    },
    methods: {
        //fetch demand list
        async fetchDemands() {
            await fetch_methode(services.demand.list, { query: {}, options: { "page": this.currentPage, "limit": this.pageSize } })
                .then((data) => {
                    this.tableInfo.data = data.data
                    console.log(data);
                })
        },

        //approve demand by manager
        async approveDemand(demand) {
            if (demand.status !== "approved") {
                try {
                    await fetch_methode(services.demand.update, { demand_id: demand._id, status: "approved" })
                        .then((res) => {
                            console.log(res);
                            this.fetchDemands()
                        });
                } catch (err) {
                    console.log(err);
                }
            }
        },

        //reject demand by manager
        async rejecetDemand(demand) {
            if (demand.status !== "rejected") {
                try {
                    await fetch_methode(services.demand.update, { demand_id: demand._id, status: "rejected" })
                        .then((res) => {
                            console.log(res);
                            this.fetchDemands()
                        });
                } catch (err) {
                    console.log(err);
                }
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
