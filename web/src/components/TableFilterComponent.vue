<template>
    <div class="flex w-full justify-center">
        <div class="flex flex-col justify-between w-full">
            <div class="w-full flex justify-between">
                <div class="w-1/3">
                    <input type="text" placeholder="Search" v-model="searchValues.search"
                        class="p-2 border border-gray-300 w-2/3 mr-2 rounded ">
                    <button @click="search" class="w-1/4 p-2 rounded bg-black text-white">Search</button>
                </div>
                <div class="w-36">
                    <button @click="toggleFilterDropdown" class="w-full p-2 rounded bg-black text-white flex justify-evenly items-center">
                        <p>Filter</p>
                        <img width="25" height="25" src="https://img.icons8.com/sf-black/64/FFFFFF/expand-arrow.png" alt="expand-arrow" />
                    </button>
                </div>
            </div>
            <div class="w-full flex justify-end">
                <div class="w-1/2 relative justify-end">
                    <!-- Filter Dropdown with Transition -->
                    <transition name="dropdown">
                        <div v-if="showFilterDropdown" class="absolute top-full left-0 bg-white rounded-md border border-gray-300 w-full p-3 mt-2 z-10 shadow-lg">
                            <div v-for="(filter, index) in filterStructure" :key="index">
                                <p class="text-sm font-semibold">{{ filter.name }} :</p>
                                <div class="flex justify-evenly p-3">
                                    <a href="#" @click.prevent="selectFilter(value)" v-for="(value, index) in filter.values" :key="index"
                                        class="rounded-full py-1 px-3 hover:bg-black hover:text-white transition-all ease-in-out"
                                        :class="{ 'bg-black text-white': value.selected === true }">
                                        <p class="text-sm">{{ value.title }}</p>
                                    </a>
                                </div>
                            </div>
                            <div class="flex gap-5 mt-3">
                                <button @click="filter" class="w-1/2 p-2 rounded bg-black text-white">Filtre</button>
                                <button @click="resertFilter" class="w-1/2 p-2 rounded bg-gray-300">Réinitialiser le filtre</button>
                            </div>
                        </div>
                    </transition>
                </div>
            </div>
        </div>
    </div>
</template>



<script>
export default {
    name: 'TableFilterComponent',
    props: {
        filterStructure: {
            type: Object,
            required: true
        }
    },
    data() {
        return {
            searchValues: {
                search: "",
                filterValues: []
            },
            showFilterDropdown: false
        };
    },
    methods: {

        toggleFilterDropdown() {
            this.showFilterDropdown = !this.showFilterDropdown;
        },

        selectFilter(selectedValue) {
            selectedValue.selected = !selectedValue.selected
            const object = {
                [selectedValue.key]: selectedValue.value
            }
            if (selectedValue.selected == true) {
                this.searchValues.filterValues.push(object)
                console.log("after add value to filter : ");
                console.log(this.searchValues);
            } else {
                this.searchValues.filterValues = this.searchValues.filterValues.filter(object => Object.values(object) != selectedValue.value)
                console.log("after remove value from filter : ");
                console.log(this.searchValues);
            }
        },

        search() {
            console.log(this.searchValues.search);
            this.$emit('searchHandler', this.searchValues.search)
        },

        filter() {
            console.log(this.searchValues);
            this.$emit('filterHandler', this.searchValues.filterValues)
        },

        resertFilter() {
            this.$emit("resertFilterHandler");
            this.filterStructure.forEach((filterGroup) => {
                filterGroup.values.forEach((value) => {
                    value.selected = false;
                });
            });
        }
    },
    mounted() {
        // console.log(this.$props.filterStructure);
    }


};
</script>

<style scoped>
/* Transition for dropdown */
.dropdown-enter-active, .dropdown-leave-active {
    transition: opacity 0.3s ease, transform 0.3s ease;
}
.dropdown-enter-from, .dropdown-leave-to {
    opacity: 0;
    transform: translateY(-10px);
}
</style>
