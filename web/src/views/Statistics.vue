<template>
    <div class="statistics bg-gray-50 p-6 ">
        <h1 class=" text-3xl font-bold text-gray-800 mb-10">Bienvenue {{ user.username }},</h1>
        <div class="flex flex-col gap-20">

            <div class="flex flex-col gap-8">
                <!-- Statistics Cards -->
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6"
                    v-if="user.role === 'admin' || user.role === 'manager'">
                    <!-- Admin/Manager Cards -->
                    <div v-for="(stat, index) in adminStats" :key="index"
                        class="bg-white rounded-2xl h-32 flex items-center w-full p-6 shadow-lg hover:shadow-xl transition-shadow duration-300 relative overflow-hidden">
                        <div class="absolute top-0 right-0 w-36 h-36 bg-gradient-to-br"
                            :class="stat.gradientColor + ' opacity-10 rounded-bl-full'">
                        </div>
                        <div class="flex justify-between items-start w-full">
                            <div>
                                <p class="text-gray-500 text-sm mb-1">{{ stat.title }}</p>
                                <h3 class="text-3xl font-bold text-gray-800">{{ stat.value }}</h3>
                            </div>
                            <div :class="`p-3 rounded-xl ${stat.iconBg}`">
                                <img :src="stat.icon" :alt="stat.title" class="w-10 h-10" />
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Employee Cards -->
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6" v-else>
                    <div v-for="(stat, index) in employeeStats" :key="index"
                        class="bg-white rounded-2xl h-32 flex items-center p-6 shadow-lg hover:shadow-xl transition-shadow duration-300 relative overflow-hidden">
                        <div class="absolute top-0 right-0 w-36 h-36 bg-gradient-to-br"
                            :class="stat.gradientColor + ' opacity-10 rounded-bl-full'">
                        </div>
                        <div class="flex justify-between items-start w-full">
                            <div>
                                <p class="text-gray-500 text-sm mb-1">{{ stat.title }}</p>
                                <h3 class="text-3xl font-bold text-gray-800">{{ stat.value }}</h3>
                            </div>
                            <div :class="`p-3 rounded-xl ${stat.iconBg}`">
                                <img :src="stat.icon" :alt="stat.title" class="w-10 h-10" />
                            </div>
                        </div>
                    </div>
                </div>
            </div>


            <!-- tables for manager and admin -->
            <div class="grid grid-cols-2 gap-6" v-if="user.role === 'admin' || user.role === 'manager'">
                <!-- Upcoming Contract Expirations -->
                <Card :style="{ backgroundColor: '#FFFFFF', borderTop: '8px solid #6C7F93' }"
                    class="h-96 border border-gray-300">
                    <template #title>
                        <div class="flex justify-between items-center">
                            <h2 class="text-lg font-semibold text-gray-800">Prochaines expirations de contrats</h2>
                        </div>
                    </template>
                    <template #content>
                        <DataTable :value="contractExpirations" responsiveLayout="scroll">
                            <Column field="username" header="Nom">
                                <template #body="slotProps">
                                    {{ getUserFullName(slotProps.data) }}
                                </template>
                            </Column>
                            <Column field="end_date" header="Expiration Date"></Column>
                            <Column field="days_until_expiration" header="Days Left">
                                <template #body="slotProps">
                                    {{ slotProps.data.days_until_expiration }} Jours
                                </template>
                            </Column>
                        </DataTable>
                    </template>
                </Card>

                <!-- Recent Demands -->
                <Card :style="{ backgroundColor: '#FFFFFF', borderTop: '8px solid #6C7F93' }"
                    class="border border-gray-300">
                    <template #title>
                        <div class="flex justify-between items-center">
                            <h2 class="text-lg font-semibold text-gray-800">Demandes récentes</h2>
                        </div>
                    </template>
                    <template #content>
                        <DataTable :value="recentDemands" responsiveLayout="scroll">
                            <Column field="type" header="Type">
                                <template #body="slotProps">
                                    {{ demandType(slotProps.data) }}
                                </template>
                            </Column>
                            <Column :field="getFullName" header="Nom">
                                <template #body="slotProps">
                                    {{ getUserFullNameInDemandes(slotProps.data.user) }}
                                </template>
                            </Column>
                            <Column field="status" header="Status"></Column>
                            <Column field="created_date" header="Date"></Column>
                        </DataTable>
                    </template>
                </Card>
            </div>


            <!-- tables for employee -->
            <div class="grid grid-cols-2 gap-6" v-else>
                <!-- Upcoming Contract Expirations -->
                <Card :style="{ backgroundColor: '#FFFFFF', borderTop: '8px solid #6C7F93' }"
                    class="h-96 border border-gray-300">
                    <template #title>
                        <div class="flex justify-between items-center">
                            <h2 class="text-lg font-semibold text-gray-800">Dernières demandes</h2>
                        </div>
                    </template>
                    <template #content>
                        <DataTable :value="last_demands" responsiveLayout="scroll">
                            <Column field="type" header="Type"></Column>
                            <Column field="status" header="status"></Column>
                            <Column field="created_date" header="Date"></Column>
                        </DataTable>
                    </template>
                </Card>

                <!-- Recent Demands -->

                <Card :style="{ backgroundColor: '#FFFFFF', borderTop: '8px solid #6C7F93' }"
                    class="h-96 border border-gray-300">
                    <template #title>
                        <div class="flex justify-between items-center">
                            <h2 class="text-lg font-semibold text-gray-800">Dernières demandes en cours</h2>
                        </div>
                    </template>
                    <template #content>
                        <DataTable :value="recentDemands" responsiveLayout="scroll">
                            <Column field="type" header="Type"></Column>
                            <Column field="status" header="Status"></Column>
                            <Column field="created_date" header="Date"></Column>
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
            last_demands: [],
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
                    console.log(data);

                    this.contractExpirations = data.data;
                }
            } catch (error) {
                console.error('Error fetching expiring contracts:', error);
                utils.errorAlert('Failed to fetch expiring contracts');
            }
        },

        async fetchUserStats() {
            try {
                const response = await utils.fetch_methode(services.user.stats)
                const data = await response.json()
                console.log(data)
                if (response.ok) {
                    this.totalUsers = data.total_users
                    this.totalActiveUsers = data.active
                }
                else {
                    console.log(data)
                }
            } catch (error) {
                console.log(error)
            }
        },

        async fetchDemandsStats() {
            try {
                const response = await utils.fetch_methode(services.demand.stats)
                const data = await response.json()
                console.log(data)
                if (response.ok) {
                    this.totalDemands = data.total_demands
                    this.totalPendingDemands = data.pending
                    if (this.user.role !== "employee") {
                        this.recentDemands = data.last_demands
                    }
                    else {
                        this.recentDemands = data.last_pending_demands
                        this.last_demands = data.last_demands
                        this.totalApprovedDemands = data.approved
                        this.totalRejectedDemands = data.rejected
                        this.last_demands.forEach(demand => {
                            demand.created_date = utils.convertDate(demand.created_date),
                                demand.status = demand.status === "approved" ? "Acceptée" : demand.status === "rejected" ? "Rejectée" : demand.status === "pending" ? "En attente" : demand.status
                        })
                    }

                    this.recentDemands.forEach(demand => {
                        demand.created_date = utils.convertDate(demand.created_date)
                        demand.status = demand.status === "approved" ? "Acceptée" : demand.status === "rejected" ? "Rejectée" : demand.status === "pending" ? "En attente" : demand.status
                    })
                }
                else {
                    console.log(data)
                }
            } catch (error) {
                console.log(error)
            }
        },

        getUserFullNameInDemandes(user) {
            if (!user || !user.firstName || !user.lastName) {
                return '-';
            }
            return `${user.firstName} ${user.lastName}`;
        },

        getUserFullName(user) {
            if (!user || !user.firstName || !user.lastName) {
                return '-';
            }
            return `${user.firstName} ${user.lastName}`;
        },

        demandType(demand){
            return utils.formatString(demand.type);
        }

    },
    computed: {
        adminStats() {
            return [
                {
                    title: 'Total des utilisateurs',
                    value: this.totalUsers,
                    icon: 'https://img.icons8.com/ios-glyphs/30/3b82f6/conference-call--v1.png',
                    gradientColor: 'from-blue-400 to-blue-600',
                    // iconBg: 'bg-blue-50'
                },
                {
                    title: 'Demandes totales',
                    value: this.totalDemands,
                    icon: 'https://img.icons8.com/external-tanah-basah-glyph-tanah-basah/48/3b82f6/external-folders-library-tanah-basah-glyph-tanah-basah.png',
                    gradientColor: 'from-blue-400 to-blue-600',
                    // iconBg: 'bg-purple-50'
                },
                {
                    title: 'Demandes en attente',
                    value: this.totalPendingDemands,
                    icon: 'https://img.icons8.com/external-tanah-basah-glyph-tanah-basah/48/f59e0b/external-pending-folder-tanah-basah-glyph-tanah-basah.png',
                    gradientColor: 'from-amber-400 to-amber-600',
                    // iconBg: 'bg-amber-50'
                },
                {
                    title: 'Utilisateurs actifs',
                    value: this.totalActiveUsers,
                    icon: 'https://img.icons8.com/ios-glyphs/30/10B981/conference-call--v1.png',
                    gradientColor: 'from-emerald-400 to-emerald-600',
                    // iconBg: 'bg-emerald-50'
                }
            ];
        },
        employeeStats() {
            return [
                {
                    title: 'Total des demandes',
                    value: this.totalDemands,
                    icon: 'https://img.icons8.com/external-tanah-basah-glyph-tanah-basah/48/3b82f6/external-folders-library-tanah-basah-glyph-tanah-basah.png',
                    gradientColor: 'from-blue-400 to-blue-600',
                    // iconBg: 'bg-blue-50'
                },
                {
                    title: 'Demandes approuvées',
                    value: this.totalApprovedDemands,
                    icon: 'https://img.icons8.com/external-tanah-basah-glyph-tanah-basah/48/10b981/external-approved-approved-and-rejected-tanah-basah-glyph-tanah-basah-6.png',
                    gradientColor: 'from-emerald-400 to-emerald-600',
                    // iconBg: 'bg-emerald-50'
                },
                {
                    title: 'Demandes en attente',
                    value: this.totalPendingDemands,
                    icon: 'https://img.icons8.com/external-tanah-basah-glyph-tanah-basah/48/f59e0b/external-pending-folder-tanah-basah-glyph-tanah-basah.png',
                    gradientColor: 'from-amber-400 to-amber-600',
                    // iconBg: 'bg-amber-50'
                },
                {
                    title: 'Demandes rejetées',
                    value: this.totalRejectedDemands,
                    icon: 'https://img.icons8.com/external-tanah-basah-glyph-tanah-basah/48/e5383b/external-rejected-approved-and-rejected-tanah-basah-glyph-tanah-basah-11.png',
                    gradientColor: 'from-red-400 to-red-600',
                    // iconBg: 'bg-red-50'
                }
            ];
        }
    },
};
</script>
