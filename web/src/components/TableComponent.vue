<template>
  <div class="py-5">
    <!-- Table -->
    <div class="overflow-x-auto rounded-[8px] shadow-lg border border-[#99C4FF]">
      <table class="w-full">
        <!-- Table Header -->
        <thead class="bg-[#006AFF]">
          <tr>
            <th v-for="(header, index) in tableInfo.headers" :key="index"
              class="px-6 py-4 text-left text-base font-medium text-white border-b border-[#99C4FF]">
              {{ header.title }}
            </th>
          </tr>
        </thead>

        <!-- Table Body -->
        <div v-if="tableInfo.data.length == 0">
          <p class="p-5 text-lg font-semibold text-[#6C7F93]">Aucune information trouvée</p>
        </div>
        <tbody v-else class="bg-white">
          <tr v-for="(row, rowIndex) in tableInfo.data" :key="rowIndex"
            class="hover:bg-[#99C4FF]/10 transition-all duration-200">
            <td v-for="(header, colIndex) in tableInfo.headers" :key="colIndex"
              class="px-6 py-4 text-base text-[#060721] border-b border-[#99C4FF]/30">
              <div v-if="header.key != 'created_date'">{{ row[header.key] }}</div>
              <div v-if="header.key == 'created_date'">{{ convertDate(row[header.key]) }}</div>
              <div class="flex" v-if="header.key === 'actions'">
                <div v-for="(button, colIndex) in tableInfo.buttons" :key="colIndex" :class="{
                  'opacity-50 pointer-events-none': typeof button.disabled === 'function' ? button.disabled(row) : button.disabled
                }" class="flex px-2" @click="!isButtonDisabled(button, row) ? button.action(row) : null">
                  <div v-html="button.button"></div>
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="flex justify-center">
      <div class="mt-6 flex justify-between items-center w-1/2">
        <button :disabled="currentPage === 1" @click="changePage(currentPage - 1)"
          class="px-6 py-2.5 bg-[#006AFF] text-white rounded-[5px] hover:bg-[#006AFF]/90 disabled:bg-[#99C4FF] disabled:cursor-not-allowed transition-all duration-200 flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
          </svg>
          Précédent
        </button>

        <span class="text-[#6C7F93] font-medium">
          Page {{ currentPage }} sur {{ totalPages }}
        </span>

        <button :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)"
          class="px-6 py-2.5 bg-[#006AFF] text-white rounded-[5px] hover:bg-[#006AFF]/90 disabled:bg-[#99C4FF] disabled:cursor-not-allowed transition-all duration-200 flex items-center gap-2">
          Suivant
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import utils from '@/shared/utils';
export default {
  props: {
    tableInfo: {
      type: Object,
      required: true,
    },
    pageSize: {
      type: Number,
      default: 10,
    },
    currentPage: {
      type: Number,
      required: true,
    },
    totalPages: {
      type: Number,
      required: true,
    },
  },
  methods: {
    changePage(newPage) {
      this.$emit("page-changed", newPage);
    },
    convertDate(date) {
      if (typeof date != "string") {
        return utils.convertDate(date);
      }else {
        return date;
      }
    },
    isButtonDisabled(button, row) {
      return typeof button.disabled === 'function' ? button.disabled(row) : button.disabled;
    }
  },
};
</script>
