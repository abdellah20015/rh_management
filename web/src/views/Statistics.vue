<template>
    <div class="statistics bg-gray-50 p-6 ">
        <h1 class=" text-3xl font-bold text-gray-800 mb-10">Dashboard Statistics</h1>
        <div class="flex flex-col gap-20">

            <!-- cards for manaerg and admin -->
            <div class="grid grid-cols-4 gap-6" v-if="user.role === 'admin' || user.role === 'manager'">
                <Card
                    class="shadow-md hover:shadow-[rgba(0,_0,_0,_0.24)_0px_3px_8px] transition-all ease-in-out overflow-hidden border border-gray-300"
                    :style="{ backgroundColor: '#FFFFFF', borderLeft: '8px solid #3B82F6' }">
                    <template #content>
                        <div class="p-4 flex justify-between flex-row-reverse">
                            <div>
                                <img width="35" height="35"
                                    src="https://img.icons8.com/ios-glyphs/30/3b82f6/conference-call--v1.png"
                                    alt="conference-call--v1" />
                            </div>
                            <div class="flex justify-between items-center">
                                <div>
                                    <p class="text-md text-gray-500 mb-2">Total des utilisateurs</p>
                                    <div class="text-4xl font-bold text-gray-800">{{ totalUsers }}</div>
                                </div>
                                <i class="pi pi-users text-blue-500 text-4xl opacity-50"></i>
                            </div>
                        </div>
                    </template>
                </Card>

                <Card
                    class="shadow-md hover:shadow-[rgba(0,_0,_0,_0.24)_0px_3px_8px] transition-all ease-in-out overflow-hidden border border-gray-300"
                    :style="{ backgroundColor: '#FFFFFF', borderLeft: '8px solid #3B82F6' }">
                    <template #content>
                        <div class="p-4 flex justify-between flex-row-reverse">
                            <div>
                                <img width="35" height="35" src="https://img.icons8.com/external-tanah-basah-glyph-tanah-basah/48/3b82f6/external-folders-library-tanah-basah-glyph-tanah-basah.png" alt="external-folders-library-tanah-basah-glyph-tanah-basah"/>
                            </div>
                            <div class="flex justify-between items-center">
                                <div>
                                    <p class="text-md text-gray-500 mb-2">Demandes totales</p>
                                    <div class="text-4xl font-bold text-gray-800">{{ totalDemands }}</div>
                                </div>

                            </div>
                        </div>
                    </template>
                </Card>

                <Card
                    class="shadow-md hover:shadow-[rgba(0,_0,_0,_0.24)_0px_3px_8px] transition-all ease-in-out overflow-hidden border border-gray-300"
                    :style="{ backgroundColor: '#FFFFFF', borderLeft: '8px solid #F59E0B' }">
                    <template #content>
                        <div class="p-4 flex justify-between flex-row-reverse">
                            <div>
                                <img width="35" height="35" src="https://img.icons8.com/external-tanah-basah-glyph-tanah-basah/48/f59e0b/external-pending-folder-tanah-basah-glyph-tanah-basah.png" alt="external-pending-folder-tanah-basah-glyph-tanah-basah"/>
                            </div>
                            <div class="flex justify-between items-center">
                                <div>
                                    <p class="text-md text-gray-500 mb-2">Demandes en attente</p>
                                    <div class="text-4xl font-bold text-gray-800">{{ totalPendingDemands }}</div>
                                </div>

                            </div>
                        </div>
                    </template>
                </Card>

                <Card
                    class="shadow-md hover:shadow-[rgba(0,_0,_0,_0.24)_0px_3px_8px] transition-all ease-in-out overflow-hidden border border-gray-300"
                    :style="{ backgroundColor: '#FFFFFF', borderLeft: '8px solid #10B981' }">
                    <template #content>
                        <div class="p-4 flex justify-between flex-row-reverse">
                            <div>
                                <img width="35" height="35"
                                    src="https://img.icons8.com/ios-glyphs/30/10B981/conference-call--v1.png"
                                    alt="conference-call--v1" />
                            </div>
                            <div class="flex justify-between items-center">
                                <div>
                                    <p class="text-md text-gray-500 mb-2">Utilisateurs actifs</p>
                                    <div class="text-4xl font-bold text-gray-800">{{ totalActiveUsers }}</div>
                                </div>
                                <i class="pi pi-check-circle text-indigo-500 text-4xl opacity-50"></i>
                            </div>
                        </div>
                    </template>
                </Card>
            </div>


            <!-- cards for employee -->
            <div class="grid grid-cols-4 gap-6" v-else>
                <Card
                    class="shadow-md hover:shadow-[rgba(0,_0,_0,_0.24)_0px_3px_8px] transition-all ease-in-out overflow-hidden border border-gray-300"
                    :style="{ backgroundColor: '#FFFFFF', borderLeft: '8px solid #3B82F6' }">
                    <template #content>
                        <div class="p-4 flex justify-between flex-row-reverse">
                            <div>
                                <img width="35" height="35" src="https://img.icons8.com/external-tanah-basah-glyph-tanah-basah/48/3b82f6/external-folders-library-tanah-basah-glyph-tanah-basah.png" alt="external-folders-library-tanah-basah-glyph-tanah-basah"/>
                            </div>
                            <div class="flex justify-between items-center">
                                <div>
                                    <p class="text-md text-gray-500 mb-2">Total des demandes</p>
                                    <div class="text-4xl font-bold text-gray-800">{{ totalDemands }}</div>
                                </div>
                                <i class="pi pi-users text-blue-500 text-4xl opacity-50"></i>
                            </div>
                        </div>
                    </template>
                </Card>

                <Card
                    class="shadow-md hover:shadow-[rgba(0,_0,_0,_0.24)_0px_3px_8px] transition-all ease-in-out overflow-hidden border border-gray-300"
                    :style="{ backgroundColor: '#FFFFFF', borderLeft: '8px solid #10B981' }">
                    <template #content>
                        <div class="p-4 flex justify-between flex-row-reverse">
                            <div>
                                <img width="35" height="35" src="https://img.icons8.com/external-tanah-basah-glyph-tanah-basah/48/10b981/external-approved-approved-and-rejected-tanah-basah-glyph-tanah-basah-6.png" alt="external-approved-approved-and-rejected-tanah-basah-glyph-tanah-basah-6"/>
                            </div>
                            <div class="flex justify-between items-center">
                                <div>
                                    <p class="text-md text-gray-500 mb-2">Demandes approuvées</p>
                                    <div class="text-4xl font-bold text-gray-800">{{ totalApprovedDemands }}</div>
                                </div>

                            </div>
                        </div>
                    </template>
                </Card>

                <Card
                    class="shadow-md hover:shadow-[rgba(0,_0,_0,_0.24)_0px_3px_8px] transition-all ease-in-out overflow-hidden border border-gray-300"
                    :style="{ backgroundColor: '#FFFFFF', borderLeft: '8px solid #F59E0B' }">
                    <template #content>
                        <div class="p-4 flex justify-between flex-row-reverse">
                            <div>
                                <img width="35" height="35" src="https://img.icons8.com/external-tanah-basah-glyph-tanah-basah/48/f59e0b/external-pending-folder-tanah-basah-glyph-tanah-basah.png" alt="external-pending-folder-tanah-basah-glyph-tanah-basah"/>
                            </div>
                            <div class="flex justify-between items-center">
                                <div>
                                    <p class="text-md text-gray-500 mb-2">Demandes en attente</p>
                                    <div class="text-4xl font-bold text-gray-800">{{ totalPendingDemands }}</div>
                                </div>

                            </div>
                        </div>
                    </template>
                </Card>

                <Card
                    class="shadow-md hover:shadow-[rgba(0,_0,_0,_0.24)_0px_3px_8px] transition-all ease-in-out overflow-hidden border border-gray-300"
                    :style="{ backgroundColor: '#FFFFFF', borderLeft: '8px solid #e5383b' }">
                    <template #content>
                        <div class="p-4 flex justify-between flex-row-reverse">
                            <div>
                                <img width="35" height="35" src="https://img.icons8.com/external-tanah-basah-glyph-tanah-basah/48/e5383b/external-rejected-approved-and-rejected-tanah-basah-glyph-tanah-basah-11.png" alt="external-rejected-approved-and-rejected-tanah-basah-glyph-tanah-basah-11"/>
                            </div>
                            <div class="flex justify-between items-center">
                                <div>
                                    <p class="text-md text-gray-500 mb-2"> Demandes rejetées</p>
                                    <div class="text-4xl font-bold text-gray-800">{{ totalRejectedDemands }}</div>
                                </div>
                                <i class="pi pi-check-circle text-indigo-500 text-4xl opacity-50"></i>
                            </div>
                        </div>
                    </template>
                </Card>
            </div>


            <!-- tables for manager and admin -->
            <div class="grid grid-cols-2 gap-6" v-if="user.role === 'admin' || user.role === 'manager'">
                <!-- Upcoming Contract Expirations -->
                <Card :style="{ backgroundColor: '#FFFFFF', borderTop: '8px solid #6C7F93' }" class="h-72 border border-gray-300">
                    <template #title>
                        <div class="flex justify-between items-center">
                            <h2 class="text-lg font-semibold text-gray-800">Prochaines expirations de contrats</h2>
                        </div>
                    </template>
                    <template #content>
                        <DataTable :value="contractExpirations" responsiveLayout="scroll">
                            <Column field="username" header="User"></Column>
                            <Column field="end_date" header="Expiration Date"></Column>
                            <Column field="days_until_expiration" header="Days Left">
                                <template #body="slotProps">
                                    {{ slotProps.data.days_until_expiration }} days
                                </template>
                            </Column>
                        </DataTable>
                    </template>
                </Card>

                <!-- Recent Demands -->
                <Card :style="{ backgroundColor: '#FFFFFF', borderTop: '8px solid #6C7F93' }" class="border border-gray-300">
                    <template #title>
                        <div class="flex justify-between items-center">
                            <h2 class="text-lg font-semibold text-gray-800">Demandes récentes</h2>
                        </div>
                    </template>
                    <template #content>
                        <DataTable :value="recentDemands" responsiveLayout="scroll">
                            <Column field="type" header="type"></Column>
                            <Column field="status" header="Status"></Column>
                            <Column field="created_date"   header="Date">

                            </Column>
                        </DataTable>
                    </template>
                </Card>
            </div>


            <!-- tables for employee -->
            <div class="grid grid-cols-2 gap-6" v-else>
                <!-- Upcoming Contract Expirations -->
                <Card :style="{ backgroundColor: '#FFFFFF', borderTop: '8px solid #3B82F6' }" class="h-72 border border-gray-300">
                    <template #title>
                        <div class="flex justify-between items-center">
                            <h2 class="text-lg font-semibold text-gray-800">Dernières demandes</h2>
                        </div>
                    </template>
                    <template #content>
                        <DataTable :value="contractExpirations" responsiveLayout="scroll">
                            <Column field="client" header="User"></Column>
                            <Column field="expirationDate" header="Expiration Date"></Column>
                            <Column header="Actions">

                            </Column>
                        </DataTable>
                    </template>
                </Card>

                <!-- Recent Demands -->
                <Card :style="{ backgroundColor: '#FFFFFF', borderTop: '8px solid #3B82F6' }" class="h-72 border border-gray-300">
                    <template #title>
                        <div class="flex justify-between items-center">
                            <h2 class="text-lg font-semibold text-gray-800">dernières demandes en cours</h2>
                        </div>
                    </template>
                    <template #content>
                        <DataTable :value="recentDemands" responsiveLayout="scroll">
                            <Column field="demandId" header="ID"></Column>
                            <Column field="status" header="Status"></Column>
                            <Column field="createdAt" header="Date"></Column>
                            <Column header="Actions">
                                <template #body="slotProps">
                                    <Button icon="pi pi-eye" class="p-button-rounded p-button-info p-button-text"
                                        @click="viewDemandDetails(slotProps.data)" />
                                </template>
                            </Column>
                        </DataTable>
                    </template>
                </Card>
            </div>
        </div>
    </div>
</template>

<script>
import Card from 'primevue/card';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';


import { useAuthStore } from '@/stores/store';
import utils from '@/shared/utils';
import services from '@/shared/services';


export default {
    name: 'Statistics',
    data() {
        return {
            totalUsers: 0,
            totalDemands: 0,
            totalPendingDemands: 0,
            totalActiveUsers: 0,
            totalRejectedDemands: 0,
            totalApprovedDemands: 0,
            contractExpirations: [],
            recentDemands: [],
            user: useAuthStore().user,
        };
    },
    components: {
        Card,
        DataTable,
        Column
    },
    mounted() {

       this.fetchExpiringContracts();
       this.fetchUserStats();
        this.fetchDemandsStats();

    },
    methods: {
        async fetchExpiringContracts() {
            try {
                const response = await utils.fetch_methode('/private/contract/expiring');
                const data = await response.json();

                if (data.status === 'success') {
                    this.contractExpirations = data.data;
                }
            } catch (error) {
                console.error('Error fetching expiring contracts:', error);
                utils.errorAlert('Failed to fetch expiring contracts');
            }
        },

        async fetchUserStats(){
            try {
                const response = await utils.fetch_methode(services.user.stats)
                const data = await response.json()
                console.log(data)
                if (response.ok) {
                    this.totalUsers = data.total_users
                    this.totalActiveUsers = data.active
                }
                else{
                    console.log(data)
                }
            } catch (error) {
                console.log(error)
            }
        }  ,

        async fetchDemandsStats(){
            try {
                const response = await utils.fetch_methode(services.demand.stats)
                const data = await response.json()
                console.log(data)
                if (response.ok) {
                    this.totalDemands = data.total_demands
                    this.totalPendingDemands = data.pending
                    if (this.user.role !== "employeee") {
                        this.recentDemands = data.last_demands
                    }
                    else{
                        this.recentDemands = data.last_pending_demands
                        this.totalApprovedDemands = data.approved
                        this.totalRejectedDemands = data.rejected
                    }

                this.recentDemands.forEach(demand => {
                    demand.created_date = utils.convertDate(demand.created_date)
                })
                }
                else{
                    console.log(data)
                }
            } catch (error) {
                console.log(error)
            }
        }  ,


    }
};
</script>
